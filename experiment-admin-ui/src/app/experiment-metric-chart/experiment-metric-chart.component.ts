import { Component, Input, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts';
import { ChartData, MetricBlock } from '../models/chartData';

@Component({
  selector: 'app-experiment-metric-chart',
  templateUrl: './experiment-metric-chart.component.html',
  styleUrls: ['./experiment-metric-chart.component.css']
})
export class ExperimentMetricChartComponent implements OnInit {

  @Input()
  chartData?: ChartData;

  @Input()
  chartName?: string;

  @Input()
  yAxis?: number[];

  @Input()
  block?: MetricBlock;

  Highcharts: typeof Highcharts = Highcharts;
  chartConstructor: string = 'chart';

  chartOptions: Highcharts.Options = {};
  chartCallback: Highcharts.ChartCallbackFunction = function (chart) { }
  updateFlag: boolean = false;

  constructor() { }

  ngOnInit(): void {
    let series: Highcharts.SeriesOptionsType[] = [];


    let yAxisValue: Highcharts.YAxisOptions = {
      title: {
        text: this.chartName
      },
      plotLines: []
    };

    if (this.yAxis) {
      this.yAxis.forEach((v, i) => {
        yAxisValue.plotLines?.push({
          value: v,
          width: 2,
          color: "green",
          dashStyle: "ShortDash",
        });
      })
    }

    this.chartData?.lines.forEach((value, key) => {
      series.push({
        data: value.dots,
        type: 'line',
        name: value.name
      })
    });

    this.chartOptions = {
      title: {
        text: this.chartName + ": " + this.block?.name + " - " + this.block?.categoryName + " - " + this.block?.categoryValue
      },
      yAxis: yAxisValue,
      xAxis: {
        type: 'datetime',
        labels: {
          format: '{value:%Y-%m-%d %H:%M}'
        }
      },
      series: series
    }

    this.updateFlag = true;
  }

}
