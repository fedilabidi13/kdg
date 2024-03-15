import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators, FormControl} from "@angular/forms";
import { AuthService } from '../auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-connect-cluster',
  templateUrl: './connect-cluster.component.html',
  styleUrls: ['./connect-cluster.component.scss']
})
export class ConnectClusterComponent implements OnInit{
  visible: boolean = false;
  firstName!: string;
  lastName!:string;
  email!:string;
  password!:string;
  selectedFile: File | null = null;  
  fileFormControl = new FormControl();
  isLinear = false;
  secondFormGroup: FormGroup = new FormGroup({});
  firstFormGroup: FormGroup = new FormGroup({});
  isLinearvarient = false;
  varientsecondFormGroup: FormGroup = new FormGroup({});

  positionsecondFormGroup: FormGroup = new FormGroup({});


  optionalsecondFormGroup: FormGroup = new FormGroup({});
  isOptional = false;

  editablesecondFormGroup: FormGroup = new FormGroup({});
  isEditable = false;

  customizesecondFormGroup: FormGroup = new FormGroup({});

  errorsecondFormGroup: FormGroup = new FormGroup({});
  errorMessage!: string;
  data!: string;

  constructor(private _formBuilder: FormBuilder, private authService: AuthService,
    public snackBar: MatSnackBar) { }

  ngOnInit() {

    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required]
    });
    this.varientsecondFormGroup = this._formBuilder.group({
      varientsecondCtrl: ['', Validators.required]
    });


    this.positionsecondFormGroup = this._formBuilder.group({
      positionsecondCtrl: ['', Validators.required]
    });

    // optional
    this.optionalsecondFormGroup = this._formBuilder.group({
      optionalsecondCtrl: ['', Validators.required]
    });


    this.editablesecondFormGroup = this._formBuilder.group({
      editablesecondCtrl: ['', Validators.required]
    });

    this.customizesecondFormGroup = this._formBuilder.group({
      customizesecondCtrl: ['', Validators.required]
    });


    this.errorsecondFormGroup = this._formBuilder.group({
      errorsecondCtrl: ['', Validators.required]
    });
  }
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      console.log('Selected file:', this.selectedFile);
      // You can perform additional actions here, such as validating the file
    }
  }
onSubmit() {
  if (this.selectedFile) {
    //TODO condition on filetype
    const formData = new FormData();
    formData.append('file', this.selectedFile);
    formData.append('firstName', this.firstName);
    formData.append('lastName', this.lastName);
    formData.append('email', this.email);
    formData.append('password',this.password)
    this.authService.connectCluster(formData).subscribe({
      next: (data) => {
        this.data = data.message;
        console.log(data.message);
        this.openSnackBar(this.data);
        this.visible=true;
        localStorage.setItem('Cluster','true')

      },
      error: (error) => {
        this.errorMessage = error.error.message;
        console.log(error.error.message)
        this.openSnackBar(error.error.message)

      },
      complete: () => {
        console.log('Request completed'); // Optional
      }
    });
    this.authService.changeClusterState().subscribe({
      next: (data) => {
        console.log('Cluster status set to: '+data.message);

      },
      error: (error) => {
        console.log(error.error.message)
      },
      complete: () => {
        console.log('Request completed');
      }
    });
    
  }
}
openSnackBar(message: string) {
  this.snackBar.open(message, "dismiss", {
    duration: 2000,
  });
}
}
