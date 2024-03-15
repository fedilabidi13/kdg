import { Component } from '@angular/core';
import {FormGroup, NgForm} from "@angular/forms";
import {empty} from "rxjs";
import { AuthService } from '../auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email !: string;
  password !: string;
  data!:string;
  errorMessage!: string;
  constructor(public snackBar: MatSnackBar, private authService: AuthService
              , private router: Router){

  }
  onSubmit(loginForm: NgForm){
    const formData = new FormData();
    formData.append("email", this.email);
    formData.append("password", this.password);
    console.log(formData.get('email'))
    console.log(formData.get('password'));
    
    this.authService.login(formData).subscribe({
      next: (data) => {
        this.data = data.message;
        console.log(data.message);
        this.openSnackBar(data.message)
        localStorage.setItem('Bearer Token',data.access_token)
        localStorage.setItem('Refresh Token',data.refresh_token)
        localStorage.setItem('Role', data.role)
        console.log('DDFSFSDFSf');
        console.log(localStorage.getItem('Bearer Token')!=='null');
        
        
        if (localStorage.getItem('Bearer Token')!='null'){
            this.router.navigate(['/dashboard'])
        }
      },
      error: (error) => {
        this.errorMessage = error.error.message;
        console.log(error.error.message)
        this.openSnackBar(this.errorMessage)

      },
      complete: () => {
        console.log('Request completed'); // Optional
      }
    });
  }
  openSnackBar(message: string) {
    this.snackBar.open(message, "dismiss", {
      duration: 2000,
    });
  }

}
