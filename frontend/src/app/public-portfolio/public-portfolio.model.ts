export type SkillCategory = 'BACKEND' | 'FRONTEND' | 'DATABASE' | 'CLOUD' | 'DEVOPS' | 'TOOLS' | 'OTHER';

export type ProjectStatus = 'IN_PROGRESS' | 'COMPLETED' | 'ARCHIVED';

export interface PublicSkill {
  name: string;
  category: SkillCategory;
}

export interface PublicSocialLink {
  platform: string;
  url: string;
  order: number;
}

export interface PublicExperience {
  company: string;
  role: string;
  description: string | null;
  startDate: string;
  endDate: string | null;
  current: boolean;
  location: string | null;
  order: number;
  technologies: PublicSkill[];
}

export interface PublicProject {
  name: string;
  slug: string;
  shortDescription: string | null;
  fullDescription: string | null;
  imageUrl: string | null;
  githubUrl: string | null;
  demoUrl: string | null;
  date: string | null;
  status: ProjectStatus;
  featured: boolean;
  order: number;
  technologies: PublicSkill[];
}

export interface PublicEducation {
  institution: string;
  course: string;
  degree: string | null;
  startDate: string;
  endDate: string | null;
  description: string | null;
}

export interface PublicCertification {
  name: string;
  issuingOrganization: string;
  issueDate: string;
  expirationDate: string | null;
  credentialUrl: string | null;
  credentialId: string | null;
}

export interface PublicProfile {
  fullName: string | null;
  headline: string | null;
  bio: string | null;
  location: string | null;
  professionalEmail: string | null;
  phone: string | null;
  githubUrl: string | null;
  linkedinUrl: string | null;
  websiteUrl: string | null;
  photoUrl: string | null;
  socialLinks: PublicSocialLink[];
}

export interface PublicPortfolio {
  profile: PublicProfile;
  experiences: PublicExperience[];
  projects: PublicProject[];
  skills: PublicSkill[];
  educations: PublicEducation[];
  certifications: PublicCertification[];
}
