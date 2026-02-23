import $ from 'jquery';
import {DashboardDto} from "./entity/dashboard.ts";
import type {LabelDto} from "./entity/label-dto.ts";
import {DataService} from "./service/dataService.ts";

// let urlRacine='http://localhost:8080/api/dashboard';
// let urlRacine = '/api/dashboard';

let listeDashboard:DashboardDto[] = [];

const dataService=new DataService();

async function getData():Promise<DashboardDto[]> {

    return dataService.getItems();

    // const url = urlRacine + "/liste-dashboard";
    // try {
    //     const response = await fetch(url);
    //     if (!response.ok) {
    //         throw new Error(`Response status: ${response.status}`);
    //     }
    //
    //     const result:DashboardDto[] = await response.json();
    //     // console.log(result);
    //     // // 2. Sélection de l'élément avec jQuery
    //     // const $select = $('#monSelect');
    //     //
    //     // // 3. Boucle sur la liste pour ajouter les options
    //     // $.each(result, function (index, item) {
    //     //     $select.append($('<option>', {
    //     //         value: item.id,
    //     //         text: item.titre
    //     //     }));
    //     // });
    //     return result;
    // } catch (error : any) {
    //     console.error(error.message);
    //     return Promise.reject(new Error(`error "${error.message}"`));
    // }
}

async function getListeCard(idDashboard:string):Promise<LabelDto[]>  {
    return dataService.getListCard(idDashboard);
    // const url = urlRacine + "/liste-card/" + idDashboard;
    // try {
    //     const response = await fetch(url);
    //     if (!response.ok) {
    //         throw new Error(`Response status: ${response.status}`);
    //     }
    //     const result:LabelDto[] = await response.json();
    //     return result;
    // } catch (error:any) {
    //     console.error(error.message);
    //     return Promise.reject(new Error(`error "${error.message}"`));
    // }
}

function construitTableau(data:any, config:any):string {
    let contenu = `<table class="table table-striped table-hover">`;
    if (data && data.lignes &&data.lignes.length > 0) {




        // let first = data[0];
        // let keys = Object.keys(first);
        // console.log("keys:", keys);
        // contenu += `<tr>`;
        // for (const key of keys) {
        //     contenu += `<th>${key}</th>`;
        // }
        contenu += `</tr>`;
        for (const item of data.lignes) {
            contenu += `<tr>`;
            console.log("item:", item);
            for (const item2 of item) {
                const valeur = construitContenu(item2, config);
                contenu += `<td>${valeur}</td>`;
            }
            contenu += `</tr>`;
        }
    }
    contenu += "</table>";
    return contenu;
}

function construitObjet(data:any, config:any):string {
    let contenu = "";

    contenu += `<table class="table table-striped table-hover">`;

    for (const [cle, valeur] of Object.entries(data)) {
        const valeur2 = construitContenu(valeur, config);
        contenu += `<tr><th>${cle}</th><td>${valeur2}</td></tr>`;
    }

    contenu += "</table>";
    return contenu;
}

function construitContenu(data:any, config:any) {
    let contenu = '';
    if (data) {
        if (data instanceof Array) {
            // let contenu = '';
            // contenu += `<table class="table table-striped table-hover">`;
            // if (data.length > 0) {
            //     let first = data[0];
            //     let keys = Object.keys(first);
            //     console.log("keys:", keys);
            //     contenu += `<tr>`;
            //     for (const key of keys) {
            //         contenu += `<th>${key}</th>`;
            //     }
            //     contenu += `</tr>`;
            //     for (const item of data) {
            //         contenu += `<tr>`;
            //         console.log("item:", item);
            //         for (const key of keys) {
            //             contenu += `<td>${item[key]}</td>`;
            //         }
            //         contenu += `</tr>`;
            //     }
            // }
            // contenu += "</table>";
            // console.log("contenu tab:", contenu);
            // $(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu);
            return construitTableau(data, config);
        } else if (data instanceof Object) {
            //let res0=$("#dashboard")

            if (data.type) {
                if (data.type === "TABLEAU") {
                    contenu = construitTableau(data.tableau, config);
                } else if (data.type === "objet") {
                    contenu = construitTableau(data.objet, config);
                } else if (data.type === "TEXTE") {
                    contenu = `<p>${data.texte}</p>`;
                } else if (data.type === "icone") {
                    contenu = `<i class="${data.classes}"></i>`;
                } else if (data.type === "lien") {
                    contenu = `<a href="${data.lien}" target="_blank">${data.texte}</a>`;
                } else if (data.type === "compose") {
                    if (data.liste && data.liste.length > 0) {
                        data.liste.forEach((element:any) => {
                            contenu += construitContenu(element, config);
                        });
                    }
                } else if (data.type === "bouton") {
                    let idBouton = config.noBouton;
                    config.noBouton++;
                    contenu = `<button class="btn btn-primary" data-idBouton="${idBouton}">${data.texte}</button>`;
                    config.paramBouton.set(idBouton, [data.methode, data.parametres]);
                }
            } else {

                // console.log("texte:", data.texte, "myId:", myId);
                //
                // contenu = `<p>${data.texte} : ${myId} !</p>`;
                //
                // contenu += `<table class="table table-striped table-hover">`;
                //
                // for (const [cle, valeur] of Object.entries(data)) {
                //     contenu += `<tr><th>${cle}</th><td>${valeur}</td></tr>`;
                // }
                //
                // contenu += "</table>";
                // console.log("contenu:", contenu);
                contenu = construitObjet(data, config);
            }

            //$(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu);
        } else if (typeof data === "string") {
            contenu = `<p>${data}</p>`;
        } else {
            contenu = `${data}`;
            //$(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu);
        }
    }

    return contenu;
}

function ajouteClickBouton(contenu:JQuery, config:any, card:any):void {
    config.paramBouton.forEach((valeur:any, cle:any) => {
        console.log("cle:", cle, "valeur:", valeur);
        let idBouton = cle;
        let methode = (valeur.length > 0) ? valeur[0] : '';
        let parametres = (valeur.length > 1) ? valeur[1] : [];
        console.log("idBouton:", idBouton, "methode:", methode, "parametres:", parametres);
        contenu.find(`button[data-idBouton="${idBouton}"]`).on('click', function () {
            console.log("appel methode:", methode, "parametres:", parametres);
            //eval(methode + "(" + parametres + ")");
            fetch("/dashboard/item/click?idDashboard=" + card.dashboard + "&idTraitement=" + card.idTraitement + "&methode=" + methode + "&parametres=" + JSON.stringify(parametres))
                .then(response => response.json())
                .then(data => {
                    console.log("Résultat reçu :", data);
                })
                .catch(error => {
                    console.error("Erreur :", error);
                });

        });
    })
    // contenu.find('button').on('click', function () {
    //     let idBouton = $(this).data('idBouton');
    //     let parametres = config.paramBouton.get(idBouton);
    //     let methode = parametres[0];
    //     let parametres2 = parametres[1];
    //     console.log("idBouton:", idBouton, "methode:", methode, "parametres:", parametres2);
    //     maFonction();
    // });
}

function miseAJourdonnes(card:LabelDto, myId0:string, idDashboard:string):void {

    // let myId = card.id;
    const myId = myId0;
    console.log("miseAJourdonnes myId:", myId);

    //fetch("/card/" + idDashboard + "/" + myId0)
    //    .then(response => response.json())
    dataService.getCard(idDashboard,myId0)
        .then(data => {
            console.log("Résultat reçu :", data);
            let paramBouton = new Map();
            let config = {noBouton: 1, paramBouton: paramBouton};
            const contenu = construitContenu(data.contenu, config) || '';
            let contenu2 = $(contenu);
            $(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu2 as any);
            ajouteClickBouton(contenu2, config, card);
            // if (data) {
            //     if (data instanceof Array) {
            //         let contenu = '';
            //         contenu += `<table class="table table-striped table-hover">`;
            //         if (data.length > 0) {
            //             let first = data[0];
            //             let keys = Object.keys(first);
            //             console.log("keys:", keys);
            //             contenu += `<tr>`;
            //             for (const key of keys) {
            //                 contenu += `<th>${key}</th>`;
            //             }
            //             contenu += `</tr>`;
            //             for (const item of data) {
            //                 contenu += `<tr>`;
            //                 console.log("item:", item);
            //                 for (const key of keys) {
            //                     contenu += `<td>${item[key]}</td>`;
            //                 }
            //                 contenu += `</tr>`;
            //             }
            //         }
            //         contenu += "</table>";
            //         console.log("contenu tab:", contenu);
            //         $(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu);
            //     } else if (data instanceof Object) {
            //         //let res0=$("#dashboard")
            //         console.log("texte:", data.texte, "myId:", myId);
            //
            //         let contenu = `<p>${data.texte} : ${myId} !</p>`;
            //
            //         contenu += `<table class="table table-striped table-hover">`;
            //
            //         for (const [cle, valeur] of Object.entries(data)) {
            //             contenu += `<tr><th>${cle}</th><td>${valeur}</td></tr>`;
            //         }
            //
            //         contenu += "</table>";
            //         console.log("contenu:", contenu);
            //
            //         $(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu);
            //     } else {
            //         let contenu = `<p>${data}</p>`;
            //         $(`#dashboard div[data-id="${myId}"] .contenu`).html(contenu);
            //     }
            // }
        })
        .catch(error => {
            console.error("Erreur :", error);
        });
}

export function setupJQuery() {


    $(document).ready(function () {

        $("#selectDashboard").on("click", function () {
                //alert("Handler for `click` called.");

                if (listeDashboard && listeDashboard.length > 0) {

                    let selectedTexte = $("#monSelect option:selected").text();

                    if (selectedTexte) {
                        let val = $("#monSelect").val();

                        let dashboardSelectionne:DashboardDto|null = null;

                        for (let dashboard of listeDashboard) {
                            if (dashboard.id === val) {
                                //console.log(dashboard);
                                dashboardSelectionne = dashboard;
                            }
                        }

                        if (dashboardSelectionne) {


                            getListeCard(dashboardSelectionne.id).then(
                                listeCard => {
                                    dashboardSelectionne.listCard = listeCard;

                                    if (dashboardSelectionne.listCard) {

                                        $("#dashboard").empty();

                                        //for (let card of dashboardSelectionne.listCard) {
                                        dashboardSelectionne.listCard.forEach(card => {
                                            // let _titre = card.label;
                                            let myId = card.id;
                                            let contenu = `
<div class="card" style="width: 18rem; border: 1px; min-width: 18rem" data-id="${myId}">
  <div class="card-body">
    <h5 class="card-title">Card title ${card.label} <button type="button" class="btn btn-primary"><i class="bi bi-arrow-clockwise"></i></button></h5>
<!--    <h6 class="card-subtitle mb-2 text-body-secondary">Card subtitle</h6>-->
<!--    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card’s content.</p>-->
<!--    <a href="#" class="card-link">Card link</a>-->
<!--    <a href="#" class="card-link">Another link</a>-->
    <div class="contenu"></div>
  </div>
</div>
        `
                                            const contenu2 = $(contenu);
                                            // let res =
                                            $("#dashboard").append(contenu2);

                                            contenu2.find("h5 button").on("click", () => {
                                                //alert("Bouton dans le h5 cliqué !");
                                                console.log("Bouton dans le h5 cliqué !" + myId);
                                                miseAJourdonnes(card, myId, dashboardSelectionne.id);
                                            });

                                            // fetch("/dashboard/item?idDashboard=" + card.dashboard + "&idTraitement=" + card.idTraitement)
                                            //     .then(response => response.json())
                                            //     .then(data => {
                                            //         console.log("Résultat reçu :", data);
                                            //         if(data && data instanceof Object){
                                            //             //let res0=$("#dashboard")
                                            //             $(`#dashboard div[data-id="${myId}"] .contenu`).html(data.texte+" :"+myId+"!");
                                            //         }
                                            //     })
                                            //     .catch(error => {
                                            //         console.error("Erreur :", error);
                                            //     });

                                            // function toto() {
                                            //     let card0=card;
                                            //     let myId0=myId;
                                            //     miseAJourdonnes(card0, myId0);
                                            // }

                                            // toto();
                                            miseAJourdonnes(card, myId, dashboardSelectionne.id);


                                        });
                                    }

                                }
                            )


                        }
                    }

                }


            }
        )
        ;

        getData().then(function (x) {
            console.log("ok");
            if (x) {
                listeDashboard = x;

                console.log(listeDashboard);
                // 2. Sélection de l'élément avec jQuery
                const $select = $('#monSelect');

                // 3. Boucle sur la liste pour ajouter les options
                $.each(listeDashboard, function (_index:number, item:DashboardDto) {
                    $select.append($('<option>', {
                        value: item.id,
                        text: item.titre
                    }));
                });
            }
        });
    });


}