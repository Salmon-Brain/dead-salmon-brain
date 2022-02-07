import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExperimentDataListComponent } from './experiment-data-list/experiment-data-list.component';
import { ExperimentViewComponent } from './experiment-view/experiment-view.component';

const routes: Routes = [
  { path: '', redirectTo: 'ui/dashboard', pathMatch: 'full' },
  { path: 'ui/dashboard', component: ExperimentDataListComponent},
  { path: 'ui/experimentView/:id', component: ExperimentViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
