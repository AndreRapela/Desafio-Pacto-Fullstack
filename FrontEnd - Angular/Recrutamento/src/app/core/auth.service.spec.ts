import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthResponse } from './models';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  const session: AuthResponse = {
    token: 'jwt-token',
    userId: 'user-1',
    name: 'Aluno 2',
    email: 'aluno2@pacto.com.br',
    role: 'CANDIDATE'
  };

  beforeEach(() => {
    localStorage.clear();
    router = jasmine.createSpyObj<Router>('Router', ['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: router }
      ]
    });

    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('starts unauthenticated without a stored session', () => {
    expect(service.isAuthenticated()).toBeFalse();
    expect(service.token()).toBeNull();
  });

  it('stores the session after login', () => {
    service.login({ email: session.email, password: 'Aluno2@123' }).subscribe();

    const request = http.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    request.flush(session);

    expect(service.isAuthenticated()).toBeTrue();
    expect(service.user()?.name).toBe('Aluno 2');
    expect(JSON.parse(localStorage.getItem('internal-recruitment-auth') ?? '{}')).toEqual(session);
  });

  it('clears the session and redirects on logout', () => {
    service.login({ email: session.email, password: 'Aluno2@123' }).subscribe();
    http.expectOne('/api/auth/login').flush(session);

    service.logout();

    expect(service.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('internal-recruitment-auth')).toBeNull();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
