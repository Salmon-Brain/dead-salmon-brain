import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentDataListComponent } from './experiment-data-list.component';

describe('ExperimentDataListComponent', () => {
  let component: ExperimentDataListComponent;
  let fixture: ComponentFixture<ExperimentDataListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExperimentDataListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentDataListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
