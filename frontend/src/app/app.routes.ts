import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { MyEventsComponent } from './pages/organizer/my-events/my-events.component';
import { EventCreateComponent } from './pages/organizer/event-create/event-create.component';
import { EventEditComponent } from './pages/organizer/event-edit/event-edit.component';

export const routes: Routes = [
  { path: '', redirectTo: '/organizer/my-events', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'events', loadChildren: () => import('./pages/events/events.routes').then(m => m.eventsRoutes) },
  { path: 'dashboard', loadChildren: () => import('./pages/dashboard/dashboard.routes').then(m => m.dashboardRoutes) },
  { path: 'profile', loadChildren: () => import('./pages/profile/profile.routes').then(m => m.profileRoutes) },
  {
    path: 'organizer',
    children: [
      { path: 'my-events', component: MyEventsComponent },
      { path: 'create-event', component: EventCreateComponent },
      { path: 'edit-event/:id', component: EventEditComponent }
    ]
  },
  { path: '**', redirectTo: '/organizer/my-events' }
];
