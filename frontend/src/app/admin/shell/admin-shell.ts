import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LogoComponent } from '../../core/ui/logo/logo';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-admin-shell',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, LogoComponent],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.css',
})
export class AdminShellComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly currentUser = this.auth.currentUser;

  protected logout(): void {
    this.auth.logout().subscribe(() => this.router.navigateByUrl('/login'));
  }
}
