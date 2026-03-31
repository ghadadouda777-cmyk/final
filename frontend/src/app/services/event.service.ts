import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Event {
  id: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  price: number;
  capacity: number;
  registrationDeadline: string;
  tags: string[];
  category: string;
  status: string;
  isOnline: boolean;
  meetingLink?: string;
  bannerImage?: string;
  location?: {
    address: string;
    city: string;
    country: string;
    coordinates?: {
      lat: number;
      lng: number;
    };
  };
  organizer?: {
    id: number;
    fullName: string;
    email: string;
    profilePicture?: string;
  };
  averageRating?: number;
  totalRegistrations?: number;
  ticketsSold?: number;
  createdAt: string;
  updatedAt: string;
}

export interface EventCreateRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  price: number;
  capacity: number;
  registrationDeadline: string;
  tags: string[];
  category: string;
  isOnline: boolean;
  meetingLink?: string;
  location: {
    address: string;
    city: string;
    country: string;
    coordinates?: {
      lat: number;
      lng: number;
    };
  };
}

export interface EventUpdateRequest extends EventCreateRequest {}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8081/api/events';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }

  getEventById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`);
  }

  createEvent(formData: FormData): Observable<Event> {
    const headers = this.getAuthHeaders();
    return this.http.post<Event>(this.apiUrl, formData, { headers });
  }

  updateEvent(id: number, formData: FormData): Observable<Event> {
    const headers = this.getAuthHeaders();
    return this.http.put<Event>(`${this.apiUrl}/${id}`, formData, { headers });
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  publishEvent(id: number): Observable<Event> {
    return this.http.post<Event>(`${this.apiUrl}/${id}/publish`, {}, {
      headers: this.getAuthHeaders()
    });
  }

  cancelEvent(id: number): Observable<Event> {
    return this.http.post<Event>(`${this.apiUrl}/${id}/cancel`, {}, {
      headers: this.getAuthHeaders()
    });
  }

  getMyEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/my-events`, {
      headers: this.getAuthHeaders()
    });
  }

  getUpcomingEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/upcoming`);
  }

  searchEvents(keyword: string): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/search?keyword=${keyword}`);
  }

  getEventsByCategory(category: string): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/category/${category}`);
  }

  getOnlineEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/online`);
  }

  getEventsByCity(city: string): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/city/${city}`);
  }

  getEventsBetweenDates(startDate: string, endDate: string): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/between-dates?startDate=${startDate}&endDate=${endDate}`);
  }

  getEventsByTags(tags: string[]): Observable<Event[]> {
    const params = tags.join(',');
    return this.http.get<Event[]>(`${this.apiUrl}/tags?tags=${params}`);
  }
}
