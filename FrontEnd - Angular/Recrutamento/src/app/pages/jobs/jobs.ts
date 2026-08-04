import { Component, OnInit, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { finalize } from "rxjs";
import { ApiService } from "../../core/api.service";
import { AuthService } from "../../core/auth.service";
import { errorMessage } from "../../core/error-message";
import { Job, JobStatus } from "../../core/models";

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: "./jobs.html",
  styleUrl: "./jobs.css",
})
export class JobsComponent implements OnInit {
  readonly jobs = signal<Job[]>([]);
  readonly loading = signal(false);
  readonly applyingId = signal("");
  readonly error = signal("");
  readonly message = signal("");
  term = "";
  status: "" | JobStatus = "OPEN";

  constructor(
    readonly auth: AuthService,
    private readonly api: ApiService,
  ) {}
  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set("");
    this.api
      .jobs(this.term, this.status || undefined)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (jobs) => this.jobs.set(jobs),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }

  apply(job: Job): void {
    this.applyingId.set(job.id);
    this.error.set("");
    this.message.set("");
    this.api
      .apply(job.id)
      .pipe(finalize(() => this.applyingId.set("")))
      .subscribe({
        next: () =>
          this.message.set(
            `Candidatura para “${job.title}” enviada com sucesso.`,
          ),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }

  remove(job: Job): void {
    if (!confirm(`Excluir a vaga “${job.title}”?`)) return;
    this.api.deleteJob(job.id).subscribe({
      next: () => {
        this.jobs.update((items) => items.filter((item) => item.id !== job.id));
        this.message.set("Vaga excluída.");
      },
      error: (error) => this.error.set(errorMessage(error)),
    });
  }
}
