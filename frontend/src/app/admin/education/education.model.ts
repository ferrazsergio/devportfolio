export interface Education {
  id: string;
  institution: string;
  course: string;
  degree: string | null;
  startDate: string;
  endDate: string | null;
  description: string | null;
}

export interface EducationRequest {
  institution: string;
  course: string;
  degree: string | null;
  startDate: string;
  endDate: string | null;
  description: string | null;
}
