import {Component, Input, OnInit} from '@angular/core';
import {MetricBlock} from '../models/chartData';

@Component({
  selector: 'app-experiment-metric-block',
  templateUrl: './experiment-metric-block.component.html',
  styleUrls: ['./experiment-metric-block.component.css']
})
export class ExperimentMetricBlockComponent implements OnInit {

  @Input()
  block?: MetricBlock;

  constructor() {
  }

  ngOnInit(): void {
  }

}
