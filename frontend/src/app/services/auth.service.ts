import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  role?: string;
}

export interface AuthResponse {
  token?: string;
  message: string;
  userId?: number;
  email?: string;
  fullName?: string;
  role?: string;
  verified?: boolean;
  profilePicture?: string;
  bio?: string;
  balance?: number;
}

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: string;
  verified: boolean;
  profilePicture?: string;
  bio?: string;
  balance?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) {
      this.setCurrentUserFromToken(token);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          this.setCurrentUserFromToken(response.token);
        }
      })
    );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData, { headers });
  }

  getCurrentUser(): Observable<AuthResponse> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`, { headers });
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('token');
    if (!token) return false;
    
    try {
      const decoded: any = jwtDecode(token);
      const isExpired = decoded.exp * 1000 < Date.now();
      return !isExpired;
    } catch {
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserRole(): string | null {
    const user = this.currentUserSubject.value;
    return user ? user.role : null;
  }

  isOrganizer(): boolean {
    return this.getUserRole() === 'ORGANIZER' || this.isAdmin();
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  private setCurrentUserFromToken(token: string): void {
    try {
      const decoded: any = jwtDecode(token);
      const user: User = {
        id: decoded.userId,
        email: decoded.sub,
        fullName: decoded.fullName,
        role: decoded.role,
        verified: decoded.verified
      };
      this.currentUserSubject.next(user);
    } catch (error) {
      console.error('Error decoding token:', error);
      this.currentUserSubject.next(null);
    }
  }

  refreshToken(): Observable<AuthResponse> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {}, { headers }).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          this.setCurrentUserFromToken(response.token);
        }
      })
    );
  }

  verifyEmail(token: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/verify-email?token=${token}`, {});
  }

  forgotPassword(email: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/reset-password`, {
      token,
      newPassword
    });
  }
}
