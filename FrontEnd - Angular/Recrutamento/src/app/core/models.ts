export type Role = "ADMIN" | "CANDIDATE";
export type JobStatus = "OPEN" | "CLOSED";
export type ApplicationStatus =
  | "SUBMITTED"
  | "UNDER_REVIEW"
  | "APPROVED"
  | "REJECTED";

export interface AuthResponse {
  token: string;
  userId: string;
  name: string;
  email: string;
  role: Role;
}

export interface Job {
  id: string;
  title: string;
  description: string;
  requirements: string;
  minCompanyTime: number;
  status: JobStatus;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
  eligible: boolean;
}

export interface JobPayload {
  title: string;
  description: string;
  requirements: string;
  minMonthsAtCompany: number;
  status: JobStatus;
}

export interface CandidateApplication {
  id: string;
  jobId: string;
  jobTitle: string;
  candidateId: string;
  candidateName: string;
  candidateEmail: string;
  companyStartDate: string;
  monthsAtCompany: number;
  status: ApplicationStatus;
  feedback?: string;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}
