import { Routes } from '@angular/router';

// Imports directs des composants
import { EventsListComponent } from './events-list/events-list.component';
import { EventDetailsComponent } from './event-details/event-details.component';
import { CreateEventComponent } from './create-event/create-event.component';

export const eventsRoutes: Routes = [
  { path: '', component: EventsListComponent },
  { path: ':id', component: EventDetailsComponent },
  { path: 'create', component: CreateEventComponent }
];
