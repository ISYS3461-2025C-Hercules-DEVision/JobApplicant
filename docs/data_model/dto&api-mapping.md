### **DM-07 – DTO & API Data Contract Mapping (Internal vs External DTOs)**

**Scope:** Job Applicant subsystem – all microservices shown in ERD v2 (Profile, Auth, Application, Subscription, Payment, Notification, Admin, Authorization).

**Goal:** Define DTOs for:

- Frontend ↔ Backend REST/HTTP APIs (external DTOs)
- Service ↔ Service communication (internal DTOs, events)
- Clarify which entity fields are **never exposed** or must be **obfuscated**.

This is a *logical* mapping document; concrete OpenAPI/Proto files will be created later in CT-02.

---

### **0. Conventions**

- **Naming**
    - External DTOs (public APIs): `XxxPublicDTO`, `XxxRequestDTO`, `XxxResponseDTO`.
    - Internal DTOs (service-to-service, events): `XxxInternalDTO`, `XxxEventPayload`.
    - Persistence entities keep simple names: `Applicant`, `Application`, etc. (see DM‑02).
- **Types**
    - `string` for IDs (`uuid`), dates (`ISO 8601`), enums, email.
    - `number` for numeric values, `boolean` for flags, `array<T>` for lists.
- **Security & privacy**
    - **Never** expose: `passwordHash`, `ssoId`, internal `failedAttempts`, raw `isRevoked`, internal shard info.
    - Token values (access/refresh) are only in **Auth responses** and not logged.
    - Payment DTOs never include card PAN/CVV; only gateway transaction metadata.
- **Serialization**
    - JSON over HTTP for synchronous APIs.
    - JSON payload over Kafka for events (same DTO shape as HTTP where possible).

---

## **1. Profile Service DTOs**

### **1.1 Applicant**

#### **1.1.1 External – ApplicantPublicDTO**

Used by FE to display/edit profile.

```json
{
  "applicantId": "uuid",
  "fullName": "string",
  "email": "string",
  "country": "string",
  "city": "string|null",
  "streetAddress": "string|null",
  "phoneNumber": "string|null",
  "profileImageUrl": "string|null",
  "isActivated": true,
  "createdAt": "2025-11-20T12:00:00Z",
  "updatedAt": "2025-11-21T08:12:00Z"
}

```

- **Source entity:** `Applicant` (DM‑02).
- **Hidden fields:** `passwordHash`, `ssoProvider`, `ssoId`, `shardKey`, `isArchived`, internal audit fields.

#### **Requests**

- **CreateApplicantRequestDTO** (Registration – local account)

```json
{
  "fullName": "string",
  "email": "string",
  "password": "string",
  "country": "string",
  "city": "string|null",
  "streetAddress": "string|null",
  "phoneNumber": "string|null"
}

```

- **UpdateApplicantProfileRequestDTO**

```json
{
  "fullName": "string",
  "city": "string|null",
  "streetAddress": "string|null",
  "phoneNumber": "string|null",
  "country": "string"
}

```

> Note: When country changes, backend triggers shard migration as per DM‑06; FE just sends new value.
> 

#### **1.1.2 Internal – ApplicantInternalDTO**

Used inside Profile Service and for events to Job Manager / Notification.

```json
{
  "applicantId": "uuid",
  "fullName": "string",
  "email": "string",
  "country": "string",
  "city": "string|null",
  "skills": ["React", "Kafka"],
  "educationSummary": [
    { "degree": "BSc SE", "institution": "RMIT", "fromYear": 2021, "toYear": 2025 }
  ],
  "workSummary": [
    { "jobTitle": "Backend Dev", "from": "2023-01", "to": "2024-06" }
  ],
  "shardKey": "VN",
  "isActivated": true,
  "createdAt": "2025-11-20T12:00:00Z",
  "updatedAt": "2025-11-21T08:12:00Z"
}

```

- Includes `shardKey` and denormalized skill/education summaries to help Job Manager matching.
- Exposed only via Kafka events, not to FE.

---

### **1.2 EducationDTO & WorkExperienceDTO (External)**

```json
{
  "educationId": "uuid",
  "degree": "string",
  "institution": "string",
  "fromYear": 2021,
  "toYear": 2025,
  "gpa": 85
}

```

```json
{
  "workExpId": "uuid",
  "jobTitle": "string",
  "companyName": "string",
  "from": "2023-01",
  "to": "2024-06",
  "description": "string"
}

```

Both internal and external shapes are identical; no sensitive fields.

---

### **1.3 Resume**

## **ResumePublicDTO (External)**

```json
{
  "resumeId": "uuid",
  "headline": "Backend Developer",
  "objective": "string",
  "education": [EducationDTO],
  "experience": [WorkExperienceDTO],
  "skills": ["React", "Node.js"],
  "certifications": ["AWS Cloud Practitioner"],
  "updatedAt": "2025-11-21T08:12:00Z"
}

```

Internal DTO is the same plus optional internal scoring fields (`matchScore`, `lastIndexedAt`) used by search and recommendation engines.

---

### **1.4 MediaPortfolioDTO (External)**

```json
{
  "mediaId": "uuid",
  "title": "Portfolio Screenshot",
  "description": "string",
  "fileUrl": "https://cdn.example.com/...",
  "mediaType": "image",
  "visibility": "public",
  "uploadDate": "2025-11-21T08:12:00Z"
}

```

Internal variant may include storage provider info and internal flags (`storageBucket`, `virusScanStatus`) which are not exposed.

---

### **1.5 SearchProfile (Premium)**

## **SearchProfilePublicDTO**

```json
{
  "searchProfileId": "uuid",
  "profileName": "Singapore SWE roles",
  "desiredCountry": "SG",
  "desiredMinSalary": 0,
  "desiredMaxSalary": null,
  "jobTitles": ["Software Engineer", "Backend Developer"],
  "technicalBackground": ["Kafka", "React"],
  "employmentStatus": ["Full-time", "Internship"],
  "isActive": true,
  "createdAt": "2025-11-21T08:12:00Z"
}

```

Internal variant adds precomputed matcher fields, e.g.:

```json
{
  "normalizedTitles": ["software engineer", "backend developer"],
  "skillIds": ["uuid-skill1", "uuid-skill2"]
}

```

These are not exposed to FE.

---

## **2. Authentication Service DTOs**

### **2.1 Login & Registration**

#### **LoginRequestDTO (External)**

```json
{
  "email": "string",
  "password": "string"
}

```

#### **LoginResponseDTO (External)**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": ApplicantPublicDTO
}

```

#### **SSOLoginRequestDTO**

```json
{
  "provider": "google",
  "idToken": "string"
}

```

#### **Internal – AuthAccountInternalDTO**

```json
{
  "authId": "uuid",
  "applicantId": "uuid",
  "email": "string",
  "passwordHash": "string",
  "ssoProvider": "local",
  "ssoId": null,
  "isActivated": true,
  "failedAttempts": 0,
  "createdAt": "2025-11-20T12:00:00Z"
}

```

- **Never** leave Auth service boundary; no external exposure.

### **2.2 Token Introspection & Events**

- **TokenIntrospectionInternalDTO**
    - Fields: `tokenId`, `authId`, `applicantId`, `isRevoked`, `expiresAt`, `scopes` (optional).
    - Used between Auth and API Gateway/edge for validating JWE/JWS tokens.

---

## **3. Authorization Service DTOs**

### **3.1 Authorization Service Overview**

- **Role of Authorization Service**:
    - The **Authorization Service** is responsible for **managing user roles** and **permissions** across the entire system.
    - It ensures that users can only access the resources or perform actions for which they are authorized.
    - **Authorization** is separate from **Authentication**: while Authentication handles verifying user credentials, Authorization controls access to resources based on the user’s roles and permissions.

### **3.2 Internal DTOs (Authorization Service)**

#### **AuthAccountInternalDTO**

Used to store user **roles**, **permissions**, and other metadata related to user access control.

```json
{
  "authId": "uuid",
  "applicantId": "uuid",
  "email": "string",
  "passwordHash": "string",
  "role": "SystemAdmin",
  "permissions": ["view_jobs", "edit_profile"],
  "failedAttempts": 0,
  "isActivated": true,
  "createdAt": "2025-11-20T12:00:00Z"
}

```

- **Fields**:
    - `authId`: Unique identifier for the authentication account (UUID).
    - `applicantId`: Foreign key linking to the applicant entity (UUID).
    - `role`: The role assigned to the user (`SystemAdmin`, `User`, `HiringManager`).
    - `permissions`: A list of permissions granted to the user for access control (`view_jobs`, `edit_profile`).
    - `failedAttempts`: Number of failed login attempts (used for rate-limiting and security).
    - `isActivated`: Boolean indicating if the user account is active.
    - `createdAt`: Timestamp of account creation.
- **Exposed only internally**: This DTO is used to manage user roles and permissions within the **Authorization Service** and is **never exposed externally**.

#### **RoleDTO (Internal)**

This DTO defines the available **roles** and the permissions associated with them.

```json
{
  "roleId": "uuid",
  "roleName": "SystemAdmin",
  "permissions": ["view_jobs", "edit_profile", "manage_users"]
}

```

- **Fields**:
    - `roleId`: Unique identifier for the role (UUID).
    - `roleName`: The name of the role (`SystemAdmin`, `User`).
    - `permissions`: List of permissions associated with the role (`view_jobs`, `edit_profile`).
- **Exposed internally**: This DTO is used within the **Authorization Service** to assign permissions to users based on their roles.

#### **AuthorizationEventPayload (Internal)**

Used to pass **role-based access control events** between services (when a user’s role is updated).

```json
{
  "authId": "uuid",
  "role": "SystemAdmin",
  "permissions": ["view_jobs", "edit_profile"]
}

```

- **Fields**:
    - `authId`: The authentication account ID (UUID) for the user whose role or permissions are being updated.
    - `role`: The new role assigned to the user.
    - `permissions`: The list of permissions assigned to the role.
- **Used in event-driven communication**: This DTO is used to notify other services when a user’s role or permissions are updated (via Kafka events).

## **4. Application Service DTOs**

### **4.1 Application Submission (External)**

#### **ApplicationCreateRequestDTO**

```json
{
  "jobPostId": "jm-job-123",
  "companyId": "jm-company-42",
  "coverLetterText": "string | null",
  "cvFileId": "uuid | null",
  "coverLetterFileId": "uuid | null"
}

```

> FE may first call File Upload endpoint to obtain cvFileId and coverLetterFileId.
> 

#### **ApplicationPublicDTO (External)**

```json
{
  "applicationId": "uuid",
  "jobPostId": "jm-job-123",
  "companyId": "jm-company-42",
  "status": "Pending",
  "submissionDate": "2025-11-21T09:00:00Z",
  "updatedAt": "2025-11-21T09:00:00Z",
  "feedback": "string|null",
  "applicantCVUrl": "https://cdn/.../cv.pdf",
  "coverLetterUrl": "https://cdn/.../cover-letter.pdf",
  "fresherFlag": false}

```

- External DTO returns signed, temporary URLs rather than raw storage keys.

### **4.2 Internal – ApplicationInternalDTO**

```json
{
  "applicationId": "uuid",
  "applicantId": "uuid",
  "jobPostId": "jm-job-123",
  "companyId": "jm-company-42",
  "status": "Pending",
  "submissionDate": "2025-11-21T09:00:00Z",
  "fresherFlag": false,
  "searchProfileMatchIds": ["uuid-profile-1"]
}

```

Used in Kafka events (`ApplicationSubmittedEvent`) to Job Manager and Notification Service.

---

## **5. Subscription Service DTOs**

### **5.1 SubscriptionStatusDTO (External)**

```json
{
  "planType": "Premium",
  "isActive": true,
  "startDate": "2025-11-01T00:00:00Z",
  "expiryDate": "2025-12-01T00:00:00Z"
}

```

- Shown on applicant profile page and subscription management UI.

### **5.2 SubscriptionInternalDTO**

```json
{
  "subscriptionId": "uuid",
  "applicantId": "uuid",
  "planType": "Premium",
  "startDate": "2025-11-01T00:00:00Z",
  "expiryDate": "2025-12-01T00:00:00Z",
  "isActive": true,
  "lastPaymentTransactionId": "uuid"
}

```

Used between Subscription and Payment/Notification services.

---

## **6. Payment Service DTOs**

### **6.1 PaymentRequestDTO (External)**

```json
{
  "amount": 10.0,
  "currency": "USD",
  "paymentMethodId": "pm_123",
  "gateway": "Stripe"
}

```

- Card details handled client → gateway (Stripe/PayPal) directly; backend receives only tokenized `paymentMethodId`.

### **6.2 PaymentReceiptDTO (External)**

```json
{
  "transactionId": "uuid",
  "amount": 10.0,
  "currency": "USD",
  "gateway": "Stripe",
  "status": "Success",
  "timestamp": "2025-11-21T09:05:00Z"
}

```

### **6.3 PaymentTransactionInternalDTO**

Same as entity; includes `applicantId` and `email` for reconciliation & audit. Not all fields exposed externally.

---

## **7. Notification Service DTOs**

### **7.1 NotificationPublicDTO**

```json
{
  "notificationId": "uuid",
  "type": "ApplicationUpdate",
  "message": "Your application was viewed",
  "isRead": false,
  "timestamp": "2025-11-21T10:00:00Z"
}

```

### **7.2 NotificationInternalEventDTO**

```json
{
  "notificationId": "uuid",
  "recipientId": "uuid",
  "channel": "in-app",
  "type": "ApplicationUpdate",
  "templateKey": "application_viewed",
  "templateParams": {
    "jobTitle": "Backend Engineer",
    "companyName": "DevVision"
  },
  "timestamp": "2025-11-21T10:00:00Z"
}

```

Internal DTO has explicit `recipientId` and template info; FE never sees these fields directly.

---

## **8. Admin Service DTOs**

### **8.1 AdminLoginRequest/Response**

```json
{
  "email": "string",
  "password": "string"
}

```

```json
{
  "accessToken": "string",
  "role": "SystemAdmin"
}

```

### **8.2 AdminApplicantSummaryDTO (External – Admin UI only)**

```json
{
  "applicantId": "uuid",
  "fullName": "string",
  "email": "string",
  "country": "string",
  "isActivated": true,
  "isArchived": false,
  "createdAt": "2025-11-20T12:00:00Z"
}

```

Admin UI may see more than normal applicant UI (`isArchived`), but never `passwordHash`.

---

## **9. Sensitive Field Matrix**

| Service | Entity | Field | Rule for External DTOs |
| --- | --- | --- | --- |
| Auth | AuthAccount | passwordHash | Never exposed; internal only |
| Auth | AuthAccount | ssoId | Never exposed |
| Auth | AuthToken | isRevoked, tokenId | Internal only; gateway + Auth |
| Profile | Applicant | shardKey, isArchived | Internal only |
| Profile | ApplicantSkill | endorsedBy | Internal only |
| Profile | Resume | internal scoring fields | Internal only |
| Payment | PaymentTransaction | applicantId, email | Not in public receipt; only amount, currency, status, timestamp |
| Admin | SystemAdmin | passwordHash | Internal only |
| Authorization | AuthAccount | role, permissions, failedAttempts | Internal only; never exposed externally |
| Authorization | Role | roleId, permissions | Internal only; never exposed externally |

Error DTOs must also avoid leaking sensitive data (invalid email vs. "user not found").

---

## **10. Example End-to-End Payloads**

### **10.1 Applicant Profile (GET /api/me)**

- **Response:** `ApplicantPublicDTO` + `ResumePublicDTO` + `SearchProfilePublicDTO[]` bundled.

```json
{
  "applicant": { /* ApplicantPublicDTO */ },
  "resume": { /* ResumePublicDTO */ },
  "searchProfiles": [ /* SearchProfilePublicDTO */ ]
}

```

### **10.2 Application Submission (POST /api/applications)**

- **Request:** `ApplicationCreateRequestDTO`
- **Response:** `ApplicationPublicDTO`

### **10.3 Subscription Status (GET /api/subscription)**

- **Response:** `SubscriptionStatusDTO`

---

## **11. Definition of Done**

- `dto-mapping.md` committed under `/docs/data-model/` with service‑by‑service sections.
- For each entity in DM‑01, at least one **external** and (where needed) **internal** DTO is defined.
- Sensitive field matrix completed; verified that no external DTO includes `passwordHash`, raw tokens, card data, or shard internals.
- Example JSON payloads provided for: Applicant profile, Application submission, Subscription status (ready to paste into OpenAPI).
- Auth/Payment DTOs reviewed by security/privacy owner (Son/Phat) for compliance.
- DTO names and shapes referenced in API contract draft (CT‑02) and in event specs (Kafka topics).
- QA has sample payloads for positive/negative tests (missing fields, extra fields, invalid enums).
- Any future changes to entities require corresponding updates to this mapping (link in PR checklist).