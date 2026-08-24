import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { SkillApiService } from '../skills/skill-api.service';
import { Skill } from '../skills/skill.model';
import { ProjectApiService } from './project-api.service';
import { PROJECT_STATUSES, Project, ProjectStatus } from './project.model';

@Component({
  selector: 'app-projects-page',
  imports: [ReactiveFormsModule],
  templateUrl: './projects-page.html',
  styleUrl: './projects-page.css',
})
export class ProjectsPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ProjectApiService);
  private readonly skillApi = inject(SkillApiService);

  protected readonly statuses = PROJECT_STATUSES;
  protected readonly items = signal<Project[]>([]);
  protected readonly skills = signal<Skill[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly editingId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    slug: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]{3,100}$/)]],
    shortDescription: [''],
    fullDescription: [''],
    imageUrl: [''],
    githubUrl: [''],
    demoUrl: [''],
    date: [''],
    status: ['IN_PROGRESS' as ProjectStatus, Validators.required],
    featured: [false],
    technologyIds: this.fb.nonNullable.control<string[]>([]),
  });

  constructor() {
    this.reload();
    this.skillApi.list().subscribe((skills) => this.skills.set(skills));
  }

  private reload(): void {
    this.loading.set(true);
    this.api.list().subscribe((items) => {
      this.items.set(items);
      this.loading.set(false);
    });
  }

  protected toggleTechnology(skillId: string, checked: boolean): void {
    const current = this.form.controls.technologyIds.value;
    this.form.controls.technologyIds.setValue(
      checked ? [...current, skillId] : current.filter((id) => id !== skillId),
    );
  }

  protected isTechnologySelected(skillId: string): boolean {
    return this.form.controls.technologyIds.value.includes(skillId);
  }

  protected edit(item: Project): void {
    this.editingId.set(item.id);
    this.form.setValue({
      name: item.name,
      slug: item.slug,
      shortDescription: item.shortDescription ?? '',
      fullDescription: item.fullDescription ?? '',
      imageUrl: item.imageUrl ?? '',
      githubUrl: item.githubUrl ?? '',
      demoUrl: item.demoUrl ?? '',
      date: item.date ?? '',
      status: item.status,
      featured: item.featured,
      technologyIds: item.technologyIds,
    });
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({
      name: '',
      slug: '',
      shortDescription: '',
      fullDescription: '',
      imageUrl: '',
      githubUrl: '',
      demoUrl: '',
      date: '',
      status: 'IN_PROGRESS',
      featured: false,
      technologyIds: [],
    });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.errorMessage.set(null);
    const value = this.form.getRawValue();
    const request = {
      name: value.name,
      slug: value.slug,
      shortDescription: value.shortDescription || null,
      fullDescription: value.fullDescription || null,
      imageUrl: value.imageUrl || null,
      githubUrl: value.githubUrl || null,
      demoUrl: value.demoUrl || null,
      date: value.date || null,
      status: value.status,
      featured: value.featured,
      technologyIds: value.technologyIds,
    };
    const editingId = this.editingId();
    const request$ = editingId ? this.api.update(editingId, request) : this.api.create(request);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.cancelEdit();
        this.reload();
      },
      error: (error: unknown) => {
        this.errorMessage.set(extractErrorMessage(error));
        this.saving.set(false);
      },
    });
  }

  protected remove(item: Project): void {
    if (!confirm(`Remover o projeto "${item.name}"?`)) {
      return;
    }
    this.api.delete(item.id).subscribe(() => this.reload());
  }

  protected moveUp(index: number): void {
    if (index === 0) {
      return;
    }
    this.swapAndReorder(index, index - 1);
  }

  protected moveDown(index: number): void {
    if (index === this.items().length - 1) {
      return;
    }
    this.swapAndReorder(index, index + 1);
  }

  private swapAndReorder(indexA: number, indexB: number): void {
    const reordered = [...this.items()];
    [reordered[indexA], reordered[indexB]] = [reordered[indexB], reordered[indexA]];
    this.items.set(reordered);
    this.api.reorder(reordered.map((item) => item.id)).subscribe(() => this.reload());
  }

  protected statusLabel(status: ProjectStatus): string {
    return this.statuses.find((option) => option.value === status)?.label ?? status;
  }

  protected technologyNames(item: Project): string {
    return item.technologyIds
      .map((id) => this.skills().find((skill) => skill.id === id)?.name)
      .filter((name): name is string => !!name)
      .join(', ');
  }
}
