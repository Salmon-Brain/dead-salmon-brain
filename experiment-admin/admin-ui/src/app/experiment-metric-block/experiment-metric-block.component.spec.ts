import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentMetricBlockComponent } from './experiment-metric-block.component';

describe('ExperimentMetricBlockComponent', () => {
  let component: ExperimentMetricBlockComponent;
  let fixture: ComponentFixture<ExperimentMetricBlockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExperimentMetricBlockComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentMetricBlockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
