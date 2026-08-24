import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { EducationApiService } from './education-api.service';
import { Education } from './education.model';

@Component({
  selector: 'app-education-page',
  imports: [ReactiveFormsModule],
  templateUrl: './education-page.html',
  styleUrl: './education-page.css',
})
export class EducationPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(EducationApiService);

  protected readonly items = signal<Education[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly editingId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    institution: ['', Validators.required],
    course: ['', Validators.required],
    degree: [''],
    startDate: ['', Validators.required],
    endDate: [''],
    description: [''],
  });

  constructor() {
    this.reload();
  }

  private reload(): void {
    this.loading.set(true);
    this.api.list().subscribe((items) => {
      this.items.set(items);
      this.loading.set(false);
    });
  }

  protected edit(item: Education): void {
    this.editingId.set(item.id);
    this.form.setValue({
      institution: item.institution,
      course: item.course,
      degree: item.degree ?? '',
      startDate: item.startDate,
      endDate: item.endDate ?? '',
      description: item.description ?? '',
    });
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({ institution: '', course: '', degree: '', startDate: '', endDate: '', description: '' });
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
      institution: value.institution,
      course: value.course,
      degree: value.degree || null,
      startDate: value.startDate,
      endDate: value.endDate || null,
      description: value.description || null,
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

  protected remove(item: Education): void {
    if (!confirm(`Remover "${item.course}"?`)) {
      return;
    }
    this.api.delete(item.id).subscribe(() => this.reload());
  }
}
