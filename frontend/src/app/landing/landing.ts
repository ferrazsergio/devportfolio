import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LogoComponent } from '../core/ui/logo/logo';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, LogoComponent],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class LandingComponent {
  protected readonly githubUrl = 'https://github.com/ferrazsergio/devportfolio';
}
