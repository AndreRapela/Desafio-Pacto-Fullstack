import { Component, OnInit, signal } from "@angular/core";
import { DatePipe } from "@angular/common";
import { ApiService } from "../../core/api.service";
import { errorMessage } from "../../core/error-message";
import { CandidateApplication } from "../../core/models";

@Component({
  standalone: true,
  imports: [DatePipe],
  templateUrl: "./my-applications.html",
  styleUrl: "./my-applications.css",
})
export class MyApplicationsComponent implements OnInit {
  readonly items = signal<CandidateApplication[]>([]);
  readonly error = signal("");
  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api
      .myApplications()
      .subscribe({
        next: (data) => this.items.set(data),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }
  label(status: string): string {
    return (
      (
        {
          SUBMITTED: "Enviada",
          UNDER_REVIEW: "Em análise",
          APPROVED: "Aprovada",
          REJECTED: "Reprovada",
        } as Record<string, string>
      )[status] ?? status
    );
  }
}
