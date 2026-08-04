import { DatePipe } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import {
  ActivatedRoute,
  RouterLink,
} from "@angular/router";
import { finalize } from "rxjs";
import { ApiService } from "../../core/api.service";
import { errorMessage } from "../../core/error-message";
import { NotificationItem } from "../../core/models";

@Component({
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: "./notification-detail.html",
  styleUrl: "./notification-detail.css",
})
export class NotificationDetailComponent implements OnInit {
  readonly notification =
    signal<NotificationItem | null>(null);

  readonly loading = signal(false);
  readonly error = signal("");

  constructor(
    private readonly api: ApiService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get("id");

    if (!id) {
      this.error.set("Notificação não encontrada.");
      return;
    }

    this.loading.set(true);

    this.api
      .notification(id)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (notification) => {
          this.notification.set(notification);

          if (!notification.read) {
            this.markAsRead(notification.id);
          }
        },
        error: (error) =>
          this.error.set(errorMessage(error)),
      });
  }

  private markAsRead(id: string): void {
    this.api.markNotificationRead(id).subscribe({
      next: (notification) =>
        this.notification.set(notification),
      error: (error) =>
        this.error.set(errorMessage(error)),
    });
  }
}