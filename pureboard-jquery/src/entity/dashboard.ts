import type {LabelDto} from "./label-dto.ts";

export class DashboardDto {
  id: string='';
  titre: string='';
  listCard:LabelDto[] = [];
}
