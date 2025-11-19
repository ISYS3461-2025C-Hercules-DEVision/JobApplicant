# 📌 DM-01 — Entities List (Aligned with Microservice Architecture & DEVision Ultimo Requirements)
> Source: EEET2582_DevVision-JobApplicant-v1.1.pdf  
> Scope: Sections 1 – 6  
> Milestone 1 Deliverable – Data Model (Level Simplex → Ultimo)

Each entity represents a persistent data object required by the SRS.  
All attributes are preliminary for ER Model v1 (to be refined in DM-02).

This document lists all entities required by the Job Applicant subsystem.  
Each entity includes:
- Description  
- OwnedByService (microservice boundary)  
- DBType (Mongo/Postgres/Redis etc.)  
- Sharded? (Yes/No)  
- Source-of-Truth Information  
- Notes on cross-service references  

---

# 🟦 1. Applicant (Core Profile)
**OwnedByService:** Profile Service  
**DBType:** MongoDB or Postgres JSONB  
**Sharded:** YES — shardKey = `country`  
**Source of Truth:** Yes  
**Description:**  
Stores the core identity and contact information of the job applicant.

---

# 🟦 2. Education
**OwnedByService:** Profile Service  
**DBType:** Same DB + same shard as Applicant  
**Sharded:** YES  
**Source of Truth:** Yes  
**Description:**  
List of academic degrees and institutions declared by the applicant.  

---

# 🟦 3. WorkExperience
**OwnedByService:** Profile Service  
**DBType:** Same DB + same shard as Applicant  
**Sharded:** YES  
**Source of Truth:** Yes  
**Description:**  
Chronological employment history including title, duration, and descriptions.

---

# 🟦 4. SkillTag
**OwnedByService:** Profile Service  
**DBType:** Global catalog (not sharded)  
**Sharded:** NO  
**Source of Truth:** Yes  
**Description:**  
Master list of skills/competencies (e.g., “React”, “Kafka”). Reused across applicants and job posts.

---

# 🟦 5. ApplicantSkill (Relation: Applicant ↔ SkillTag)
**OwnedByService:** Profile Service  
**DBType:** Same shard as Applicant  
**Sharded:** YES  
**Source of Truth:** **Canonical source** for technical skill declarations  
**Description:**  
Many-to-many mapping of Applicant to SkillTag with proficiency level.

---

# 🟦 6. MediaPortfolio
**OwnedByService:** Profile Service  
**DBType:** Metadata stored in same shard as Applicant; files stored in Object Storage (S3/minio)  
**Sharded:** YES  
**Source of Truth:** Yes  
**Description:**  
Uploaded images/videos used to showcase portfolio.

---

# 🟦 7. Resume
**OwnedByService:** Profile Service  
**DBType:** Same shard as Applicant  
**Sharded:** YES  
**Source of Truth:** Yes, but see notes  
**Notes:** Resume.skills is **denormalized**, ApplicantSkill remains the authoritative mapping.  
**Description:**  
Applicant’s summarized resume data, stored as JSON arrays.

---

# 🟧 8. AuthToken
**OwnedByService:** Auth Service  
**DBType:** Redis (revocation) + Postgres/Mongo (token metadata)  
**Sharded:** NO  
**Source of Truth:** Auth Service  
**Description:**  
Stores token metadata (issuedAt, expiry, failedAttempts, isRevoked).  
**Notes:**  
- `applicantId` is **reference ONLY**, NOT a foreign key.  
- Sensitive fields never exposed in external DTO.  

---

# 🟩 9. Application
**OwnedByService:** Application Service  
**DBType:** MongoDB/Postgres  
**Sharded:** NO (global DB)  
**Source of Truth:** Application Service  
**Description:**  
Represents an application submitted to a Job Manager JobPost.  
**Notes:**  
- `jobPostId`, `companyId` are **external IDs** coming from Job Manager subsystem.  
- NO FK constraints.  

---

# 🟩 10. CVFileReference / CoverLetterReference
**OwnedByService:** Application Service  
**DBType:** MongoDB/Postgres  
**Sharded:** NO  
**Source of Truth:** Application Service  
**Description:**  
Metadata for CV/Cover Letter file uploads. Actual files stored in Object Storage.

---

# 🟨 11. SearchProfile
**OwnedByService:** Profile Service  
**DBType:** Same shard as Applicant  
**Sharded:** YES  
**Source of Truth:** Yes  
**Description:**  
Applicant-defined search preferences for Premium subscription.

---

# 🟨 12. Subscription
**OwnedByService:** Subscription Service  
**DBType:** Postgres (global)  
**Sharded:** NO  
**Source of Truth:** Subscription Service  
**Notes:**  
Links to applicantId as a string reference only.  
**Description:**  
Premium membership details: planType, dates, active flag.

---

# 🟨 13. PaymentTransaction
**OwnedByService:** Payment Service  
**DBType:** Postgres (global)  
**Sharded:** NO  
**Source of Truth:** Payment Service  
**Notes:**  
- No sensitive card info stored.  
- applicantId is a reference only.  
**Description:**  
Records payment success/failure events from Stripe/PayPal.

---

# 🟪 14. Notification
**OwnedByService:** Notification Service  
**DBType:** MongoDB/Postgres  
**Sharded:** NO  
**Source of Truth:** Notification Service  
**Description:**  
Stores email or in-app notifications sent due to Kafka triggers.

---

# 🟫 15. SystemAdmin
**OwnedByService:** Admin Service  
**DBType:** Postgres  
**Sharded:** NO  
**Source of Truth:** Admin Service  
**Description:**  
Internal admin accounts managing applicants/companies/job posts.

---

# 🧩 SUMMARY TABLE (Copy for report appendix)

| Entity | OwnedByService | DB Type | Sharded? | Notes |
|--------|----------------|---------|----------|--------|
| Applicant | Profile | Mongo/Postgres | YES | ShardKey = country |
| Education | Profile | Same shard | YES | - |
| WorkExperience | Profile | Same shard | YES | - |
| SkillTag | Profile | Global | NO | Skill catalog |
| ApplicantSkill | Profile | Same shard | YES | Canonical |
| MediaPortfolio | Profile | Same shard | YES | Files in S3 |
| Resume | Profile | Same shard | YES | Denormalized skills |
| AuthToken | Auth | Redis + DB | NO | applicantId reference |
| Application | Application | Global DB | NO | References external JobPost |
| CV/CoverLetter | Application | Global DB | NO | File metadata |
| SearchProfile | Profile | Same shard | YES | Premium search prefs |
| Subscription | Subscription | Postgres | NO | applicantId reference |
| PaymentTransaction | Payment | Postgres | NO | No card data |
| Notification | Notification | DB | NO | Consumed from Kafka |
| SystemAdmin | Admin | Postgres | NO | Internal admin users |
