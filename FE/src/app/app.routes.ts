import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./view/launcher/launcher.component').then((m) => m.LauncherComponent),
  },
  {
    path: 'launcher',
    loadComponent: () =>
      import('./view/launcher/launcher.component').then((m) => m.LauncherComponent),
  },
  {
    path: 'gestione-stipendio',
    loadChildren: () =>
      import('./gestione-stipendio/gestione-stipendio.routes').then((m) => m.routes),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
