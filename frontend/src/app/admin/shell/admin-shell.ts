import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LogoComponent } from '../../core/ui/logo/logo';
import { ThemeToggleComponent } from '../../core/ui/theme-toggle/theme-toggle';
import { LocaleToggleComponent } from '../../core/i18n/locale-toggle/locale-toggle';
import { TranslatePipe } from '../../core/i18n/translate.pipe';
import { UserMenuComponent } from './user-menu/user-menu';

@Component({
  selector: 'app-admin-shell',
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    LogoComponent,
    ThemeToggleComponent,
    LocaleToggleComponent,
    UserMenuComponent,
    TranslatePipe,
  ],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.css',
})
export class AdminShellComponent {}
