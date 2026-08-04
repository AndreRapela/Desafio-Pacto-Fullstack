import { Component, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { finalize } from "rxjs";
import { ApiService } from "../../core/api.service";
import { errorMessage } from "../../core/error-message";
import { JobPayload } from "../../core/models";

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: "./job-form.html",
  styleUrl: "./job-form.css",
})
export class JobFormComponent implements OnInit {
  readonly loading = signal(false);
  readonly error = signal("");
  readonly jobId: string | null;
  readonly form;

  constructor(
    private readonly fb: FormBuilder,
    private readonly api: ApiService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {
    this.jobId = this.route.snapshot.paramMap.get("id");
    this.form = this.fb.nonNullable.group({
      title: ["", Validators.required],
      description: ["", Validators.required],
      requirements: ["", Validators.required],
      minMonthsAtCompany: [0, [Validators.required, Validators.min(0)]],
      status: ["OPEN", Validators.required],
    });
  }

  ngOnInit(): void {
    if (!this.jobId) return;
    this.loading.set(true);
    this.api
      .job(this.jobId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (job) =>
          this.form.setValue({
            title: job.title,
            description: job.description,
            requirements: job.requirements,
            minMonthsAtCompany: job.minMonthsAtCompany,
            status: job.status,
          }),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }

  save(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set("");
    const payload = this.form.getRawValue() as JobPayload;
    const request = this.jobId
      ? this.api.updateJob(this.jobId, payload)
      : this.api.createJob(payload);
    request.pipe(finalize(() => this.loading.set(false))).subscribe({
      next: () => void this.router.navigateByUrl("/vagas"),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }
}
