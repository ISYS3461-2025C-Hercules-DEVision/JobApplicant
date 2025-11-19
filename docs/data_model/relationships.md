# DM-03 — Relationships & Cardinalities (Microservices, Ultimo-Level)

This document defines the conceptual entity relationships of the Job Applicant subsystem under an **Ultimo-level microservices + sharding + event-driven architecture**.  
All relationships describe **business cardinality**, not database foreign keys (because each microservice owns its own database).

---

# 1. Profile Service Relationships (Sharded by `country`)

### 1.1 Applicant → Education  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant has 0..N Education records  
- **Storage:** Same shard as Applicant  
- **Cascade:** Delete Applicant → Delete all Education in same shard  

### 1.2 Applicant → WorkExperience  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant has 0..N WorkExperience entries  
- **Storage:** Same shard  
- **Cascade:** Delete Applicant → Delete WorkExperience  

### 1.3 Applicant → ApplicantSkill → SkillTag  
- **Type:** Many-to-many  
- **Cardinality:**  
  - One Applicant has N skills  
  - One SkillTag may be used by M Applicants  
- **Storage:**  
  - ApplicantSkill: same shard  
  - SkillTag: global catalog  
- **Cascade:**  
  - Delete Applicant → Delete ApplicantSkill  
  - Delete SkillTag → Invalidates related ApplicantSkill entries  

### 1.4 Applicant → MediaPortfolio  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant has 0..N media items  
- **Storage:** Metadata in same shard; files in Object Storage  
- **Cascade:** Delete Applicant → Delete metadata + delete files  

### 1.5 Applicant → Resume  
- **Type:** 1-to-1  
- **Cardinality:** One Applicant has exactly one Resume  
- **Storage:** Same shard  
- **Cascade:** Delete Applicant → Delete Resume  

### 1.6 Applicant → SearchProfile  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant has 0..N SearchProfiles  
- **Storage:** Same shard  
- **Cascade:** Delete Applicant → Delete all SearchProfiles  

---

# 2. Application Service Relationships (Global DB)

### 2.1 Applicant → Application  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant has 0..N Applications  
- **Storage:** Application DB (different microservice)  
- **Reference:** `applicantId` stored as string only (no FK)  
- **Cascade:**  
  - On Applicant deletion → Applications are **not deleted**  
  - They are **anonymized** (PII removed, applicantId replaced with a placeholder)  

### 2.2 Application → CVFileReference / CoverLetterReference  
- **Type:** 1-to-1  
- **Cardinality:**  
  - One Application has 0..1 CV  
  - One Application has 0..1 Cover Letter  
- **Storage:** Application DB  
- **Cascade:** Delete Application → Delete file metadata (Object Storage cleanup)  

### 2.3 Application → JobPost (external)  
- **Type:** many-to-one (conceptual)  
- **Reference:** `jobPostId` stored as string (Job Manager owns JobPost)  
- **Cascade:** If JM deletes JobPost → Application is marked archived  

---

# 3. Subscription & Payment Relationships (Global DB)

### 3.1 Applicant → Subscription  
- **Type:** 1-to-1 (current active subscription)  
- **Storage:** Subscription DB  
- **Reference:** `applicantId` stored as string  
- **Cascade:** On Applicant deletion → SubscriptionService auto-cancels subscription  

### 3.2 Applicant → PaymentTransaction  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant may have many transactions  
- **Cascade:** On Applicant deletion → Do **not** delete (audit). Mask PII.  

---

# 4. Notification Service Relationships

### 4.1 Applicant → Notification  
- **Type:** 1-to-many  
- **Cardinality:** One Applicant has 0..N notifications  
- **Cascade:** On Applicant deletion → Mark notifications archived  

---

# 5. Sharding Relationships

### 5.1 Same-Shard Requirement  
The following MUST exist in the same shard as the Applicant:

- Education  
- WorkExperience  
- Resume  
- ApplicantSkill  
- MediaPortfolio  
- SearchProfile  

### 5.2 Shard Migration  
When `country` changes:  
- Applicant and all same-shard entities migrate together  
- Application, Payment, Subscription remain in their own databases  
- Migration emits:  
  - `ApplicantShardMoved`  
  - `ApplicantShardMoveCompleted`

---

# 6. Event Relationships (Kafka)

## 6.1 Applicant Events
### ApplicantCreated  
- Producer: Profile Service  
- Consumers: Notification, (JM optional)

### ProfileUpdated  
- Producer: Profile Service  
- Consumers: Notification, Job Manager (for headhunt matching)

### ApplicantDeleted  
- Producer: Profile Service  
- Consumers: Application, Payment, Subscription, Notification  
- Effect: PII anonymization in external services

### ApplicantShardMoved  
- Producer: Profile Service  
- Consumers: Any service with routing/state caches  

---

## 6.2 Application Events
### ApplicationSubmitted  
- Producer: Application Service  
- Consumers: Job Manager, Notification Service  

---

## 6.3 Payment & Subscription Events
### PaymentSucceeded  
- Producer: Payment Service  
- Consumers: Subscription, Notification  

### SubscriptionActivated  
- Producer: Subscription Service  
- Consumers: Notification  

---

# 7. Summary Table — Complete Relationship Matrix (Ultimo-Level)

| Relationship | Cardinality | Storage Location | Enforcement Mode | Cascade / Event Behavior |
|-------------|-------------|------------------|------------------|---------------------------|
| Applicant → Education | 1:N | Profile shard | Strong (same DB) | Delete Education on Applicant delete |
| Applicant → WorkExperience | 1:N | Profile shard | Strong | Delete WorkExperience |
| Applicant → Resume | 1:1 | Profile shard | Strong | Delete Resume |
| Applicant → MediaPortfolio | 1:N | Profile shard + S3 | Strong | Delete metadata + object files |
| Applicant → ApplicantSkill | 1:N | Profile shard | Strong | Delete ApplicantSkill |
| ApplicantSkill → SkillTag | N:1 | Profile global catalog | Loose (ID reference) | If SkillTag deleted → cleanup ApplicantSkill |
| Applicant → SkillTag (via ApplicantSkill) | M:N | Profile shard + catalog | Strong (join table) | As above |
| Resume → Education/WorkExperience | 1:N (embedded/refs) | Profile shard | Strong | Auto-clean via Applicant cascades |
| Applicant → SearchProfile | 1:N | Profile shard | Strong | Delete SearchProfiles |
| Applicant → Application | 1:N | Application DB | Loose (ID reference) | **Anonymize**, not delete |
| Application → CVFileReference | 1:1 | Application DB | Strong | Delete metadata + file |
| Application → CoverLetterReference | 1:1 | Application DB | Strong | Delete metadata + file |
| Application → JobPost (JM) | N:1 | JM DB | External reference | JM delete → Application archived |
| Application → Company (JM) | N:1 | JM DB | External reference | No cascade |
| Applicant → Subscription | 1:1 (active) | Subscription DB | Loose | Cancel subscription |
| Applicant → PaymentTransaction | 1:N | Payment DB | Loose | Retain for audit, mask PII |
| Subscription → PaymentTransaction | M:N (conceptual) | Payment DB | Loose | No cascade |
| Applicant → Notification | 1:N | Notification DB | Loose | Archive on delete |
| Notification → Application (contextual) | Optional | Notification DB | Loose | No cascade |
| Admin → Applicant | 1:N | Admin DB | Command relationship | Admin may deactivate |
| Admin → Company | 1:N | Admin DB | Command relationship | Admin may deactivate |
| Admin → JobPost | 1:N | Admin DB | Command relationship | Admin may delete JobPost |
| Applicant (country) → Shard | 1:1 | Profile DB | Routing rule | Shard migration on country change |
| Profile Service → JM (ProfileUpdated event) | Event-based | Kafka | Loose | Trigger headhunt matching |
| Application Service → JM (ApplicationSubmitted) | Event-based | Kafka | Loose | Update company dashboards |
| Payment Service → Subscription (PaymentSucceeded) | Event-based | Kafka | Loose | Activate subscription |
| Profile → All Services (ApplicantShardMoved) | Event-based | Kafka | Loose | Update routing caches |