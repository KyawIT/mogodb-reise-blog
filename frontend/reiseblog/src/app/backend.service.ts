import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BackendService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private httpClient:HttpClient) { }

  public httpGetRequest(endpoint:string): Promise<any> | void {
    return new Promise<any>((resolve, reject) => {
      this.httpClient.get(this.apiUrl + endpoint).subscribe({
        next: (response) => {
          resolve(response);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }
  public httpPostRequest(endpoint:string, body:any): Promise<any> | void {
    return new Promise<any>((resolve, reject) => {
      this.httpClient.post(this.apiUrl + endpoint, JSON.stringify(body), {
        headers: { 'Content-Type': 'application/json', 'charset': 'utf-8' }
      }).subscribe({
        next: (response) => {
          resolve(response);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }
  public httpPutRequest(endpoint:string, body:any): Promise<any> | void {
    return new Promise<any>((resolve, reject) => {
      this.httpClient.put(this.apiUrl + endpoint, JSON.stringify(body), {
        headers: { 'Content-Type': 'application/json', 'charset': 'utf-8' }
      }).subscribe({
        next: (response) => {
          resolve(response);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }  
  public httpDeleteRequest(endpoint:string): Promise<any> | void {
    return new Promise<any>((resolve, reject) => {
      this.httpClient.delete(this.apiUrl + endpoint).subscribe({
        next: (response) => {
          resolve(response);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }
  public httpPatchRequest(endpoint:string, body:any): Promise<any> | void {
    return new Promise<any>((resolve, reject) => {
      this.httpClient.patch(this.apiUrl + endpoint, JSON.stringify(body), {
        headers: { 'Content-Type': 'application/json', 'charset': 'utf-8' }
      }).subscribe({
        next: (response) => {
          resolve(response);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }  
}
