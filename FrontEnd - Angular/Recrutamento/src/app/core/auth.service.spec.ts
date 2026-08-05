import { provideHttpClient } from "@angular/common/http";
import {
  HttpTestingController,
  provideHttpClientTesting,
} from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { AuthService } from "./auth.service";
import { AuthResponse } from "./models";

describe("AuthService", () => {
  let service: AuthService;
  let http: HttpTestingController;
  let navigateByUrl: ReturnType<typeof vi.fn>;

  const session: AuthResponse = {
    token: "jwt-token",
    userId: "user-1",
    name: "Candidato",
    email: "candidato@candidato.com",
    role: "CANDIDATE",
  };

  beforeEach(() => {
    localStorage.clear();
    navigateByUrl = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: Router,
          useValue: { navigateByUrl },
        },
      ],
    });

    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it("inicia sem autenticação quando não existe sessão salva", () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.token()).toBeNull();
  });

  it("salva a sessão após o login", () => {
    service
      .login({
        email: session.email,
        password: "candidato123",
      })
      .subscribe();

    const request = http.expectOne("/api/auth/login");

    expect(request.request.method).toBe("POST");

    request.flush(session);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.user()?.name).toBe("Candidato");

    expect(
      JSON.parse(
        localStorage.getItem("internal-recruitment-auth") ?? "{}",
      ),
    ).toEqual(session);
  });

  it("limpa a sessão e redireciona no logout", () => {
    service
      .login({
        email: session.email,
        password: "candidato123",
      })
      .subscribe();

    http.expectOne("/api/auth/login").flush(session);

    service.logout();

    expect(service.isAuthenticated()).toBe(false);
    expect(
      localStorage.getItem("internal-recruitment-auth"),
    ).toBeNull();

    expect(navigateByUrl).toHaveBeenCalledWith("/login");
  });
});