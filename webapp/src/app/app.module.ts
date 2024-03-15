import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { MatFormFieldModule } from "@angular/material/form-field";
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatMenuModule} from "@angular/material/menu";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MenuItems} from "./shared/MenuItems";
import {MatSidenavModule} from "@angular/material/sidenav";
import { HeaderComponent } from './components/header/header.component';
import {SpinnerComponent} from "./shared/spinner.component";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './auth/login/login.component';
import {MatInputModule} from "@angular/material/input";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { ConnectClusterComponent } from './auth/connect-cluster/connect-cluster.component';
import {MatTabsModule} from "@angular/material/tabs";
import {MatStepperModule} from "@angular/material/stepper";
import { NgxMatFileInputModule } from '@angular-material-components/file-input';
import { BackOfficeComponent } from './back-office/back-office.component';
import { AppSidebarComponent } from './components/sidebar/sidebar.component';
import { CdkStepperModule } from '@angular/cdk/stepper';
import { UsersComponent } from './back-office/users/users.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { NgxApexchartsModule } from 'ngx-apexcharts';
import { AdminDashboardComponent } from './back-office/admin-dashboard/admin-dashboard.component';
import { HttpClientModule } from '@angular/common/http';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { UserRegistrationComponent } from './back-office/user-registration/user-registration.component';
import { MatDialogModule } from '@angular/material/dialog'; // Import MatDialogModule
import { MatRadioModule } from '@angular/material/radio';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { PermissionsComponent } from './components/permissions/permissions.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';



@NgModule({
  declarations: [
    AppComponent,
    SpinnerComponent,
    LoginComponent,
    ConnectClusterComponent,
    BackOfficeComponent,
    UsersComponent,
    AdminDashboardComponent,
    UserRegistrationComponent,
    PermissionsComponent,
    

  ],
  imports: [
    MatTooltipModule,
    MatCheckboxModule,
    MatSlideToggleModule,
    MatRadioModule,
    MatDialogModule,
    MatSnackBarModule,
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatButtonToggleModule,
    MatMenuModule,
    MatToolbarModule,
    MatListModule,
    MatSidenavModule,
    AppSidebarComponent,
    HeaderComponent,
    BrowserAnimationsModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    FormsModule,
    MatTabsModule,
    MatStepperModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    NgxMatFileInputModule,
    AppSidebarComponent,
    CdkStepperModule,
    MatTableModule,
    MatPaginatorModule,
    NgxApexchartsModule
    
  ],
  providers: [MenuItems],
  bootstrap: [AppComponent]
})
export class AppModule { }
