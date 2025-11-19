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
# 4. **Authentication Microservices Component Diagram**

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

# 5. **Payment Microservices Component Diagram**

This diagram details the internal structure of the **Payment Service**, including payment validation logic, gateway integrations,  
subscription workflows, and event handling.

### **Purpose**
- Break down the payment workflow and its subcomponents.
- Explain integrations with VNPay/NAPAS and internal microservices.
- Show how financial events are processed, verified, and stored.
- Demonstrate communication with Notification, Subscription, and other services (if applicable).

### **Why It Matters**
Payment processing requires:
- Reliability  
- Auditability  
- Idempotency  
- Fault tolerance  

This diagram ensures:
- Clear visibility into payment operations  
- Proper handling of callbacks  
- Safe, consistent transaction processing  
- Logical separation of billing, invoicing, and subscription logic  

It is essential for financial compliance and secure operation.

---

# 📌 Summary Table

| Diagram | Description | Why It’s Important |
|--------|-------------|---------------------|
| **System Context Diagram** | Shows entire system boundary and external actors. | Establishes high-level understanding for all stakeholders. |
| **Front-End Modularized Container Diagram** | Breakdown of UI modules and front-end boundaries. | Ensures scalable, maintainable front-end architecture. |
| **Microservices Container Diagram** | Maps all microservices, databases, and external systems. | Clarifies responsibilities and integration patterns. |
| **Authentication Component Diagram** | Detailed breakdown of authentication logic and Google SSO flow. | Critical for securing user identity and access. |
| **Payment Component Diagram** | Detailed structure of payment workflow and gateway integration. | Ensures correctness and reliability of financial transactions. |

---