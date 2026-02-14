import {ContenuDto} from './contenu-dto';

export class CardEntity {
  id: string = '';
  nonCalcule: boolean = true;
  titre: string = '';
  contenu: ContenuDto = new ContenuDto();
  recharge?: (card: CardEntity) => void;
}
