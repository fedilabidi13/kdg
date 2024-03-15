import { MediaMatcher } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MenuItems } from 'src/app/shared/MenuItems';
import { MatTableDataSource } from '@angular/material/table'
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { UserRegistrationComponent } from '../user-registration/user-registration.component';
import { AuthService } from 'src/app/auth/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { User } from 'src/app/shared/models/user';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { PermissionsComponent } from 'src/app/components/permissions/permissions.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent  implements OnInit , AfterViewInit{ 
  toggleValue: boolean = false;
  users: any=[];
  data !: string;
  errorMessage!: string;
  @ViewChild(MatPaginator) paginator!: MatPaginator; 
  dataSource !: MatTableDataSource<any>;

  pageSize = 5; 
  pageSizeOptions = [5, 10, 25]; 
  displayedColumns = ['firstName', 'lastName', 'email','actions']; 
ngModel: any;

  constructor(public dialog: MatDialog, private authService: AuthService, public snackBar: MatSnackBar) { } 
  ngAfterViewInit(): void {
    if (this.dataSource) {
      this.dataSource.paginator = this.paginator;
    }
  }
  ngOnInit(): void { 
    this.authService.listUsers().subscribe({
      next: ((data: {}) => {
        this.users = data;
        console.log(this.users)
        this.dataSource = new MatTableDataSource<any>(this.users);
        this.dataSource.data = this.users;
        this.dataSource.paginator = this.paginator;
      }),
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
  filter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = filterValue;
  } 
  openDialog(){
    const dialogRef = this.dialog.open(UserRegistrationComponent, {
      width: '500px',
      
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
  openSnackBar(message: string) {
    this.snackBar.open(message, "dismiss", {
      duration: 2000,
    });
  }
  toggleChanged(event: MatSlideToggleChange, email: any) {
    if (!event.checked){
      this.authService.banUser(email).subscribe({
        next: ((data) => {
          console.log(data.message)
          this.openSnackBar(data.message)

        }),
        error: (error) => {
          console.log(error.error.message)
        },
        complete: () => {
          console.log('Request completed'); // Optional
        }
      });
    }
    if (event.checked){
      this.authService.unBanUser(email).subscribe({
        next: ((data) => {
          console.log(data.message)
          this.openSnackBar(data.message)
        }),
        error: (error) => {
          console.log(error.error.message)
        },
        complete: () => {
          console.log('Request completed'); // Optional
        }
      });
    }
}
isAdmin(id:any): boolean{
  if (id==1){
    return true;
  }
  return false;
}
openPermissionDialog(email:any){
  const dialogRef = this.dialog.open(PermissionsComponent, {
    width: '500px',
    
  });
  localStorage.setItem('email', email);
  dialogRef.afterClosed().subscribe(result => {
  });
}
}
