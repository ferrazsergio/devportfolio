import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { SkillApiService } from '../skills/skill-api.service';
import { Skill } from '../skills/skill.model';
import { ExperienceApiService } from './experience-api.service';
import { Experience } from './experience.model';

@Component({
  selector: 'app-experiences-page',
  imports: [ReactiveFormsModule],
  templateUrl: './experiences-page.html',
  styleUrl: './experiences-page.css',
})
export class ExperiencesPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ExperienceApiService);
  private readonly skillApi = inject(SkillApiService);

  protected readonly items = signal<Experience[]>([]);
  protected readonly skills = signal<Skill[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly editingId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    company: ['', Validators.required],
    role: ['', Validators.required],
    description: [''],
    startDate: ['', Validators.required],
    endDate: [''],
    current: [false],
    location: [''],
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

  protected edit(item: Experience): void {
    this.editingId.set(item.id);
    this.form.setValue({
      company: item.company,
      role: item.role,
      description: item.description ?? '',
      startDate: item.startDate,
      endDate: item.endDate ?? '',
      current: item.current,
      location: item.location ?? '',
      technologyIds: item.technologyIds,
    });
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({
      company: '',
      role: '',
      description: '',
      startDate: '',
      endDate: '',
      current: false,
      location: '',
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
      company: value.company,
      role: value.role,
      description: value.description || null,
      startDate: value.startDate,
      endDate: value.current ? null : value.endDate || null,
      current: value.current,
      location: value.location || null,
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

  protected remove(item: Experience): void {
    if (!confirm(`Remover a experiência em "${item.company}"?`)) {
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

  protected technologyNames(item: Experience): string {
    return item.technologyIds
      .map((id) => this.skills().find((skill) => skill.id === id)?.name)
      .filter((name): name is string => !!name)
      .join(', ');
  }
}
