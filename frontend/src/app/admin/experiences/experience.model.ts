export interface Experience {
  id: string;
  company: string;
  role: string;
  description: string | null;
  startDate: string;
  endDate: string | null;
  current: boolean;
  location: string | null;
  order: number;
  technologyIds: string[];
}

export interface ExperienceRequest {
  company: string;
  role: string;
  description: string | null;
  startDate: string;
  endDate: string | null;
  current: boolean;
  location: string | null;
  technologyIds: string[];
}
