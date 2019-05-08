import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SignUpInfo} from '../sign-up-info';
import {SignInInfo} from '../sign-in-info';
import {JwtResponse} from '../jwt-response';

const httpOptions ={
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private signUpURL: string = 'http://localhost:8080/api/auth/signup';
  private signInURL: string = 'http://localhost:8080/api/auth/signin';

  constructor(private httpClient: HttpClient) { }

  signUp(info: SignUpInfo): Observable<string> {
    return this.httpClient.post<string>(this.signUpURL, info, httpOptions);
  }

  attemptAuthentication(credentials: SignInInfo): Observable<JwtResponse> {
    return this.httpClient.post<JwtResponse>(this.signInURL, credentials, httpOptions);

  }
}
