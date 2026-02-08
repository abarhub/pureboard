import {ContenuDto} from './contenu-dto';

export class Card {
  id: string = '';
  nonCalcule: boolean = true;
  titre: string = '';
  contenu: ContenuDto = new ContenuDto();
}
