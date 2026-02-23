import {TypeContenuDto} from './type-contenu-dto';
import {TableauDto} from './tableau-dto';

export class ContenuDto {

  type: string = TypeContenuDto.INCONNU;
  texte: string = '';
  tableau: TableauDto | null = null;

  classe: string = '';

  lien: string = '';

  listeContenu: ContenuDto[] = [];

  nomMethode: string = '';

  parametresMethode: string = '';
}
