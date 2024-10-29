# Honkai Website Backend

A Spring Boot backend service for a social media platform focused on the gaming community of Hoyoverse's games, primarily Honkai Star Rail. This service provides REST APIs for user management, post creation, file handling, and social interactions (i.e. comments, likes, notification, etc.).

## Features

### User Management
- User registration and authentication
- Role-based authorization (Admin/Consumer roles)
- Profile management (username, email, bio, profile picture)
- Password encryption using BCrypt
- Basic HTTP Auth security 

### Post System
- Create, read, update, and delete posts
- Image upload support for posts
- Post ownership and permissions
- User-specific post retrieval

### Social Features
- Like system for posts
- Comment system on posts
- User interaction tracking
- Followers/Followings tracking system
- Social Hub Feed

### File Management
- Secure file upload system (<b>locally</b>)
- Support for multiple image formats (JPEG, JPG, PNG, WebP, GIF, BMP, HEIC, HEIF, AVIF, TIFF, SVG)
- Generated unique filenames for security
- Resource handling for stored files

## Tech Stack

### Core Dependencies
```xml
<dependencies>
    <!--  Spring Boot Essentials  -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!--  Database (including testing database)  -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Addons -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## Setup and Configuration

1. Configure your MySQL database settings in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

2. Set up file upload directory in `application.properties`:
```properties
file.upload-dir=./uploads
```

3. Allow multipart files and size restrictions in `application.properties`:
```properties
# Enable multipart support
spring.servlet.multipart.enabled=true

# Upload File Restriction
spring.servlet.multipart.max-file-size=##MB 
spring.servlet.multipart.max-request-size=##MB
```

4. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

## API Endpoints

### User Management
- `POST /api/users/createUser` - Register new user
- `POST /api/users/createAdmin` - Create admin user (Admin only)
- `POST /api/users/{userId}/profile-pic` - Change user profile picture
- `POST /api/users/{userId}/roles/{role}` - Change user roles (Admin only)
- `GET /api/users` - Get all users
- `GET /api/users/id/{userId}` - Get user by ID
- `GET /api/users/name/{username}` - Get user by username
- `PUT /api/users/{userId}` - Update user details (Admin only)
- `DELETE /api/users/{userId}` - Delete user (Admin only)
- `DELETE /api/users/{userId}/roles/{role}` - Delete user roles (Admin only)

### Posts
- `GET /api/posts` - Get all posts (Admin only)
- `GET /api/posts/user/{userId}` - Get user's posts
- `POST /api/posts/user/{userId}` - Create new post
- `PUT /api/posts/{postId}` - Update post
- `DELETE /api/posts/{postId}` - Delete post

### Likes
- `POST /api/likes/user/{userId}/like/{postId}` - User like a post.
- `DELETE /api/likes/user/{userId}/unlike/{postId}` - User unlike a post, that's been liked.
- `GET /api/likes/user/{userId}/like/{postId}` - Check if user like this post.

### Comments
- `POST /api/comments/user/{userId}/comment/{postId}` - User comment on a post.
- `DELETE /api/comments/{commentId}/user/{userId}` - Only the user (themselves) can delete their comment.
- `GET /api/comments/post/{postId}` - Check the user's comment on this post, if exists.

### Follow
- `POST /api/follow/user/{userId}/follow/{username}` - Current user will follow a targeted user.
- `GET /api/follow/user/{username}/followers` - Get list of users following this user.
- `GET /api/follow/user/{username}/followings` - Get list of users that this person is following.
- `GET /api/follow/user/{userId}/check/{username}` - Check if current user is following this targeted user.
- `DELETE /api/follow/user/{userId}/unfollow/{username}` - Current user will unfollow a targeted user.

### File Management
- `POST /api/files/upload` - Upload file
- `GET /api/files/{fileName}` - Get uploaded file

## To-Do List

### Security Enhancements
- [x] Set up basic authentication
- [x] Implement role-based authorization
- [x] Configure CORS settings
- [ ] Implement JWT or OAuth 2.0 authentication
- [ ] Add password reset functionality with email verification
- [ ] Add request validation and sanitization
- [ ] Implement rate limiting for API endpoints

### Feature Additions
- [x] Implement file upload system
- [x] Add user roles and permissions
- [x] Create post management system
- [x] Implement comments and likes system
- [ ] Add pagination for posts and comments
- [ ] Implement user search functionality
- [ ] Add post categories/tags
- [ ] Implement user blocking system
- [ ] Add SSO Sign-in with Google, Twitter (X), etc.

### Performance Improvements
- [x] Configure proper database relationships
- [ ] Implement caching for frequently accessed data
- [ ] Optimize database queries
- [ ] Implement API response pagination
- [ ] Utilize AWS E2 Service, for storing all files.

### Code Quality
- [x] Implement proper error handling
- [x] Add service layer abstraction
- [ ] Add comprehensive unit tests
- [ ] Implement integration tests
- [ ] Add API documentation (Swagger/OpenAPI)

### Database Optimizations
- [x] Set up proper entity relationships
- [x] Optimize entity relationships

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Create a new Pull Request

## Project Status

This is an unofficial, non-commercial project designed and implemented independently. The project has no affiliation with <b>Honkai Star Rail</b> or its parent company, <b>Hoyoverse</b>.

## Rights and Usage
All rights reserved. This code is provided for viewing and educational purposes only. No part of this codebase may be reproduced, distributed, or modified without explicit written permission from the author.

**Note:**
- This is a personal project and is not officially licensed or affiliated with any company
- The code is shared publicly for portfolio and educational purposes
- Unauthorized use, modification, or distribution of this code is prohibited
- For inquiries about using this code, please contact the author directly

© 2024 Johnny Nguyen. All rights reserved.
