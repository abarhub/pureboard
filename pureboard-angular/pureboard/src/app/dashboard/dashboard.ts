import {Component, OnInit, signal} from '@angular/core';
import {DataService} from '../service/data-service';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DashboardDto} from '../entity/dashboard';
import {Card} from '../entity/card';
import {KeyValuePipe} from '@angular/common';
import {TypeContenuDto} from '../entity/type-contenu-dto';
import {ContenuComponent} from '../component/contenu-component/contenu-component';

@Component({
  selector: 'app-dashboard',
  imports: [
    ReactiveFormsModule,
    KeyValuePipe,
    ContenuComponent
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {

  dashboards: DashboardDto[] = [];
  selectionDashboard!: FormGroup;
  listeCards: Map<string, Card> = new Map();
  chargementCards = signal(false);

  constructor(private dataService: DataService, private fb: FormBuilder) {
  }

  ngOnInit() {

    console.log('Dashboard initialized with items:', this.dashboards);
    this.selectionDashboard = this.fb.group({
      dashboard: ['', Validators.required],
      // nom: ['', Validators.required],
      // email: ['', [Validators.required, Validators.email]],
      // age: [null, Validators.min(18)]
    });
    // On s'abonne à l'Observable pour recevoir les données
    this.dataService.getItems().subscribe(data => {
      this.dashboards = data;
    });
  }

  protected onSubmit() {

  }

  protected selection($event: PointerEvent) {
    $event.preventDefault();
    console.log('selection', this.selectionDashboard.value);
    this.listeCards.clear();
    this.chargementCards.set(true);
    let idDashboard = this.selectionDashboard.value.dashboard;
    this.dataService.getListCard(idDashboard).subscribe(data => {
      console.log('liste card', data);
      for (const card of data) {
        let cardObj = new Card();
        cardObj.id = card.id;
        cardObj.nonCalcule = true;
        cardObj.titre = card.label;
        this.listeCards.set(card.id, cardObj);
      }
      this.chargementCards.set(false);
      this.recalculCards(idDashboard);
    });
  }

  private recalculCards(idDashboard: string) {
    for (const card of this.listeCards.values()) {
      this.dataService.getCard(idDashboard, card.id).subscribe(data => {
        if (data && data.contenu) {
          console.log('card', card.id, data.contenu);
          card.contenu = data.contenu;
          card.nonCalcule = false;
          console.log('card2', card);
        }
      });
    }
  }

  protected readonly TypeContenuDto = TypeContenuDto;
}
