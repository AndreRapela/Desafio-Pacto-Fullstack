import { Component, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { finalize } from "rxjs";
import { AuthService } from "../../core/auth.service";
import { errorMessage } from "../../core/error-message";

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: "./login.html",
  styleUrl: "./login.css",
})
export class LoginComponent {
  readonly loading = signal(false);
  readonly error = signal("");
  readonly form;

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {
    this.form = this.fb.nonNullable.group({
      email: ["", [Validators.required, Validators.email]],
      password: ["", Validators.required],
    });
  }

  fill(email: string, password: string): void {
    this.form.setValue({ email, password });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set("");
    this.auth
      .login(this.form.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => void this.router.navigateByUrl("/vagas"),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }
}
