import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { EventService, EventCreateRequest } from '../../../services/event.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-event-create',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './event-create.component.html',
  styleUrls: ['./event-create.component.scss']
})
export class EventCreateComponent implements OnInit {
  eventForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  selectedFile: File | null = null;
  previewUrl: string | null = null;

  categories = [
    'MUSIC_CONCERTS',
    'CONFERENCES_BUSINESS', 
    'EDUCATION_CLASSES',
    'WEDDINGS_CELEBRATIONS',
    'SPORTS_WELLNESS',
    'ARTS_CULTURE',
    'GASTRONOMY_FOOD',
    'TECHNOLOGY_GAMING',
    'COMMUNITY_SOCIAL',
    'TRAVEL_OUTDOORS'
  ];

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private authService: AuthService,
    private router: Router
  ) {
    this.eventForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      price: [0, [Validators.min(0)]],
      capacity: [1, [Validators.min(1)]],
      registrationDeadline: ['', Validators.required],
      tags: [''],
      category: ['CONFERENCES_BUSINESS', Validators.required],
      isOnline: [false],
      meetingLink: [''],
      locationAddress: ['', Validators.required],
      locationCity: ['', Validators.required],
      locationCountry: ['', Validators.required],
      locationLat: [null],
      locationLng: [null]
    });
  }

  ngOnInit(): void {
    const userRole = this.authService.getUserRole();
    if (userRole !== 'ORGANIZER' && userRole !== 'ADMIN') {
      this.router.navigate(['/dashboard']);
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.previewUrl = URL.createObjectURL(file);
    }
  }

  removeImage(): void {
    this.selectedFile = null;
    this.previewUrl = null;
  }

  onSubmit(): void {
    if (this.eventForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formData = this.prepareFormData();
    
    this.eventService.createEvent(formData).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = 'Événement créé avec succès!';
        setTimeout(() => {
          this.router.navigate(['/organizer/my-events']);
        }, 2000);
      },
      error: (error: any) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de la création de l\'événement';
      }
    });
  }

  private prepareFormData(): FormData {
    const formData = new FormData();
    
    const eventData: EventCreateRequest = {
      title: this.eventForm.get('title')?.value,
      description: this.eventForm.get('description')?.value,
      startDate: this.eventForm.get('startDate')?.value,
      endDate: this.eventForm.get('endDate')?.value,
      price: this.eventForm.get('price')?.value,
      capacity: this.eventForm.get('capacity')?.value,
      registrationDeadline: this.eventForm.get('registrationDeadline')?.value,
      tags: this.eventForm.get('tags')?.value.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag),
      category: this.eventForm.get('category')?.value,
      isOnline: this.eventForm.get('isOnline')?.value,
      meetingLink: this.eventForm.get('meetingLink')?.value,
      location: {
        address: this.eventForm.get('locationAddress')?.value,
        city: this.eventForm.get('locationCity')?.value,
        country: this.eventForm.get('locationCountry')?.value,
        coordinates: this.eventForm.get('locationLat')?.value ? {
          lat: Number(this.eventForm.get('locationLat')?.value),
          lng: Number(this.eventForm.get('locationLng')?.value)
        } : undefined
      }
    };

    formData.append('event', JSON.stringify(eventData));
    
    if (this.selectedFile) {
      formData.append('bannerImage', this.selectedFile);
    }

    return formData;
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

  cancel(): void {
    this.router.navigate(['/organizer/my-events']);
  }
}
