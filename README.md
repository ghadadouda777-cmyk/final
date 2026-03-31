# Eventify - Application de Gestion d'Événements

## Description

Eventify est une application complète de gestion d'événements transformée de la stack MERN (MongoDB, Express, React, Node.js) vers une architecture moderne Spring Boot + Angular + PostgreSQL.

## Architecture

### Backend (Spring Boot + PostgreSQL)
- **Framework**: Spring Boot 3.2.5
- **Base de données**: PostgreSQL
- **Sécurité**: Spring Security avec JWT
- **ORM**: JPA/Hibernate
- **Upload d'images**: Cloudinary
- **Email**: Spring Mail
- **Validation**: Bean Validation

### Frontend (Angular)
- **Framework**: Angular 21
- **Styling**: Tailwind CSS
- **HTTP Client**: Angular HttpClient
- **Gestion d'état**: Services Angular avec RxJS
- **Authentification**: JWT avec interceptors

## Fonctionnalités

### 🎯 Gestion des Événements
- Création, modification, suppression d'événements
- Recherche avancée par catégorie, date, lieu
- Événements en ligne et physiques
- Gestion des capacités et inscriptions

### 👥 Gestion des Utilisateurs
- Authentification avec JWT
- Rôles : Participant, Organisateur, Admin
- Profils personnalisables
- Vérification par email

### 📝 Inscriptions
- Inscription aux événements
- Gestion des places disponibles
- Suivi des participations
- Annulation d'inscription

### 💬 Communication
- Système de commentaires
- Évaluations et avis
- Notifications par email

### 📊 Dashboard
- Statistiques pour les organisateurs
- Vue d'ensemble des événements
- Analytics des participations

## Structure du Projet

```
eventifyfinal/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/eventify/
│   │   ├── controller/         # Contrôleurs REST
│   │   ├── service/           # Services métier
│   │   ├── repository/        # Repositories JPA
│   │   ├── entity/           # Entités JPA
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── security/         # Configuration sécurité
│   │   └── config/           # Configuration générale
│   └── pom.xml               # Maven configuration
├── frontend/                  # Angular Frontend
│   ├── src/app/
│   │   ├── components/        # Composants réutilisables
│   │   ├── pages/           # Pages de l'application
│   │   ├── services/        # Services Angular
│   │   └── models/          # Interfaces TypeScript
│   └── package.json          # NPM configuration
└── client/                    # Ancien projet React (conservé)
```

## Installation et Configuration

### Prérequis
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

### Backend

1. **Configurer la base de données PostgreSQL**:
```sql
CREATE DATABASE event_management;
CREATE USER eventify WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE event_management TO eventify;
```

2. **Configurer les variables d'environnement**:
```properties
# backend/src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/event_management
spring.datasource.username=eventify
spring.datasource.password=password
```

3. **Lancer le backend**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Le backend sera disponible sur `http://localhost:8080`

### Frontend

1. **Installer les dépendances**:
```bash
cd frontend
npm install
```

2. **Lancer le frontend**:
```bash
ng serve
```

Le frontend sera disponible sur `http://localhost:4200`

## Configuration Cloudinary

Pour l'upload d'images, configurez vos identifiants Cloudinary dans `application.properties`:

```properties
cloudinary.cloud.name=votre_cloud_name
cloudinary.api.key=votre_api_key
cloudinary.api.secret=votre_api_secret
```

## Configuration Email

Configurez les paramètres SMTP dans `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre_email@gmail.com
spring.mail.password=votre_app_password
```

## API Documentation

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription
- `GET /api/auth/me` - Profil utilisateur
- `POST /api/auth/refresh` - Rafraîchir token

### Événements
- `GET /api/events` - Lister les événements
- `GET /api/events/{id}` - Détails événement
- `POST /api/events` - Créer événement (authentifié)
- `PUT /api/events/{id}` - Modifier événement (organisateur)
- `DELETE /api/events/{id}` - Supprimer événement (organisateur)

### Inscriptions
- `POST /api/registrations/events/{eventId}` - S'inscrire
- `DELETE /api/registrations/{id}` - Annuler inscription
- `GET /api/registrations/my-registrations` - Mes inscriptions

## Déploiement

### Backend (Production)
```bash
mvn clean package
java -jar target/eventify-backend-0.0.1-SNAPSHOT.jar
```

### Frontend (Production)
```bash
ng build --configuration production
# Déployer le contenu de dist/frontend/
```

## Tests

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
ng test
```

## Migration depuis MERN

### Changements principaux :
1. **MongoDB → PostgreSQL**: Migration vers base de données relationnelle
2. **Express → Spring Boot**: Migration vers framework Java enterprise
3. **React → Angular**: Migration vers framework TypeScript enterprise
4. **Redux → Services Angular**: Remplacement de Redux par services avec RxJS
5. **Mongoose → JPA/Hibernate**: Migration ORM Java
6. **Socket.io → WebSocket**: Communication temps réel avec WebSocket Spring

### Avantages de la nouvelle architecture :
- **Performance**: Spring Boot offre de meilleures performances
- **Sécurité**: Spring Security plus robuste
- **Maintenabilité**: Code typé avec TypeScript et Java
- **Scalabilité**: Architecture plus modulaire
- **Entreprise**: Stack plus adaptée aux environnements d'entreprise

## Contribuer

1. Forker le projet
2. Créer une branche feature
3. Commiter les changements
4. Pusher vers la branche
5. Créer une Pull Request

## Licence

Ce projet est sous licence MIT.
