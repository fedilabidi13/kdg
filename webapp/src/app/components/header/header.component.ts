import { Component } from '@angular/core';
import {CommonModule, NgFor, NgIf} from "@angular/common";
import {RouterModule} from "@angular/router";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {MatListModule} from "@angular/material/list";
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [NgFor, NgIf, RouterModule, CommonModule, MatIconModule, MatMenuModule, MatListModule, MatSelectModule, MatOptionModule],
  templateUrl: './header.component.html',
  styleUrls: []
})
export class HeaderComponent {

}
