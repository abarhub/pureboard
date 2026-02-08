import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DashboardDto} from '../entity/dashboard';
import {CardDto} from '../entity/card-dto';
import {LabelDto} from '../entity/label-dto';

@Injectable({
  providedIn: 'root',
})
export class DataService {

  private apiUrl = 'api/dashboard'; // Remplacez par votre URL

  constructor(private http: HttpClient) {
  }

  // Cette méthode retourne un "Observable" que le composant pourra écouter
  getItems(): Observable<DashboardDto[]> {
    return this.http.get<DashboardDto[]>(this.apiUrl + "/liste-dashboard");
  }

  getListCard(id: string): Observable<LabelDto[]> {
    return this.http.get<LabelDto[]>(this.apiUrl + "/liste-card/" + id);
  }

  getCard(idDashboard: string, id: string): Observable<CardDto> {
    return this.http.get<CardDto>(this.apiUrl + "/card/" + idDashboard + "/" + id);
  }

}
