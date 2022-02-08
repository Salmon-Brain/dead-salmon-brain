import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ExperimentDataListComponent } from './experiment-data-list/experiment-data-list.component';


import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatInputModule } from "@angular/material/input";
import { MatSortModule } from "@angular/material/sort";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ExperimentViewComponent } from './experiment-view/experiment-view.component';

import { HighchartsChartModule } from 'highcharts-angular';
import { ExperimentMetricChartComponent } from './experiment-metric-chart/experiment-metric-chart.component';
import { ExperimentMetricBlockComponent } from './experiment-metric-block/experiment-metric-block.component';

@NgModule({
  declarations: [
    AppComponent,
    ExperimentDataListComponent,
    ExperimentViewComponent,
    ExperimentMetricChartComponent,
    ExperimentMetricBlockComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    HighchartsChartModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
