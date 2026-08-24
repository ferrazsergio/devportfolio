import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { SocialLinkApiService } from './social-link-api.service';
import { SocialLink } from './social-link.model';

@Component({
  selector: 'app-social-links-page',
  imports: [ReactiveFormsModule],
  templateUrl: './social-links-page.html',
  styleUrl: './social-links-page.css',
})
export class SocialLinksPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(SocialLinkApiService);

  protected readonly items = signal<SocialLink[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly editingId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    platform: ['', Validators.required],
    url: ['', Validators.required],
    order: [0, Validators.required],
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

  protected edit(item: SocialLink): void {
    this.editingId.set(item.id);
    this.form.setValue({ platform: item.platform, url: item.url, order: item.order });
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.form.reset({ platform: '', url: '', order: this.items().length });
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

  protected remove(item: SocialLink): void {
    if (!confirm(`Remover o link de ${item.platform}?`)) {
      return;
    }
    this.api.delete(item.id).subscribe(() => this.reload());
  }
}
