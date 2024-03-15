import {inject} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
  CanActivateFn, ActivatedRoute, mapToCanActivate
} from '@angular/router';
import { AuthService } from '../auth/auth.service';


export const AuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  let k8sConnected ="";
  authService.isClusterConnected()
  if (!authService.isClusterConnected()){
    router.navigate(['/connect-cluster'])
    return false;
  }
  if (authService.isLoggedIn()) {
    return true;
  } else {
    router.navigate(['/sign-in']);
    return false;
  }
}