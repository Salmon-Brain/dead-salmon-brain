import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, catchError, finalize, Observable, of} from "rxjs";
import {ExperimentService} from "../services/experiment.service";
import {ExperimentData} from "../models/experimentMetrics";

export class ExperimentDataSource implements DataSource<ExperimentData> {

  private experimentsSubject = new BehaviorSubject<ExperimentData[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  constructor(private experimentService: ExperimentService) {
  }

  connect(collectionViewer: CollectionViewer): Observable<ExperimentData[]> {
    return this.experimentsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.experimentsSubject.complete();
    this.loadingSubject.complete();
  }

  loadExperiments(filter = "", sortColumn = "id", sortDirection = "ASC", pageIndex: number = 0, pageSize: number = 25) {

    this.loadingSubject.next(true);

    this.experimentService.findExperiments(
      filter,
      sortColumn,
      sortDirection,
      pageIndex,
      pageSize
    ).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false))
    )
      .subscribe(lessons => this.experimentsSubject.next(lessons));
  }
}
