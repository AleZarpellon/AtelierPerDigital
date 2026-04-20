import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

type LauncherCard = {
  title: string;
  subtitle: string;
  description: string;
  badge: string;
  icon: string;
  route: string | null;
  cta: string;
  accentClass: string;
  available: boolean;
};

@Component({
  selector: 'app-launcher',
  standalone: true,
  templateUrl: './launcher.component.html',
  styleUrl: './launcher.component.css',
})
export class LauncherComponent {
  private router = inject(Router);

  cards = signal<LauncherCard[]>([
    {
      title: 'Gestione Stipendio',
      subtitle: 'Budget personale',
      description: 'Apri il modulo attuale per resoconto, rate, spese, risparmio e salvadanaio.',
      badge: 'Attivo',
      icon: 'pi pi-wallet',
      route: '/gestione-stipendio',
      cta: 'Apri applicazione',
      accentClass: 'launcher-card--salary',
      available: true,
    },
    {
      title: 'Nuovo Applicativo',
      subtitle: 'Slot pronto',
      description:
        'Questa card e la struttura iniziale sono pronte per ospitare un secondo modulo desktop.',
      badge: 'Prossimamente',
      icon: 'pi pi-sparkles',
      route: null,
      cta: 'Aggiungi modulo',
      accentClass: 'launcher-card--next',
      available: false,
    },
  ]);

  openCard(route: string | null) {
    if (!route) {
      return;
    }

    this.router.navigateByUrl(route);
  }
}
