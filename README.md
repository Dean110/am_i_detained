# Am I Detained - Check-In Alert System

A multi-user check-in alert system that automatically notifies emergency contacts if a user fails to check in by their scheduled daily time or manually triggers an alert.

## Quick Links
- [Deployment Guide](DEPLOYMENT.md) - AWS Elastic Beanstalk deployment instructions
- [Acceptance Tests](ACCEPTANCE_TESTS.md) - Test documentation (if exists)
- [API Documentation](http://localhost:8080/swagger-ui.html) - Swagger UI (when running)

## Features

- **OAuth2 Authentication**: Login with Google, Facebook, or Instagram
- **User Profile Management**: Configure phone number, alias, location, and custom alert messages
- **Emergency Contacts**: Manage contacts who will be notified in case of emergency
- **Automated Check-ins**: Set daily check-in times with grace periods
- **Escalation System**: Sends reminder first, then alerts contacts if no response
- **Manual Alerts**: Trigger emergency alerts immediately when needed
- **Location Support**: Manual text entry or GPS coordinates via API
- **History Tracking**: View check-in and alert history
- **REST API**: Full API access for mobile apps or integrations
- **Web UI**: Bootstrap-based responsive interface

## Technology Stack

- **Spring Boot 4.0.1** with Java 25
- **Spring Security** with OAuth2 Client
- **Spring Data JPA** with H2 (dev) / PostgreSQL (prod)
- **Spring Batch** for scheduled monitoring
- **Thymeleaf** for server-side templates
- **Bootstrap 5** for UI styling
- **SpringDoc OpenAPI** for API documentation

## Prerequisites

- Java 25 or higher
- Gradle 9.2.1 or higher
- PostgreSQL (for production)
- OAuth2 credentials from Google, Facebook, and/or Instagram

## OAuth2 Setup

### Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Go to Credentials → Create Credentials → OAuth 2.0 Client ID
5. Set authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Copy Client ID and Client Secret

### Facebook OAuth2
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app or select existing
3. Add Facebook Login product
4. Set Valid OAuth Redirect URIs: `http://localhost:8080/login/oauth2/code/facebook`
5. Copy App ID and App Secret

### Instagram OAuth2
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app with Instagram Basic Display
3. Configure OAuth Redirect URI: `http://localhost:8080/login/oauth2/code/instagram`
4. Copy Instagram App ID and App Secret

## Configuration

### Development (H2 Database)

Set environment variables or update `application.properties`:

```bash
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
export FACEBOOK_CLIENT_ID=your-facebook-app-id
export FACEBOOK_CLIENT_SECRET=your-facebook-app-secret
export INSTAGRAM_CLIENT_ID=your-instagram-app-id
export INSTAGRAM_CLIENT_SECRET=your-instagram-app-secret
```

### Production (PostgreSQL)

Set additional environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/am_i_detained
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your-password
```

## Running the Application

### Development Mode

```bash
./gradlew bootRun
```

Access the application at `http://localhost:8080`

### Production Mode

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Or build and run the JAR:

```bash
./gradlew build
java -jar build/libs/am_i_detained-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

API documentation is available at:

```
http://localhost:8080/v3/api-docs
```

## REST API Endpoints

### User Profile
- `GET /api/user/profile` - Get current user profile
- `PUT /api/user/profile` - Update user profile
- `PUT /api/user/location` - Update GPS location

### Contacts
- `GET /api/contacts` - List all contacts
- `POST /api/contacts` - Create new contact
- `PUT /api/contacts/{id}` - Update contact
- `DELETE /api/contacts/{id}` - Delete contact

### Check-in Schedule
- `GET /api/checkin/schedule` - Get check-in schedule
- `PUT /api/checkin/schedule` - Update check-in schedule

### Check-ins
- `POST /api/checkin` - Manual check-in
- `GET /api/checkin/history` - Get check-in history

### Alerts
- `POST /api/alert` - Trigger emergency alert
- `GET /api/alert/history` - Get alert history

## Web UI Pages

- `/` - Landing page with OAuth login options
- `/dashboard` - Main dashboard with quick actions
- `/profile` - User profile management
- `/contacts` - Emergency contacts management
- `/schedule` - Check-in schedule configuration
- `/history` - Check-in and alert history

## Database Access (Development)

H2 Console is available at `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:am_i_detained`
- Username: `sa`
- Password: (leave blank)

## Monitoring

Spring Boot Actuator endpoints are available at `/actuator`:

- `/actuator/health` - Application health status
- `/actuator/info` - Application information

## How It Works

1. **User Registration**: Users log in via OAuth2 (Google/Facebook/Instagram)
2. **Profile Setup**: Users configure their profile with phone, alias, location, and custom message
3. **Add Contacts**: Users add emergency contacts with phone numbers and emails
4. **Set Schedule**: Users set daily check-in time and grace period
5. **Enable Monitoring**: Users enable automated check-in monitoring
6. **Check-in Process**:
   - User can manually check in anytime via dashboard or API
   - If user misses check-in deadline, system sends reminder
   - If no response within grace period, system alerts all emergency contacts
7. **Manual Alert**: Users can trigger emergency alert immediately if needed

## Notification System

Currently uses mock implementation that logs to console. To integrate real SMS/Email:

1. Implement `NotificationService` interface
2. Use AWS SNS, Twilio, or other service
3. Remove `@Primary` from `MockNotificationService`
4. Add `@Primary` to your implementation

## Security

- OAuth2 authentication required for all protected endpoints
- Users can only access their own data
- CSRF protection enabled (except H2 console in dev)
- Passwords and secrets managed via environment variables

## License

This project is for educational purposes.
