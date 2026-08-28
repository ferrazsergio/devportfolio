import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LogoComponent } from '../../core/ui/logo/logo';
import { ThemeToggleComponent } from '../../core/ui/theme-toggle/theme-toggle';
import { UserMenuComponent } from './user-menu/user-menu';

@Component({
  selector: 'app-admin-shell',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, LogoComponent, ThemeToggleComponent, UserMenuComponent],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.css',
})
export class AdminShellComponent {}
