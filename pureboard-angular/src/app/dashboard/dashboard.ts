import {Component, OnInit, signal} from '@angular/core';
import {DataService} from '../service/data-service';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DashboardDto} from '../entity/dashboard';
import {CardEntity} from '../entity/card';
import {KeyValuePipe} from '@angular/common';
import {TypeContenuDto} from '../entity/type-contenu-dto';
import {ContenuComponent} from '../component/contenu-component/contenu-component';
import {Select} from 'primeng/select';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {ProgressSpinner} from 'primeng/progressspinner';

@Component({
  selector: 'app-dashboard',
  imports: [
    ReactiveFormsModule,
    KeyValuePipe,
    ContenuComponent,
    Select,
    Button,
    Card,
    ProgressSpinner
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {

  dashboards: DashboardDto[] = [];
  selectionDashboard!: FormGroup;
  listeCards: Map<string, CardEntity> = new Map();
  chargementCards = signal(false);
  idDashboard: string = '';

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
    if (idDashboard) {
      this.idDashboard = idDashboard;

      let tab = this.dashboards.find(x => x.id === idDashboard);
      if (tab) {

        if (tab.type == 'card') {
          this.dataService.getListCard(idDashboard).subscribe(data => {
            console.log('liste card', data);
            for (const card of data) {
              let cardObj = new CardEntity();
              cardObj.id = card.id;
              cardObj.nonCalcule = true;
              cardObj.titre = card.label;
              this.listeCards.set(card.id, cardObj);
            }
            this.chargementCards.set(false);
            this.recalculToutesCards();
          });
        } else if (tab.type == 'tableau') {
          console.log('tableau');
        }

      }

    }
  }

  private recalculToutesCards() {
    for (const card of this.listeCards.values()) {
      this.recalculCards(card);
    }
  }

  private recalculCards(card: CardEntity) {
    // let cardTrouve: CardEntity | null = null;
    // console.log('recalculCards', idCard);
    // //console.log('listeCards', this.listeCards);
    // for (const card of this.listeCards.values()) {
    //   //console.log('test', card.id, idDashboard, card);
    //   if (card.id === idCard) {
    //     cardTrouve = card;
    //     break;
    //   }
    // }
    // if (cardTrouve) {
    //   let card = cardTrouve;
    card.nonCalcule = true;
    this.dataService.getCard(this.idDashboard, card.id).subscribe({
      next: data => {
        if (data && data.contenu) {
          // console.log('card', card.id, data.contenu);
          card.contenu = data.contenu;
          // console.log('card2', card);
          card.recharge = (x) => {
            this.recalculCards(x);
          }
        }
      },
      error: err => {
        console.error(err);
      },
      complete: () => {
        card.nonCalcule = false;
      }
    });
    // } else {
    //   console.log('rechargeCard', this.idDashboard, '/', idCard, "non trouve");
    // }
  }

  protected readonly TypeContenuDto = TypeContenuDto;

  protected rechargeCard(card: CardEntity) {
    console.log('rechargeCard', card);
    if (card.recharge) {
      console.log('rechargeCard', card, "...");
      card.recharge(card);
      console.log('rechargeCard', card, "ok");
    }
  }
}
