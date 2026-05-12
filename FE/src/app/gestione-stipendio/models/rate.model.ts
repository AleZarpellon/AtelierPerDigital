export interface RateModel {
  idRate: number | null;
  descrizione: string;
  euro: number;
  nrRate: number | null;
  nrRateMax: number | null;
  maxValore: number | null;
  periodo: string | null;
  mese: string | null;
  attivo: boolean;
}
