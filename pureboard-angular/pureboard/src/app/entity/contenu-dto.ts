import {TypeContenuDto} from './type-contenu-dto';
import {TableauDto} from './tableau-dto';

export class ContenuDto {

  type: TypeContenuDto = TypeContenuDto.INCONNU;
  texte: string = '';
  tableau: TableauDto | null = null;
}
