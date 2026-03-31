import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EventService } from '../../../services/event.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-my-events',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-events.component.html',
  styleUrls: ['./my-events.component.scss']
})
export class MyEventsComponent implements OnInit {
  events: any[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private eventService: EventService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMyEvents();
  }

  loadMyEvents(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.eventService.getMyEvents().subscribe({
      next: (events: any[]) => {
        this.events = events;
        this.isLoading = false;
      },
      error: (error: any) => {
        this.isLoading = false;
        this.errorMessage = 'Erreur lors du chargement de vos événements';
        console.error('Error loading events:', error);
      }
    });
  }

  editEvent(eventId: number): void {
    this.router.navigate(['/organizer/edit-event', eventId]);
  }

  deleteEvent(eventId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet événement? Cette action est irréversible.')) {
      this.isLoading = true;
      
      this.eventService.deleteEvent(eventId).subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Événement supprimé avec succès';
          this.loadMyEvents();
          
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = 'Erreur lors de la suppression de l\'événement';
          console.error('Error deleting event:', error);
        }
      });
    }
  }

  publishEvent(eventId: number): void {
    this.isLoading = true;
    
    this.eventService.publishEvent(eventId).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Événement publié avec succès';
        this.loadMyEvents();
        
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error: any) => {
        this.isLoading = false;
        this.errorMessage = 'Erreur lors de la publication de l\'événement';
        console.error('Error publishing event:', error);
      }
    });
  }

  cancelEvent(eventId: number): void {
    if (confirm('Êtes-vous sûr de vouloir annuler cet événement?')) {
      this.isLoading = true;
      
      this.eventService.cancelEvent(eventId).subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Événement annulé avec succès';
          this.loadMyEvents();
          
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = 'Erreur lors de l\'annulation de l\'événement';
          console.error('Error canceling event:', error);
        }
      });
    }
  }

  viewRegistrations(eventId: number): void {
    this.router.navigate(['/organizer/event-registrations', eventId]);
  }

  createNewEvent(): void {
    this.router.navigate(['/organizer/create-event']);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PUBLISHED':
        return 'bg-green-100 text-green-800';
      case 'DRAFT':
        return 'bg-gray-100 text-gray-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusDisplayName(status: string): string {
    switch (status) {
      case 'PUBLISHED':
        return 'Publié';
      case 'DRAFT':
        return 'Brouillon';
      case 'CANCELLED':
        return 'Annulé';
      default:
        return status;
    }
  }

  getCategoryDisplayName(category: string): string {
    switch (category) {
      case 'MUSIC_CONCERTS':
        return 'Concerts & Musique';
      case 'CONFERENCES_BUSINESS':
        return 'Conférences & Business';
      case 'EDUCATION_CLASSES':
        return 'Éducation & Cours';
      case 'WEDDINGS_CELEBRATIONS':
        return 'Mariages & Célébrations';
      case 'SPORTS_WELLNESS':
        return 'Sports & Bien-être';
      case 'ARTS_CULTURE':
        return 'Arts & Culture';
      case 'GASTRONOMY_FOOD':
        return 'Gastronomie & Food';
      case 'TECHNOLOGY_GAMING':
        return 'Technologie & Gaming';
      case 'COMMUNITY_SOCIAL':
        return 'Communauté & Social';
      case 'TRAVEL_OUTDOORS':
        return 'Voyage & Plein air';
      default:
        return category;
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  isEventInFuture(startDate: string): boolean {
    return new Date(startDate) > new Date();
  }
}
