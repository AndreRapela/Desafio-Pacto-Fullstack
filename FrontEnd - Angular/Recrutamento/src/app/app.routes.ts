import { Routes } from "@angular/router";
import {  adminGuard, authGuard, candidateGuard } from "./core/guards";

export const routes: Routes = [
  {
    path: "login",
    loadComponent: () =>
      import("./pages/login/login").then((m) => m.LoginComponent),
  },
  {
    path: "cadastro",
    loadComponent: () =>
      import("./pages/register/register").then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: "vagas",
    canActivate: [authGuard],
    loadComponent: () =>
      import("./pages/jobs/jobs").then((m) => m.JobsComponent),
  },
  {
    path: "candidaturas",
    canActivate: [candidateGuard],
    loadComponent: () =>
      import("./pages/my-applications/my-applications").then(
        (m) => m.MyApplicationsComponent,
      ),
  },
  {
    path: "notificacoes",
    canActivate: [authGuard],
    loadComponent: () =>
      import("./pages/notifications/notifications").then(
        (m) => m.NotificationsComponent,
      ),
  },
  {
  path: "notificacoes/:id",
  canActivate: [authGuard],
  loadComponent: () =>
    import(
      "./pages/notification-detail/notification-detail"
    ).then((m) => m.NotificationDetailComponent),
},
  {
    path: "admin/vagas/nova",
    canActivate: [adminGuard],
    loadComponent: () =>
      import("./pages/job-form/job-form").then(
        (m) => m.JobFormComponent,
      ),
  },
  {
    path: "admin/vagas/:id",
    canActivate: [adminGuard],
    loadComponent: () =>
      import("./pages/job-form/job-form").then(
        (m) => m.JobFormComponent,
      ),
  },
  {
    path: "admin/candidatos",
    canActivate: [adminGuard],
    loadComponent: () =>
      import("./pages/admin-applications/admin-applications").then(
        (m) => m.AdminApplicationsComponent,
      ),
  },
  
  { path: "", pathMatch: "full", redirectTo: "vagas" },
  { path: "**", redirectTo: "vagas" },
];
