import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentMetricChartComponent } from './experiment-metric-chart.component';

describe('ExperimentMetricChartComponent', () => {
  let component: ExperimentMetricChartComponent;
  let fixture: ComponentFixture<ExperimentMetricChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExperimentMetricChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentMetricChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
