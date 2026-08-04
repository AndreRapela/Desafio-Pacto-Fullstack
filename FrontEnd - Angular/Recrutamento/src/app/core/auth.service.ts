import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'internal-recruitment-auth';
  private readonly session = signal<AuthResponse | null>(this.readSession());

  readonly user = computed(() => this.session());
  readonly isAuthenticated = computed(() => !!this.session()?.token);
  readonly isAdmin = computed(() => this.session()?.role === 'ADMIN');

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  login(payload: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', payload).pipe(tap(data => this.persist(data)));
  }

  register(payload: { name: string; email: string; password: string; companyStartDate: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/register', payload).pipe(tap(data => this.persist(data)));
  }

  token(): string | null { return this.session()?.token ?? null; }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.session.set(null);
    void this.router.navigateByUrl('/login');
  }

  private persist(data: AuthResponse): void {
    localStorage.setItem(this.storageKey, JSON.stringify(data));
    this.session.set(data);
  }

  private readSession(): AuthResponse | null {
    try {
      const value = localStorage.getItem(this.storageKey);
      return value ? JSON.parse(value) as AuthResponse : null;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
