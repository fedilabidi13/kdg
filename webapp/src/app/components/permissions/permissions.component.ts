import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-permissions',
  templateUrl: './permissions.component.html',
  styleUrls: ['./permissions.component.scss']
})
export class PermissionsComponent implements OnInit{
  email!: any;
  disabled = true;
  ngOnInit(): void {
    this.email = localStorage.getItem('email');
  }
  handleChange(event: any) {
    this.disabled = event.checked;
  }

}
