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

Service Name	            Responsibility

Authorization Service	    Manages access control and handles external API integrations, ensuring secure communication between internal services and third-party systems.

Authentication Service      Handles registration, login, and user authentication.

Applicant Service	        Manages applicant profiles and resumes.

Job Service	                Handles job listing, search, and application management.

Premium Applicant Service   Service	Manages premium applicant subscriptions and integrates with Payment Gateway.

Payment Service	            Processes transactions and payments for premium subscriptions. Integrates with external payment gateways (e.g., NAPAS or PayPal).

Application Service         Manages the job application process, linking applicants to job postings and tracking their application statuses.

Notification Service        Sends notifications (email, SMS, or in-app) for registration confirmations, job updates, and payment alerts.

3️⃣ Component Diagram