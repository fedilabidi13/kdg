import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { User } from '../shared/models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticated = false;
  private isClusterPresent !:any;
  private authSecretKey = 'Bearer Token';
  private url : string = 'http://localhost:8081'
  constructor(private httpClient: HttpClient) { 
    this.isAuthenticated = localStorage.getItem(this.authSecretKey)!='null';
    this.isClusterPresent = localStorage.getItem('Cluster');
  }
  isLoggedIn(){
    return this.isAuthenticated;
  }
  isClusterConnected(): boolean{
      this.k8sChecker().subscribe({
        next: (data) => {
        this.isClusterPresent= this.parseBoolean(data.message)
        console.log(data)  
        },
        error: (error) => {
          console.log(error)
  
        },
        complete: () => {
          console.log('Request completed'); 
        }
      });
      return this.isClusterPresent;
  }
  k8sChecker(){
    return this.httpClient.get<any>(this.url+'/auth/is-cluster-connected')
  }
  connectCluster(formData: FormData): Observable<any> {
    return this.httpClient.post<any>(this.url+'/auth/connect-cluster', formData);
  }
  changeClusterState(){
    return this.httpClient.post<any>(this.url+'/auth/change-cluster-state',{})
  }
  login(formData: FormData): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data; charset=utf-8');
    return this.httpClient.post<any>(this.url+'/auth/authenticate', formData, {headers: headers});
  }
  registerUser(formData: FormData): Observable<any>{
    const token = localStorage.getItem('Bearer Token')
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.httpClient.post(this.url+'/admin/register-user', formData, {headers: headers})
  }
  banUser(email: string): Observable<any>{
    const token = localStorage.getItem('Bearer Token')
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.httpClient.post(this.url+'/admin/ban-user/'+email, {}, {headers: headers})

  }
  unBanUser(email: string): Observable<any>{
    const token = localStorage.getItem('Bearer Token')
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.httpClient.post(this.url+'/admin/unban-user/'+email, {}, {headers: headers})

  }
  listUsers(): Observable<Object> {
    const token = localStorage.getItem('Bearer Token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get(this.url + '/admin/list-users',{headers: headers})
    .pipe(map((res:any)=>{
      return res;
    }));
  }
  parseBoolean(str: string): boolean {
    return str.trim().toLowerCase() === 'true';
}
getClusterData():Observable<any>{
  return this.httpClient.get<any>(this.url+'/auth/cluster-info')
}
}