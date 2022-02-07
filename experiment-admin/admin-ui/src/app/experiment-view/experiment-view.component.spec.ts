import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentViewComponent } from './experiment-view.component';

describe('ExperimentViewComponent', () => {
  let component: ExperimentViewComponent;
  let fixture: ComponentFixture<ExperimentViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExperimentViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
