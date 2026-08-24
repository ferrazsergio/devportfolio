import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { SkillApiService } from './skill-api.service';
import { SKILL_CATEGORIES, Skill } from './skill.model';

@Component({
  selector: 'app-skills-page',
  imports: [ReactiveFormsModule],
  templateUrl: './skills-page.html',
  styleUrl: './skills-page.css',
})
export class SkillsPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(SkillApiService);

  protected readonly categories = SKILL_CATEGORIES;
  protected readonly skills = signal<Skill[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly editingId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    category: ['BACKEND' as Skill['category'], Validators.required],
  });

  constructor() {
    this.reload();
  }

  private reload(): void {
    this.loading.set(true);
    this.api.list().subscribe((skills) => {
      this.skills.set(skills);
      this.loading.set(false);
    });
  }

  protected edit(skill: Skill): void {
    this.editingId.set(skill.id);
    this.form.setValue({ name: skill.name, category: skill.category });
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({ name: '', category: 'BACKEND' });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.errorMessage.set(null);
    const value = this.form.getRawValue();
    const editingId = this.editingId();
    const request$ = editingId ? this.api.update(editingId, value) : this.api.create(value);

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

  protected remove(skill: Skill): void {
    if (!confirm(`Remover a habilidade "${skill.name}"?`)) {
      return;
    }
    this.api.delete(skill.id).subscribe(() => this.reload());
  }

  protected categoryLabel(category: Skill['category']): string {
    return this.categories.find((option) => option.value === category)?.label ?? category;
  }
}
