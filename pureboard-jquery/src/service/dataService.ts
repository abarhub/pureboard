import type {DashboardDto} from "../entity/dashboard.ts";
import type {LabelDto} from "../entity/label-dto.ts";
import type {CardDto} from "../entity/card-dto.ts";

export class DataService {

    private apiUrl = 'api/dashboard'; // Remplacez par votre URL

    constructor() {
    }

    // Cette méthode retourne un "Observable" que le composant pourra écouter
    getItems(): Promise<DashboardDto[]> {
        return fetch(this.apiUrl + "/liste-dashboard")
            .then(response => response.json());
    }

    getListCard(id: string): Promise<LabelDto[]> {
        return fetch(this.apiUrl + "/liste-card/" + id)
            .then(response => response.json());
    }

    getCard(idDashboard: string, id: string): Promise<CardDto> {
        return fetch(this.apiUrl + "/card/" + idDashboard + "/" + id)
            .then(response => response.json());
    }

}