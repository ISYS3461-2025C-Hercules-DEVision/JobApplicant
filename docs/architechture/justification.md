🧩 System Architecture Justification — Job Applicant Platform

📘 Overview

This document provides justification and explanations for the three architecture diagrams designed for the Job Applicant System, following the C4 model principles:

System Context Diagram (Level 1)

Container Diagram (Microservices Level 2)

Component Diagram (Microservices Level 3)

Each level progressively refines the system’s structure, from a high-level overview to detailed service components.
1️⃣ System Context Diagram (Level 1)
🏗️ Purpose

The System Context Diagram defines the scope and boundaries of the Job Applicant System. It shows how the system interacts with external users and external systems, providing a top-level overview of the environment.

🧭 Description

At this level, the Job Applicant System is treated as a single unit. It identifies key actors (such as Job Applicants, Employers, and Admins) and external systems (like Payment Gateway or Email Notification Service).

👥 Main Interactions

Job Applicants register, log in, manage profiles, search, and apply for jobs.

Employers post jobs and review applicants.

Admins manage system-level settings and user accounts.

External Payment Service handles premium subscriptions.

Notification Service sends confirmation or alert emails.

✅ Justification

Defines clear system boundaries and stakeholders.

Provides a shared understanding for both technical and non-technical audiences.

Serves as a starting point for identifying microservices in lower-level diagrams.

2️⃣ Container Diagram — Microservices Architecture (Level 2)
🏗️ Purpose

The Container Diagram breaks down the monolithic system into independent deployable services (microservices). It illustrates the main building blocks (containers) such as APIs, databases, and external integrations.

🧭 Description

Each major functionality from the functional requirements has been implemented as a separate service, enabling scalability, fault isolation, and independent deployment.

⚙️ Identified Microservices


Authorization Service	    Manages access control and handles external API integrations, ensuring secure communication between internal services and third-party systems.

Authentication Service      Handles registration, login, and user authentication.

Applicant Service	        Manages applicant profiles and resumes.

Job Service	                Handles job listing, search, and application management.

Premium Applicant Service   Service	Manages premium applicant subscriptions and integrates with Payment Gateway.

Payment Service	            Processes transactions and payments for premium subscriptions. Integrates with external payment gateways (e.g., NAPAS or PayPal).

Application Service         Manages the job application process, linking applicants to job postings and tracking their application statuses.

Notification Service        Sends notifications (email, SMS, or in-app) for registration confirmations, job updates, and payment alerts.

3️⃣ Component Diagram 
# 1. **Authentication Microservices Component Diagram**

This diagram zooms into the **Authentication Service** internals, showing controllers, services, repositories, integrations, and token flows.

### **Purpose**
- Clarify separation of responsibilities inside the Authentication Service.
- Show how Google SSO is handled (OAuth2 → ID Token → User Creation → JWT Issuance).
- Visualize interactions with:
  - User Service  
  - Authorization Service  
  - Notification Service  
  - Kafka  
  - Google OAuth Provider  

### **Why It Matters**
Authentication is a high-risk, security-sensitive component.  
This diagram makes it easy to:

- Audit authentication flows  
- Explain token lifecycle (Access Token, Refresh Token)  
- Support onboarding of new developers  
- Document Google SSO integration  
- Ensure compliance with OAuth2, OIDC, and Zero-Trust principles  

It provides depth where the overall container diagram provides breadth.

---

# 2. **Login front-end  Component Diagram**
# Login Module – Component Justification

This section explains the purpose and rationale behind each component within the Login module.

## 1. Login Form
The **Login Form** component is responsible for rendering the user interface for traditional credential-based authentication (email and password).  
It is separated from the business logic to ensure a clean separation of concerns, making the UI easy to update or reuse without modifying the underlying login logic.

## 2. Social Login
The **Social Login** component manages authentication through external identity providers such as Google OAuth.  
Since OAuth flows differ significantly from standard username/password login, this component is isolated to allow independent development and future expansion to additional providers.

## 3. Login Hook
The **Login Hook** encapsulates all stateful login logic, including loading states, error handling, API communication, and updating the global authentication state.  
By moving logic into a hook, the UI components remain simple and stateless, and the hook becomes reusable across different pages or containers.

## 4. Login Service
The **Login Service** acts as the communication layer between the frontend and the backend authentication API.  
It centralizes all login-related API operations, ensuring that any updates to endpoints, payload structures, or authentication logic can be applied in one place without affecting UI components.

# 3. **Registration front-end  Component Diagram**
# Registration Module  – Component Justification

This section explains the purpose and rationale behind each component within the Registration module.  
Only registration-specific components are described here.

## 1. Social Register
The **Social Register** component manages user registration through external identity providers such as Google OAuth.  
Since the OAuth onboarding flow (token exchange, redirect handling, user profile retrieval) is fundamentally different from manual account creation, this component is isolated to allow independent development and future extension to additional providers.

## 2. Register Hook
The **Register Hook** (shared with the login module) encapsulates the core authentication logic such as API communication, loading states, error handling, and state updates.  
Reusing it in the Registration module avoids duplicated logic and ensures that authentication behaviour remains consistent between Login and Registration features.

## 3. Register Service
The **Register Service** acts as the communication layer between the frontend and the backend authentication/registration APIs.  
This includes sending registration data, handling OAuth callbacks, and processing backend responses.  
Centralizing these interactions ensures that changes to API endpoints, validation rules, or onboarding workflows can be applied in one place without modifying UI components.
# 4. **Notification front-end  Component Diagram**
# Notification Module – Component Justification

This section explains the purpose and rationale behind each component within the Notification module.  
Only notification-specific components are described here.

## 1. Notification UI
The **Notification UI** component is responsible for displaying notifications to users, such as unread messages, maintenance updates, or payment alerts.  
Separating the UI from the logic ensures that design changes, layout updates, or new display styles can be applied without affecting the underlying notification retrieval or processing logic.

## 2. Notification Hook
The **Notification Hook** encapsulates all logic needed to manage notifications, including:
- Fetching notifications from the backend  
- Tracking read/unread status  
- Handling real-time updates or polling  
- Managing loading or error states  

By keeping this logic in a hook, the Notification UI remains clean, while the hook can be reused across different screens that need notification data.

## 3. Notification Service
The **Notification Service** handles communication with the backend notification API.  
It centralizes all HTTP calls related to:
- Retrieving notifications  
- Marking notifications as read  
- Updating user notification status  

Centralizing API interactions in one service makes it easier to update endpoints, change notification formats, or integrate WebSocket/Kafka-based updates in the future without modifying UI or hook components.

---
# 5. **Authorization Microservice Component Diagram**

This diagram show the structural and functional composition of the Authorization Microservice, performing how the authorization decisions are processed, validated and integrated with a microservice system.

### **Purpose**
- Describe the authorization workflow and its internal layers
- Illustrate integrations with relevant microservices and platform components
- Detail how authorization requests are performed
- Highlight how the service interfaces with external systems

### **Why It Matters**
Authorization microservice must satisfy secure, consistent and scalable access control. This requires:
- Confidentiality and Integrity
- Consistency
- Scalability and Extensibility
  
This diagram ensures:
- Clear visibility into authorization operations
- Safe and efficient communication with other microservices
- Logcical layering of authorization concerns 

---
# 6. **Notification Microservice Component Diagram**

This diagram indicates the internal structure of Notification Service, including meesage routing logic, media configuration and external microservice interactions. 

### **Purpose**
- Break down the notification workflow
- Explain integrations with Kafka, Redis and internal microservices
- Show how notification events are processed and stored
- Demonstrate communication with Applicant, Authentication and Payment and others microservices

### **Why It Matters**
This notification microservice requires: 
- Scalability
- Responsiveness
- Decoupling
- Reliability

This diagram ensures: 
- Clear visibility of how notifications are operated
- Proper handling of asynchronous events
- Safe, consistent message delivery
- Logical seperation of interface, service logic, and media configuration

---

# 7. **Admin Microservice Component Diagram**

This diagram illustrates the internal structure of Admin Service, including inquiry handling logic and inter-service communication

### **Purpose**
- Break down the admin workflow and its subcomponents
- Show how dmin events are handled, routed and stored
- Demonstrate communication with Application and Notification microservices.

### **Why It Matters**
Admin Microservice requires:
- Security and Manipulation
- Responsiveness
- Modularity
- Auditability  

This diagram ensures: 
- Clear visibility into admin operations
- Logical seperation of interface, service logic and configuration layers
- Safe, consistent inquiry management

---

# 8. **Profile Front-end Component Diagram**
# Profile Module - Component Justification

This section explains the purpose and rationale behind each component within the Profile module.

## 1. Profile Detail
This component is responsible for rendering user's profile information, such as name, email and other personal data. 
It is designed as a presentation component, which seperated from data-fetching logic, to ensure a clean seperation of concerns. It would make the code easy to style, reuse in other containers without affecting the logic.

## 2. Profile Detail Hook
The Profile Detail Hook encapsulates all stateful logic related to loading user profile data.
It manages asynschronous API calls, loading states, and error handling. By splitting this logic in a hook, the Profile Detail components remains stateless and focused mainly on UI rendering.

## 3. Profile Detail Service
This component acts as the communication layer between the front-end and back-end profile API.
It centralizes all operations for fetching user profile data, ensuring that any changes to endpoints or payload formats are confined in one place.

## 4. Profile Update
It provides the interface for editting or deleting user profile information.
It is kept isolately from update profile logic to allow flexible UI changes without affecting the logic of back-end operation.

## 5. Profile Update Hook
The Profile Update Hook manages state transitions, form validation, and submission logic for profile update.
It handles API communication, loading indicators and error feedback.

## 6. Profile Update Service
This is responsible for sending update or delete requests to back-end profile API.
It encapsulates all unusual logic, make it easy to modify request formats or endpoints withoutt touching UI components.

## 7. Profile Avatar
Display the user's avatar.
It is designed as a visual component, allowing flexible styling and placement across the application.

## 8. Profile Avatar Hook
The Hook handles the logic for loading data and managing upload states.
It abstracts asynchronous operations and error handling, keeping the avatar component stateless and focused on rendering.

## 9. Profile Avatar Service
The Profile Avatar Service communicates with back-end logic to fetch or update avatar.
It centralizes all avatar-related API interactions, ensuring consistent behavior and simplifying future enhancements.

---

# 9.  **Application Dashboard Front-end Component Diagram**
# Application Dashboard Module - Component Justification

---

# 10. **Application Form Front-end Component Diagram**
# Application Form Module - Component Justification

---

# 11. **Applicant Service Component Diagram**

### **Purpose**

### **Why It Matters**

---

# 12. **Application Service Component Diagram**

### **Purpose**

### **Why It Matters**

---

# 13. **Premium Applicant Subscription Service Component Diagram**

### **Purpose**

### **Why It Matters**

---

# 14.  **Account Management Front-end Component Diagram**
# Account Management Module - Component Justification

---

# 15.  **Job Listing Front-end Component Diagram**
# Job Listing Module - Component Justification

---

# 16.  **Payment Front-end Component Diagram**
# Payment Module - Component Justification

---
# 📌 Summary Table

| Diagram | Description | Why It’s Important |
|--------|-------------|---------------------|
| **System Context Diagram** | Shows entire system boundary and external actors. | Establishes high-level understanding for all stakeholders. |
| **Front-End Modularized Container Diagram** | Breakdown of UI modules and front-end boundaries. | Ensures scalable, maintainable front-end architecture. |
| **Microservices Container Diagram** | Maps all microservices, databases, and external systems. | Clarifies responsibilities and integration patterns. |
| **Authentication Component Diagram** | Detailed breakdown of authentication logic and Google SSO flow. | Critical for securing user identity and access. |
| **Payment Component Diagram** | Detailed structure of payment workflow and gateway integration. | Ensures correctness and reliability of financial transactions. |
| **Authorization Component Diagram** | Logic for permission control, access based on normal or premium account, policy enforcement. | Essential for enforcing secure access boundaries and protecting sensitive actions. |
| **Notification Component Diagram** | Architecture of notification delivery, media config and event streaming. | Ensures reliable, scalable and decoupled communication across services. |
| **Admin Component Diagram** | Internal structure of admin logic, inquiry handling and service orchestration. | Enables secure, modular and auditable administrative operations. |

---

