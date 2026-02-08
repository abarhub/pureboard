import {Component, input} from '@angular/core';
import {JsonPipe} from "@angular/common";
import {TypeContenuDto} from '../../entity/type-contenu-dto';
import {ContenuDto} from '../../entity/contenu-dto';
import {TableModule} from 'primeng/table';

@Component({
  selector: 'app-contenu-component',
  imports: [
    JsonPipe,
    TableModule
  ],
  templateUrl: './contenu-component.html',
  styleUrl: './contenu-component.scss',
})
export class ContenuComponent {

  contenu = input.required<ContenuDto>();

  protected readonly TypeContenuDto = TypeContenuDto;
}
