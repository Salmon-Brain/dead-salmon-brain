import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ExperimentData, ExperimentPage } from '../models/experimentMetrics';
import { map, tap } from 'rxjs/operators';
import { isDevMode } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ExperimentService {

  private experimentDataUrl;
  private totalCount: BehaviorSubject<number> = new BehaviorSubject<number>(0)

  constructor(
    private http: HttpClient
  ) {
    if (isDevMode()) {
      this.experimentDataUrl = "http://localhost:8081/api/experiments"
    } else {
      this.experimentDataUrl = "/api/experiments"
    }
  }

  getTotalCount() {
    return this.totalCount;
  }

  findExperiments(
    filter = '',
    sortColumn = 'id',
    sortOrder = 'asc',
    pageNumber = 0,
    pageSize = 10
  ): Observable<ExperimentData[]> {
    return this.http.get<ExperimentPage>(this.experimentDataUrl, {
      params: new HttpParams()
        .set('filter', filter)
        .set('sort', sortColumn)
        .set('order', sortOrder.toUpperCase())
        .set('pageNumber', pageNumber.toString())
        .set('pageSize', pageSize.toString())
    }).pipe(tap(r => (this.totalCount.next(r.totalCount))), map(r => r.experiments))
  }

  getExperiment(id: number): Observable<ExperimentData> {
    return this.http.get<ExperimentData>(this.experimentDataUrl + "/" + id)
  }
}