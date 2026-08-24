export type ProjectStatus = 'IN_PROGRESS' | 'COMPLETED' | 'ARCHIVED';

export const PROJECT_STATUSES: { value: ProjectStatus; label: string }[] = [
  { value: 'IN_PROGRESS', label: 'Em andamento' },
  { value: 'COMPLETED', label: 'Concluído' },
  { value: 'ARCHIVED', label: 'Arquivado' },
];

export interface Project {
  id: string;
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
  technologyIds: string[];
}

export interface ProjectRequest {
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
  technologyIds: string[];
}
