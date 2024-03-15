import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { last } from 'rxjs';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.scss']
})
export class UserRegistrationComponent {
  email!:string;
  firstName!: string;
  lastName!: string;
  data!: string;
  errorMessage!: string;
  constructor(private authService : AuthService, public snackBar: MatSnackBar){}
  onSubmit(){
    const formData  =  new FormData();
    formData.append('firstName', this.firstName)
    formData.append('lastName', this.lastName)
    formData.append('email', this.email)
    console.log('hello');
    
    console.log(this.firstName)
    console.log(this.lastName)
    this.authService.registerUser(formData).subscribe({
      next: (data) => {
        this.data = data.message;
        console.log(data.message);
        this.openSnackBar(data.message)
        
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
