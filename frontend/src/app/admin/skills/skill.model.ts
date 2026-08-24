export type SkillCategory = 'BACKEND' | 'FRONTEND' | 'DATABASE' | 'CLOUD' | 'DEVOPS' | 'TOOLS' | 'OTHER';

export const SKILL_CATEGORIES: { value: SkillCategory; label: string }[] = [
  { value: 'BACKEND', label: 'Backend' },
  { value: 'FRONTEND', label: 'Frontend' },
  { value: 'DATABASE', label: 'Banco de Dados' },
  { value: 'CLOUD', label: 'Cloud' },
  { value: 'DEVOPS', label: 'DevOps' },
  { value: 'TOOLS', label: 'Ferramentas' },
  { value: 'OTHER', label: 'Outro' },
];

export interface Skill {
  id: string;
  name: string;
  category: SkillCategory;
}
