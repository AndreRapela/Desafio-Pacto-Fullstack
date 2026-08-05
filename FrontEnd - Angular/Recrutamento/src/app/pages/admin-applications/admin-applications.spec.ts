import { TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import {
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { ApiService } from "../../core/api.service";
import {
  ApplicationStatus,
  CandidateApplication,
} from "../../core/models";
import { AdminApplicationsComponent } from "./admin-applications";

describe("AdminApplicationsComponent", () => {
  let component: AdminApplicationsComponent;
  let updateApplication: ReturnType<typeof vi.fn>;

  const application = {
    id: "application-1",
    jobId: "job-1",
    jobTitle: "Desenvolvedor Full Stack",
    candidateId: "candidate-1",
    candidateName: "Candidato",
    candidateEmail: "candidato@candidato.com",
    monthsAtCompany: 24,
    status: "SUBMITTED",
    feedback: "",
    createdAt: "2026-08-04T19:59:00",
  } as CandidateApplication;

  beforeEach(() => {
    updateApplication = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        AdminApplicationsComponent,
        {
          provide: ApiService,
          useValue: { updateApplication },
        },
      ],
    });

    component = TestBed.inject(
      AdminApplicationsComponent,
    );
  });

  it("salva a avaliação e marca a candidatura como salva", () => {
    const updated = {
      ...application,
      status: "APPROVED" as ApplicationStatus,
      feedback: "Perfil aprovado",
    };

    component.items.set([application]);
    updateApplication.mockReturnValue(of(updated));

    component.save(updated);

    expect(updateApplication).toHaveBeenCalledWith(
      updated.id,
      "APPROVED",
      "Perfil aprovado",
    );

    expect(component.items()).toEqual([updated]);

    expect(component.message()).toBe(
      "Avaliação salva com sucesso.",
    );

    expect(component.isSaved(updated.id)).toBe(true);
    expect(component.savingId()).toBe("");
  });

  it("reativa o botão quando a avaliação é alterada", () => {
    component.savedApplicationIds.set(
      new Set([application.id]),
    );

    component.markAsPending(application.id);

    expect(component.isSaved(application.id)).toBe(false);
  });
});