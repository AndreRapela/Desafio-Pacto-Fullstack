import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationStatus, CandidateApplication, Job, JobPayload, JobStatus, NotificationItem } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  jobs(term = '', status?: JobStatus): Observable<Job[]> {
    let params = new HttpParams();
    if (term.trim()) params = params.set('term', term.trim());
    if (status) params = params.set('status', status);
    return this.http.get<Job[]>('/api/jobs', { params });
  }

  job(id: string): Observable<Job> { return this.http.get<Job>(`/api/jobs/${id}`); }
  createJob(payload: JobPayload): Observable<Job> { return this.http.post<Job>('/api/jobs', payload); }
  updateJob(id: string, payload: JobPayload): Observable<Job> { return this.http.put<Job>(`/api/jobs/${id}`, payload); }
  deleteJob(id: string): Observable<void> { return this.http.delete<void>(`/api/jobs/${id}`); }
  apply(jobId: string): Observable<CandidateApplication> { return this.http.post<CandidateApplication>(`/api/applications/jobs/${jobId}`, {}); }
  myApplications(): Observable<CandidateApplication[]> { return this.http.get<CandidateApplication[]>('/api/applications/me'); }

  jobApplications(jobId: string, minMonths = 0): Observable<CandidateApplication[]> {
    const params = new HttpParams().set('minMonthsAtCompany', minMonths);
    return this.http.get<CandidateApplication[]>(`/api/applications/job/${jobId}`, { params });
  }

  updateApplication(id: string, status: ApplicationStatus, feedback: string): Observable<CandidateApplication> {
    return this.http.patch<CandidateApplication>(`/api/applications/${id}/status`, { status, feedback });
  }

  notifications(): Observable<NotificationItem[]> { return this.http.get<NotificationItem[]>('/api/notifications'); }
  markNotificationRead(id: string): Observable<NotificationItem> { return this.http.patch<NotificationItem>(`/api/notifications/${id}/read`, {}); }
}
