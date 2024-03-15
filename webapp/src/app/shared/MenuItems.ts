import { Injectable } from '@angular/core';

export interface Menu {
  state: string;
  name: string;
  type: string;
  icon: string;
}

const MENUITEMS = [
  { state: 'dashboard', name: 'Dashboard', type: 'link', icon: 'account_box' },
  { state: 'users', name: 'Users', type: 'link', icon: 'account_box' },
  { state: 'permissions', type: 'link', name: 'Permissions', icon: 'verified_user' },
  { state: 'cluster-config', type: 'link', name: 'Cluster', icon: 'wb_cloudy' },
  { state: 'history', type: 'link', name: 'History', icon: 'view_list' }
];

@Injectable()
export class MenuItems {
  getMenuitem(): Menu[] {
    return MENUITEMS;
  }
}
