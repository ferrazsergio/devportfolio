import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { LandingComponent } from './landing/landing';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import { AdminShellComponent } from './admin/shell/admin-shell';
import { DashboardComponent } from './admin/dashboard/dashboard';
import { ProfilePageComponent } from './admin/profile/profile-page';
import { SkillsPageComponent } from './admin/skills/skills-page';
import { ExperiencesPageComponent } from './admin/experiences/experiences-page';
import { ProjectsPageComponent } from './admin/projects/projects-page';
import { EducationPageComponent } from './admin/education/education-page';
import { CertificationsPageComponent } from './admin/certifications/certifications-page';
import { SocialLinksPageComponent } from './admin/social-links/social-links-page';
import { PublicPortfolioComponent } from './public-portfolio/public-portfolio';

export const routes: Routes = [
  { path: '', component: LandingComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'admin',
    canActivate: [authGuard],
    component: AdminShellComponent,
    children: [
      { path: '', component: DashboardComponent },
      { path: 'profile', component: ProfilePageComponent },
      { path: 'skills', component: SkillsPageComponent },
      { path: 'experiences', component: ExperiencesPageComponent },
      { path: 'projects', component: ProjectsPageComponent },
      { path: 'education', component: EducationPageComponent },
      { path: 'certifications', component: CertificationsPageComponent },
      { path: 'social-links', component: SocialLinksPageComponent },
    ],
  },
  // Precisa ser a última: é um segmento coringa (username), colidiria com qualquer
  // rota fixa registrada depois dela.
  { path: ':username', component: PublicPortfolioComponent },
];
