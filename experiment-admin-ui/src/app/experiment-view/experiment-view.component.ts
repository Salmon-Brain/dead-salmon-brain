import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { ChartData, LineData, MetricBlock } from '../models/chartData';
import { ExperimentService } from '../services/experiment.service';
import { ExperimentData, StatResult } from '../models/experimentMetrics';

@Component({
  selector: 'app-experiment-view',
  templateUrl: './experiment-view.component.html',
  styleUrls: ['./experiment-view.component.css']
})
export class ExperimentViewComponent implements OnInit {

  experiment: ExperimentData | undefined;
  blocks: Map<string, MetricBlock> = new Map<string, MetricBlock>();

  constructor(
    private route: ActivatedRoute,
    private experimentService: ExperimentService
  ) {
  }

  ngOnInit(): void {
    this.getChartsData()
  }

  getExperimentData(): Observable<ExperimentData> {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    return this.experimentService.getExperiment(id)
  }

  getChartsData(): void {
    this.getExperimentData().subscribe(r => {
      this.experiment = r;

      this.experiment.metricData.forEach(m => {

        let statResult: StatResult = m.statisticsData.statResult

        let key = m.metricName
          .concat("#")
          .concat(m.statisticsData.categoryName)
          .concat("#")
          .concat(m.statisticsData.categoryValue);

        var block = this.blocks.get(key);
        if (!block) {
          block = new MetricBlock(
            m.metricName,
            m.statisticsData.categoryName,
            m.statisticsData.categoryValue
          );
          this.blocks.set(key, block);
        }

        this.append(block.pValue, "p-value", m.ts, statResult.pvalue)

        this.append(block.controlCentralTendency, "value", m.ts, statResult.controlCentralTendency)
        this.append(block.controlCentralTendency, "left", m.ts, statResult.controlCentralTendency + statResult.controlVariance)
        this.append(block.controlCentralTendency, "right", m.ts, statResult.controlCentralTendency - statResult.controlVariance)

        this.append(block.treatmentCentralTendency, "value", m.ts, statResult.treatmentCentralTendency)
        this.append(block.treatmentCentralTendency, "left", m.ts, statResult.treatmentCentralTendency + statResult.treatmentVariance)
        this.append(block.treatmentCentralTendency, "right", m.ts, statResult.treatmentCentralTendency - statResult.treatmentVariance)

        this.append(block.percentage, "left", m.ts, statResult.percentageLeft)
        this.append(block.percentage, "mid", m.ts, (statResult.percentageRight + statResult.percentageLeft) / 2)
        this.append(block.percentage, "right", m.ts, statResult.percentageRight)
      })
    })
  }

  private append(chart: ChartData, param: string, date: Date, value: number): void {
    var line = chart.lines.get(param)
    if (!line) {
      line = new LineData(param);
      chart.lines.set(param, line);
    }
    line.dots.push(
      [new Date(date).getTime(), value]
    )
  }
}
