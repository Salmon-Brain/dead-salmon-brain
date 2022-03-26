import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, debounceTime, distinctUntilChanged, fromEvent, tap} from 'rxjs';
import {ExperimentDataSource} from '../services/eperimentDataSource';
import {ExperimentService} from '../services/experiment.service';


@Component({
  selector: 'app-experiment-data-list',
  templateUrl: './experiment-data-list.component.html',
  styleUrls: ['./experiment-data-list.component.css']
})
export class ExperimentDataListComponent implements OnInit, AfterViewInit {

  dataSource: ExperimentDataSource;
  displayedColumns = ["id", "expUid", "timestamp"];
  totalCount: BehaviorSubject<number>;

  @ViewChild(MatPaginator, {static: false}) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('input') input!: ElementRef;


  constructor(private experimentService: ExperimentService, private route: ActivatedRoute) {
    this.dataSource = new ExperimentDataSource(this.experimentService);
    this.totalCount = this.experimentService.getTotalCount();
  }

  ngOnInit(): void {
    this.dataSource.loadExperiments("", "id", "ASC", 0, 10)
  }

  ngAfterViewInit() {
    // server-side search
    fromEvent(this.input.nativeElement, 'keyup')
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.paginator.pageIndex = 0;
          this.loadExperiments();
        })
      )
      .subscribe();

    this.sort.sortChange.subscribe(() => {
      this.paginator.pageIndex = 0;
      this.loadExperiments();
    });
    this.paginator.page
      .pipe(
        tap(() => this.loadExperiments())
      )
      .subscribe();
  }

  loadExperiments() {
    this.dataSource.loadExperiments(
      this.input.nativeElement.value,
      this.sort.active,
      this.sort.direction,
      this.paginator.pageIndex,
      this.paginator.pageSize
    )
  }
}
