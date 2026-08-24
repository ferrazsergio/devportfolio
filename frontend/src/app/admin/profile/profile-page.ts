import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { extractErrorMessage } from '../../core/http/api-error';
import { ProfileApiService } from './profile-api.service';

@Component({
  selector: 'app-profile-page',
  imports: [ReactiveFormsModule],
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

  protected readonly form = this.fb.nonNullable.group({
    fullName: ['', Validators.required],
    username: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]{3,50}$/)]],
    headline: [''],
    bio: [''],
    location: [''],
    professionalEmail: ['', Validators.email],
    phone: [''],
    photoUrl: [''],
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
          photoUrl: profile.photoUrl ?? '',
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
        photoUrl: value.photoUrl || null,
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
}
