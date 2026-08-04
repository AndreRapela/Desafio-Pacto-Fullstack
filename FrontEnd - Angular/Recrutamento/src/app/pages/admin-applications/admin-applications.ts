import { Component, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { DatePipe } from "@angular/common";
import { ApiService } from "../../core/api.service";
import { errorMessage } from "../../core/error-message";
import {
  ApplicationStatus,
  CandidateApplication,
  Job,
} from "../../core/models";

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: "./admin-applications.html",
  styleUrl: "./admin-applications.css",
})
export class AdminApplicationsComponent implements OnInit {
  readonly jobs = signal<Job[]>([]);
  readonly items = signal<CandidateApplication[]>([]);
  readonly error = signal("");
  selectedJob = "";
  minMonths = 0;

  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api
      .jobs("", undefined)
      .subscribe({
        next: (jobs) => this.jobs.set(jobs),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }

  loadApplications(): void {
    this.error.set("");
    this.api
      .jobApplications(this.selectedJob, this.minMonths)
      .subscribe({
        next: (data) => this.items.set(data),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }

  save(item: CandidateApplication): void {
    this.api
      .updateApplication(
        item.id,
        item.status as ApplicationStatus,
        item.feedback ?? "",
      )
      .subscribe({
        next: (updated) =>
          this.items.update((items) =>
            items.map((current) =>
              current.id === updated.id ? updated : current,
            ),
          ),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }
}
