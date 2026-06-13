const BASE = "http://localhost:8080";

async function post<T>(path: string, body: unknown, params?: Record<string, string>): Promise<T> {
  const url = new URL(BASE + path);
  if (params) Object.entries(params).forEach(([k, v]) => url.searchParams.set(k, v));
  const res = await fetch(url.toString(), {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  return res.json() as Promise<T>;
}

// ------------------------------------------------------------------
// Shared types
// ------------------------------------------------------------------

export type VerificationStatus = "UNVERIFIED" | "VOUCHED" | "FULLY_VERIFIED";
export type DonorType = "INDIVIDUAL" | "CORPORATE" | "INSTITUTIONAL";
export type FitLabel = "Strong fit" | "Good fit" | "Complementary fit";

// ------------------------------------------------------------------
// Auth
// ------------------------------------------------------------------

export type AuthUser = { id: number; name: string; role: "DONOR" | "ORGANIZATION"; email: string };

export type DonorRegistrationRequest = {
  name: string;
  email: string;
  donorType: DonorType;
  preferredRegions: string[];
  preferredEnergyFocus: string[];
  minGivingCapacityEur: number | null;
  maxGivingCapacityEur: number | null;
  requiresVouchedOnly: boolean;
};

export type OrgRegistrationRequest = {
  name: string;
  contactEmail: string;
  organizationType: "NGO" | "RLO";
  description: string;
};

export function login(email: string): Promise<AuthUser> {
  return post<AuthUser>("/api/auth/login", { email, password: "" });
}

export function registerDonor(data: DonorRegistrationRequest): Promise<AuthUser> {
  return post<AuthUser>("/api/auth/register/donor", data);
}

export function registerOrganization(data: OrgRegistrationRequest): Promise<AuthUser> {
  return post<AuthUser>("/api/auth/register/organization", data);
}

// ------------------------------------------------------------------
// Donor → Projects  (blind search)
// ------------------------------------------------------------------

export type DonorMatchRequest = {
  preferredRegions: string[];
  preferredEnergyFocus: string[];
  minGivingCapacityEur: number | null;
  maxGivingCapacityEur: number | null;
  requiresVouchedOnly: boolean;
};

export type MatchResult = {
  projectId: number;
  projectTitle: string;
  organizationId: number;
  organizationName: string;
  oneSentenceMission: string;
  displayLocation: string;
  energyFocusTags: string[];
  targetAmountEur: number;
  currentFundingAmountEur: number;
  verificationStatus: VerificationStatus;
  vouchedBy: string[];
  fitLabel: FitLabel;
  matchReason: string;
};

export function findProjects(criteria: DonorMatchRequest): Promise<MatchResult[]> {
  return post<MatchResult[]>("/api/match/projects", criteria);
}

// ------------------------------------------------------------------
// RLO → Donors
// ------------------------------------------------------------------

export type ProjectMatchRequest = {
  location: string;
  energyFocusTags: string[];
  targetAmountEur: number | null;
  isVouched: boolean;
};

export type DonorMatchResult = {
  donorId: number;
  donorName: string;
  donorType: DonorType;
  preferredRegions: string[];
  preferredEnergyFocus: string[];
  minGivingCapacityEur: number;
  maxGivingCapacityEur: number;
  fitLabel: FitLabel;
  matchReason: string;
};

export function findDonors(criteria: ProjectMatchRequest): Promise<DonorMatchResult[]> {
  return post<DonorMatchResult[]>("/api/match/donors", criteria);
}

// ------------------------------------------------------------------
// Portfolio suggestion
// ------------------------------------------------------------------

export type PortfolioAllocation = {
  projectId: number;
  projectTitle: string;
  organizationName: string;
  displayLocation: string;
  suggestedAmountEur: number;
  reason: string;
};

export type PortfolioSuggestion = {
  totalDonationEur: number;
  allocations: PortfolioAllocation[];
  summary: string;
};

export function suggestPortfolio(criteria: DonorMatchRequest, totalDonation: number): Promise<PortfolioSuggestion> {
  return post<PortfolioSuggestion>("/api/match/portfolio", criteria, {
    totalDonation: String(totalDonation),
  });
}

// ------------------------------------------------------------------
// AI Co-Pilot  (POST /api/onboard)
// ------------------------------------------------------------------

export type OnboardRequest = { rawNotes: string; language?: string };
export type OnboardResponse = {
  profileName: string;
  problemStatement: string;
  beneficiaries: string;
  budget: string;
  sdgTags: string[];
};

export function polishNotes(data: OnboardRequest): Promise<OnboardResponse> {
  return post<OnboardResponse>("/api/onboard", data);
}

// ------------------------------------------------------------------
// Publish a project (closes the loop: co-pilot profile -> matchable project)
// ------------------------------------------------------------------

export type CreateProjectRequest = {
  title: string;
  aiPolishedDescription: string;
  rawInputWhatsapp: string;
  energyFocusTags: string[];
  targetAmountEur: number | null;
  displayLocation: string;
  fundingDeadline: string | null;
};

export type Project = {
  id: number;
  organizationId: number;
  title: string;
  aiPolishedDescription: string;
  energyFocusTags: string[];
  targetAmountEur: number;
  currentFundingAmountEur: number;
  displayLocation: string;
  fundingDeadline: string | null;
};

export function createProject(organizationId: number, data: CreateProjectRequest): Promise<Project> {
  return post<Project>(`/api/organizations/${organizationId}/projects`, data);
}

export async function getOrganizationProjects(organizationId: number): Promise<Project[]> {
  const res = await fetch(`${BASE}/api/organizations/${organizationId}/projects`);
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  return res.json() as Promise<Project[]>;
}
