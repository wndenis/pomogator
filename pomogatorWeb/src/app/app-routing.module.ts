import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MapComponent } from './map/map/map.component';
import { MainComponent } from './main/main/main.component';
import { AppComponent } from './app.component';

const routes: Routes = [
  {path: 'map', component: MapComponent },
  {path: 'main-page', component: MainComponent},
  {path: '', component: AppComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
