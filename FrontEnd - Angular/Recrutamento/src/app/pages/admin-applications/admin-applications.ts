import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { errorMessage } from '../../core/error-message';
import { ApplicationStatus, CandidateApplication, Job } from '../../core/models';
import { finalize } from 'rxjs';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './admin-applications.html',
  styleUrl: './admin-applications.css',
})
export class AdminApplicationsComponent implements OnInit {
  readonly jobs = signal<Job[]>([]);
  readonly items = signal<CandidateApplication[]>([]);
  readonly error = signal('');
  readonly savingId = signal('');
  readonly savedApplicationIds = signal<Set<string>>(new Set());
  readonly message = signal('');
  selectedJob = '';
  minMonths = 0;

  constructor(private readonly api: ApiService) {}
  ngOnInit(): void {
    this.api.jobs('', undefined).subscribe({
      next: (jobs) => this.jobs.set(jobs),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }

  loadApplications(): void {
    this.error.set('');
    this.api.jobApplications(this.selectedJob, this.minMonths).subscribe({
      next: (data) => this.items.set(data),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }

  save(item: CandidateApplication): void {
    this.savingId.set(item.id);

    this.api
      .updateApplication(item.id, item.status as ApplicationStatus, item.feedback ?? '')
      .pipe(finalize(() => this.savingId.set('')))
      .subscribe({
        next: (updated) => this.handleSaved(updated as CandidateApplication),
        error: (error) => this.error.set(errorMessage(error)),
      });
  }

  private handleSaved(updated: CandidateApplication): void {
    this.items.update((items) => items.map((item) => (item.id === updated.id ? updated : item)));

    this.savedApplicationIds.update((ids) => new Set(ids).add(updated.id));

    this.message.set('Avaliação salva com sucesso.');
  }

  isSaved(id: string): boolean {
    return this.savedApplicationIds().has(id);
  }

  markAsPending(id: string): void {
    this.savedApplicationIds.update((ids) => {
      const updated = new Set(ids);
      updated.delete(id);
      return updated;
    });
  }
}
