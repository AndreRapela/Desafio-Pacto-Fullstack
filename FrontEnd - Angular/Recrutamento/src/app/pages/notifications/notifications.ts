import { Component, OnInit, signal } from "@angular/core";
import { DatePipe } from "@angular/common";
import { ApiService } from "../../core/api.service";
import { errorMessage } from "../../core/error-message";
import { NotificationItem } from "../../core/models";

@Component({
  standalone: true,
  imports: [DatePipe],
  templateUrl: "./notifications.html",
  styleUrl: "./notifications.css",
})
export class NotificationsComponent implements OnInit {
  readonly items = signal<NotificationItem[]>([]);
  readonly error = signal("");
  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api
      .notifications()
      .subscribe({
        next: (data) => this.items.set(data),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }
  markRead(item: NotificationItem): void {
    this.api
      .markNotificationRead(item.id)
      .subscribe({
        next: (updated) =>
          this.items.update((items) =>
            items.map((current) =>
              current.id === updated.id ? updated : current,
            ),
          ),
      });
  }
}
