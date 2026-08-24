import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { CertificationApiService } from './certification-api.service';
import { Certification } from './certification.model';

@Component({
  selector: 'app-certifications-page',
  imports: [ReactiveFormsModule],
  templateUrl: './certifications-page.html',
  styleUrl: './certifications-page.css',
})
export class CertificationsPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(CertificationApiService);

  protected readonly items = signal<Certification[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly editingId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    issuingOrganization: ['', Validators.required],
    issueDate: ['', Validators.required],
    expirationDate: [''],
    credentialUrl: [''],
    credentialId: [''],
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

  protected edit(item: Certification): void {
    this.editingId.set(item.id);
    this.form.setValue({
      name: item.name,
      issuingOrganization: item.issuingOrganization,
      issueDate: item.issueDate,
      expirationDate: item.expirationDate ?? '',
      credentialUrl: item.credentialUrl ?? '',
      credentialId: item.credentialId ?? '',
    });
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({
      name: '',
      issuingOrganization: '',
      issueDate: '',
      expirationDate: '',
      credentialUrl: '',
      credentialId: '',
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
      issuingOrganization: value.issuingOrganization,
      issueDate: value.issueDate,
      expirationDate: value.expirationDate || null,
      credentialUrl: value.credentialUrl || null,
      credentialId: value.credentialId || null,
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

  protected remove(item: Certification): void {
    if (!confirm(`Remover "${item.name}"?`)) {
      return;
    }
    this.api.delete(item.id).subscribe(() => this.reload());
  }
}
