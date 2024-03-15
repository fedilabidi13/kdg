import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./auth/login/login.component";
import {ConnectClusterComponent} from "./auth/connect-cluster/connect-cluster.component";
import { BackOfficeComponent } from './back-office/back-office.component';
import { UsersComponent } from './back-office/users/users.component';
import { AdminDashboardComponent } from './back-office/admin-dashboard/admin-dashboard.component';
import { AuthGuard } from '../app/guards/auth.guard'
const routes: Routes = [
  { path: 'sign-in', component: LoginComponent },
  { path: 'connect-cluster', component: ConnectClusterComponent },
  { path: '', component: BackOfficeComponent, canActivate: [AuthGuard],
  children: [
    { path: 'dashboard', component: AdminDashboardComponent, canActivate: [AuthGuard] },
    { path: 'users', component: UsersComponent , canActivate: [AuthGuard]},
    
  ]
}
 
  
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
