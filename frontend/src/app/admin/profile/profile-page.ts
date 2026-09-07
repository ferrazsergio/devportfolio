import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '../../core/i18n/translate.pipe';
import { extractErrorMessage } from '../../core/http/api-error';
import { SpellcheckDirective } from '../../core/spellcheck/spellcheck.directive';
import { ProfileApiService } from './profile-api.service';

const MAX_PHOTO_SIZE_BYTES = 5 * 1024 * 1024;
const ALLOWED_PHOTO_TYPES = ['image/jpeg', 'image/png', 'image/webp'];

@Component({
  selector: 'app-profile-page',
  imports: [ReactiveFormsModule, TranslatePipe, SpellcheckDirective],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ProfileApiService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly savedMessage = signal(false);

  protected readonly profile = this.api.profile;
  protected readonly uploadingPhoto = signal(false);
  protected readonly photoError = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    username: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]{3,50}$/)]],
    headline: [''],
    bio: [''],
    location: [''],
    professionalEmail: ['', Validators.email],
    phone: [''],
    githubUrl: [''],
    linkedinUrl: [''],
    websiteUrl: [''],
  });

  constructor() {
    this.api.get().subscribe({
      next: (profile) => {
        this.form.patchValue({
          fullName: profile.fullName ?? '',
          username: profile.username ?? '',
          headline: profile.headline ?? '',
          bio: profile.bio ?? '',
          location: profile.location ?? '',
          professionalEmail: profile.professionalEmail ?? '',
          phone: profile.phone ?? '',
          githubUrl: profile.githubUrl ?? '',
          linkedinUrl: profile.linkedinUrl ?? '',
          websiteUrl: profile.websiteUrl ?? '',
        });
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.errorMessage.set(null);
    this.savedMessage.set(false);
    const value = this.form.getRawValue();
    this.api
      .update({
        ...value,
        headline: value.headline || null,
        bio: value.bio || null,
        location: value.location || null,
        professionalEmail: value.professionalEmail || null,
        phone: value.phone || null,
        githubUrl: value.githubUrl || null,
        linkedinUrl: value.linkedinUrl || null,
        websiteUrl: value.websiteUrl || null,
      })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.savedMessage.set(true);
        },
        error: (error: unknown) => {
          this.errorMessage.set(extractErrorMessage(error));
          this.saving.set(false);
        },
      });
  }

  protected onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file) {
      return;
    }

    if (!ALLOWED_PHOTO_TYPES.includes(file.type)) {
      this.photoError.set('Formato inválido. Envie uma imagem JPEG, PNG ou WebP.');
      return;
    }
    if (file.size > MAX_PHOTO_SIZE_BYTES) {
      this.photoError.set('Arquivo muito grande. O limite é 5MB.');
      return;
    }

    this.photoError.set(null);
    this.uploadingPhoto.set(true);
    this.api.uploadPhoto(file).subscribe({
      next: () => this.uploadingPhoto.set(false),
      error: (error: unknown) => {
        this.photoError.set(extractErrorMessage(error));
        this.uploadingPhoto.set(false);
      },
    });
  }

  protected removePhoto(): void {
    this.uploadingPhoto.set(true);
    this.photoError.set(null);
    this.api.removePhoto().subscribe({
      next: () => this.uploadingPhoto.set(false),
      error: (error: unknown) => {
        this.photoError.set(extractErrorMessage(error));
        this.uploadingPhoto.set(false);
      },
    });
  }
}
