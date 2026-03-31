import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EventService, Event } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  featuredEvents: Event[] = [];
  upcomingEvents: Event[] = [];
  onlineEvents: Event[] = [];
  isLoading = false;

  constructor(private eventService: EventService, private authService: AuthService) {}

  ngOnInit(): void {
    // Pas de chargement pour le design exact
    this.isLoading = false;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
