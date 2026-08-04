import { DatePipe } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { finalize } from "rxjs";
import { ApiService } from "../../core/api.service";
import { errorMessage } from "../../core/error-message";
import { NotificationItem } from "../../core/models";

@Component({
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: "./notifications.html",
  styleUrl: "./notifications.css",
})
export class NotificationsComponent implements OnInit {
  readonly notifications = signal<NotificationItem[]>([]);
  readonly loading = signal(false);
  readonly error = signal("");

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set("");

    this.api
      .notifications()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (notifications) => {
          this.notifications.set(notifications);
        },
        error: (error) => {
          this.error.set(errorMessage(error));
        },
      });
  }

  preview(message: string): string {
    return message.length > 50
      ? `${message.slice(0, 50)}...`
      : message;
  }
}