import { Component, ElementRef, HostListener, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';
import { ProfileApiService } from '../../profile/profile-api.service';

@Component({
  selector: 'app-user-menu',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './user-menu.html',
  styleUrl: './user-menu.css',
})
export class UserMenuComponent {
  private readonly auth = inject(AuthService);
  private readonly profileApi = inject(ProfileApiService);
  private readonly router = inject(Router);
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  protected readonly currentUser = this.auth.currentUser;
  protected readonly open = signal(false);

  protected readonly photoUrl = computed(() => this.profileApi.profile()?.photoUrl ?? null);
  protected readonly initials = computed(() => {
    const name = this.currentUser()?.name ?? '';
    return name
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase())
      .join('');
  });

  constructor() {
    if (!this.profileApi.profile()) {
      this.profileApi.get().subscribe({ error: () => undefined });
    }
  }

  protected toggle(): void {
    this.open.update((value) => !value);
  }

  protected logout(): void {
    this.auth.logout().subscribe(() => this.router.navigateByUrl('/login'));
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (this.open() && !this.elementRef.nativeElement.contains(event.target as Node)) {
      this.open.set(false);
    }
  }
}
