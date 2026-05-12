import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Button } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { ToastService } from '../../../shared/toast.service';
import { RateService } from '../../services/rate.service';
import { RateModel } from '../../models/rate.model';
import { TooltipModule } from 'primeng/tooltip';
import {
  positiveEuroValidator,
  positiveIntValidator,
} from '../../../shared/validators/euro.validator';
import { SelectButtonModule } from 'primeng/selectbutton';

@Component({
  selector: 'app-rate.component',
  imports: [
    CommonModule,
    TableModule,
    CardModule,
    ReactiveFormsModule,
    FormsModule,
    InputText,
    ToggleSwitchModule,
    Button,
    TooltipModule,
    SelectButtonModule,
  ],
  templateUrl: './rate.component.html',
  styleUrl: './rate.component.css',
  standalone: true,
})
export class RateComponent implements OnInit {
  private fb = inject(FormBuilder);
  private rateService = inject(RateService);
  private toastService = inject(ToastService);
  checkTipeForm = signal<string>('');
  rateList = signal<RateModel[]>([]);
  cols = signal<string[]>([
    'descrizione',
    'euro',
    'rate',
    'maxValore',
    'periodo',
    'disattivo/attivo',
    'azioni',
  ]);
  sceltaQuantitavo = signal<string>('nessuna');
  quotaOld = signal<number>(0);

  form = signal<FormGroup>(
    this.fb.group({
      idRate: [null],
      descrizione: ['', Validators.required],
      euro: [null, [Validators.required, positiveEuroValidator()]],
      nrRate: [null, positiveIntValidator()],
      nrRateMax: [null, positiveIntValidator()],
      maxValore: [null, positiveEuroValidator()],
      periodo: ['', [Validators.required]],
      mese: [null],
      attivo: [true],
    }),
  );
  totale = computed<number>(() =>
    this.rateList()
      .filter((r) => r.attivo)
      .reduce((acc, s) => acc + (Number(s.euro) || 0), 0),
  );
  totaleInizioMese = computed<number>(() =>
    this.rateList()
      .filter((r) => r.periodo === 'Inizio mese' && r.attivo)
      .reduce((acc, r) => acc + (Number(r.euro) || 0), 0),
  );

  totaleVicinoStipendio = computed<number>(() =>
    this.rateList()
      .filter((r) => r.periodo === 'Vicino stipendio' && r.attivo)
      .reduce((acc, r) => acc + (Number(r.euro) || 0), 0),
  );

  totaleAnnuali = computed<number>(() =>
    this.rateList()
      .filter((r) => r.periodo === 'Annuale' && r.attivo)
      .reduce((acc, r) => acc + (Number(r.euro) || 0), 0),
  );

  periodoOptions = [
    { label: 'Inizio mese', value: 'Inizio mese' },
    { label: 'Vicino stipendio', value: 'Vicino stipendio' },
    { label: 'Annuale', value: 'Annuale' },
  ];
  meseOptions = [
    { label: 'Gennaio', value: 'Gennaio' },
    { label: 'Febbraio', value: 'Febbraio' },
    { label: 'Marzo', value: 'Marzo' },
    { label: 'Aprile', value: 'Aprile' },
    { label: 'Maggio', value: 'Maggio' },
    { label: 'Giugno', value: 'Giugno' },
    { label: 'Luglio', value: 'Luglio' },
    { label: 'Agosto', value: 'Agosto' },
    { label: 'Settembre', value: 'Settembre' },
    { label: 'Ottobre', value: 'Ottobre' },
    { label: 'Novembre', value: 'Novembre' },
    { label: 'Dicembre', value: 'Dicembre' },
  ];
  sceltaQuantitavoOptions = [
    { label: 'Nessuna', value: 'nessuna' },
    { label: 'Rate', value: 'rate' },
  ];

  ngOnInit(): void {
    this.loadRate();
    this.form().get('maxValore')?.disable();

    this.form()
      .get('nrRateMax')
      ?.valueChanges.subscribe(() => this.updateMaxValore());
    this.form()
      .get('nrRate')
      ?.valueChanges.subscribe(() => this.updateMaxValore());

    // quando cambia euro: prima ricalcola nrRateMax (solo modifica), poi aggiorna maxValore
    this.form()
      .get('euro')
      ?.valueChanges.subscribe(() => {
        this.updateNrRateMax();
        if (this.checkTipeForm() === 'modifica') return;
        this.updateMaxValore();
      });

    this.form()
      .get('periodo')
      ?.valueChanges.subscribe((periodo) => this.applyPeriodoRules(periodo));
  }

  applyPeriodoRules(periodo: string | null) {
    const meseControl = this.form().get('mese');
    if (!meseControl) return;

    if (periodo === 'Annuale') {
      meseControl.setValidators([Validators.required]);
      this.sceltaQuantitavo.set('nessuna');
      this.form().patchValue(
        {
          nrRate: null,
          nrRateMax: null,
          maxValore: null,
        },
        { emitEvent: false },
      );
    } else {
      meseControl.clearValidators();
      meseControl.setValue(null, { emitEvent: false });
    }
    meseControl.updateValueAndValidity({ emitEvent: false });
  }

  updateMaxValore() {
    const nrRateMax = Number(this.form().get('nrRateMax')?.value) || 0;
    const nrRate = Number(this.form().get('nrRate')?.value) || 0;
    const euro = Number(this.form().get('euro')?.value) || 0;

    if (nrRateMax && nrRate != null && euro) {
      const result = (nrRateMax - nrRate) * euro;
      this.form()
        .get('maxValore')
        ?.setValue(result > 0 ? result : 0, { emitEvent: false });
    }
  }

  updateNrRateMax() {
    if (this.checkTipeForm() !== 'modifica') return;

    const nrRate = Number(this.form().get('nrRate')?.value) || 0;
    const nrRateMax = Number(this.form().get('nrRateMax')?.value) || 0;
    const euro = Number(this.form().get('euro')?.value) || 0;

    if (nrRate != null && euro && nrRateMax) {
      const nrRate = Number(this.form().get('nrRate')?.value) || 0;
      const euro = Number(this.form().get('euro')?.value) || 0;

      if (euro && this.quotaOld()) {
        const nuovoNrRateMax = Math.ceil(this.quotaOld() / euro);
        this.form().get('nrRateMax')?.setValue(nuovoNrRateMax, { emitEvent: false });
      }
    }
  }

  onAnnullaOrNew(isNew: boolean) {
    if (isNew) this.checkTipeForm.set('nuovo');
    else this.checkTipeForm.set('');

    // resetta il form per una nuova spesa
    this.form().reset({
      idRate: null,
      descrizione: '',
      euro: null,
      nrRate: null,
      nrRateMax: null,
      maxValore: null,
      periodo: '',
      mese: null,
      attivo: true,
    });
    this.sceltaQuantitavo.set('nessuna');
  }

  onDelete(idRate: number) {
    this.rateService.deleteRate(idRate).subscribe({
      next: (res) => {
        if (res.success) {
          this.toastService.show('success', 'Successo!', res.message);
          this.rateList.update((list) => list.filter((s) => s.idRate !== idRate));
        }
      },
      error: (err) => {
        this.toastService.showErrorHttp(err.message);
        console.error(err);
      },
    });
  }

  onEdit(objRate: RateModel) {
    this.checkTipeForm.set('modifica');
    this.form().patchValue({
      ...objRate,
      attivo: objRate.attivo ?? true,
      mese: objRate.mese ?? null,
    });
    this.applyPeriodoRules(objRate.periodo);
    this.gestioneSceltaQuantitativo(objRate);
    if (objRate.maxValore) this.quotaOld.set(objRate.maxValore);
  }

  onRipristina() {
    const objRate = this.rateList().find((s) => s.idRate === this.form().get('idRate')?.value);
    if (objRate) {
      this.form().patchValue({
        ...objRate,
        attivo: objRate.attivo ?? true,
        mese: objRate.mese ?? null,
      });
      this.applyPeriodoRules(objRate.periodo);

      this.gestioneSceltaQuantitativo(objRate);
    }
  }

  onToggleAttivo(row: RateModel, value: boolean) {
    if (!row.idRate) return;

    this.rateService.updateAttivo(row.idRate, value).subscribe({
      next: (res) => {
        if (res.success) {
          this.rateList.update((list) =>
            this.sortByPeriodo(
              list.map((s) => (s.idRate === row.idRate ? { ...s, attivo: value } : s)),
            ),
          );
          this.toastService.show('success', 'Successo!', res.message);
        }
      },
      error: (err) => {
        this.toastService.showErrorHttp(err.error?.message || err.message);
        console.error(err);
      },
    });
  }

  gestioneSceltaQuantitativo(objRate: RateModel) {
    if (objRate.nrRate !== null && objRate.nrRateMax) {
      this.sceltaQuantitavo.set('rate');
    } else {
      this.sceltaQuantitavo.set('nessuna');
    }
  }

  salva() {
    this.form().markAllAsTouched();
    if (this.checkForm()) {
      const payload = this.form().getRawValue() as RateModel;
      if (payload.periodo !== 'Annuale') {
        payload.mese = null;
      }

      this.rateService.saveRate(payload).subscribe({
        next: (res) => {
          if (res.success) {
            if (this.checkTipeForm() === 'nuovo')
              this.rateList.update((list) => this.sortByPeriodo([...list, res.data]));
            else
              this.rateList.update((list) =>
                this.sortByPeriodo(list.map((s) => (s.idRate === res.data.idRate ? res.data : s))),
              );

            this.toastService.show('success', 'Successo!', res.message);
            this.onAnnullaOrNew(false);
          }
        },
        error: (err) => {
          this.toastService.showErrorHttp(err.message);
          console.error(err);
        },
      });
    } else {
      this.toastService.show('warn', 'Attenzione!', 'Form non valido, controllare i campi');
    }
  }

  onSceltaQuantitavoChange(value: string) {
    switch (value) {
      case 'nessuna':
        this.form().patchValue({
          nrRate: null,
          nrRateMax: null,
          maxValore: null,
        });
        break;
      case 'rate':
        this.form().patchValue({
          nrRate: 0,
        });
        break;
    }
  }

  showError(controlName: string, errorKey?: string): boolean {
    const control = this.form().get(controlName);
    if (!control || !control.touched) return false;

    if (!errorKey) return control.invalid;
    return !!control.errors?.[errorKey];
  }

  checkForm(): boolean {
    if (this.form().valid) {
      if (this.form().get('periodo')?.value === 'Annuale') {
        return true;
      }

      if (this.sceltaQuantitavo() === 'nessuna') {
        return true;
      } else if (this.sceltaQuantitavo() === 'rate') {
        if (this.form().get('nrRate')?.value !== null && this.form().get('nrRateMax')?.value) {
          this.form()
            .get('maxValore')
            ?.setValue(
              (this.form().get('nrRateMax')?.value - this.form().get('nrRate')?.value) *
                this.form().get('euro')?.value,
            );
          return true;
        } else return false;
      }
    }

    return false;
  }

  loadRate() {
    this.rateService.getRate().subscribe({
      next: (res) => {
        if (res.success) {
          this.toastService.show('success', 'Successo!', res.message);
          this.rateList.set(
            this.sortByPeriodo(
              res.data.map((rate) => ({
                ...rate,
                attivo: rate.attivo ?? true,
                mese: rate.mese ?? null,
              })),
            ),
          );
        }
      },
      error: (err) => {
        this.toastService.showErrorHttp(err.message);
        console.error(err);
      },
    });
  }

  sortByPeriodo(list: RateModel[]): RateModel[] {
    const order: Record<string, number> = {
      'Inizio mese': 0,
      'Vicino stipendio': 1,
      Annuale: 2,
    };

    const monthOrder: Record<string, number> = {
      Gennaio: 0,
      Febbraio: 1,
      Marzo: 2,
      Aprile: 3,
      Maggio: 4,
      Giugno: 5,
      Luglio: 6,
      Agosto: 7,
      Settembre: 8,
      Ottobre: 9,
      Novembre: 10,
      Dicembre: 11,
    };

    return [...list].sort((a, b) => {
      const aOrder = order[a.periodo ?? ''] ?? 2;
      const bOrder = order[b.periodo ?? ''] ?? 2;
      if (aOrder !== bOrder) return aOrder - bOrder;

      if (a.periodo === 'Annuale' && b.periodo === 'Annuale') {
        const aMonth = monthOrder[a.mese ?? ''] ?? 99;
        const bMonth = monthOrder[b.mese ?? ''] ?? 99;
        return aMonth - bMonth;
      }

      return 0;
    });
  }
}
