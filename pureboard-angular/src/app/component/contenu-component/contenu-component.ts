import {Component, input} from '@angular/core';
import {JsonPipe} from "@angular/common";
import {TypeContenuDto} from '../../entity/type-contenu-dto';
import {ContenuDto} from '../../entity/contenu-dto';

@Component({
  selector: 'app-contenu-component',
  imports: [
    JsonPipe
  ],
  templateUrl: './contenu-component.html',
  styleUrl: './contenu-component.scss',
})
export class ContenuComponent {

  contenu = input.required<ContenuDto>();

  protected readonly TypeContenuDto = TypeContenuDto;
}
