# DM-02 — Entities, Data Types & Validation (Ultimo / Microservices)
> Source: EEET2582_DevVision-JobApplicant-v1.1.pdf  
> Scope: Sections 1 – 6  
> Milestone 1 Deliverable – Data Model (Level Simplex → Ultimo)

All attributes are preliminary for ER Model v1

> Purpose: Definitive field types, constraints, indexing and DTO visibility for each entity under the **Ultimo (microservice)** architecture.  
> This file assumes each microservice owns its own database (no cross-service FK constraints). See DM-01 for ownership mapping.

---

## Conventions & Notes
- **ID format:** `uuid` (string, UUIDv4) across all services. Example: `"applicantId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"`.  
- **Timestamps:** ISO8601 strings (UTC). Field names: `createdAt`, `updatedAt`, `deletedAt`.  
- **Field visibility:** `internalOnly` fields MUST NOT be returned by external DTOs/endpoints. Use the `external` DTO mapping stage to sanitize.  
- **No cross-DB FK constraints:** Where one service references another's entity, store only the ID as a reference string and validate via API calls or event consumer logic.  
- **Sharding rule:** Profile DB is sharded by `country`. ShardKey = `country`. See Shard Migration section below.
- **FTS:** Use DB engine FTS features (Postgres `GIN` + `tsvector` or MongoDB text index) for fields flagged `FTS`.  
- **Indexes:** Each entity section includes recommended indexes for performance.

---

# 1. Profile Service Entities (Sharded DB: shardKey = country)
Owned: Applicant, Education, WorkExperience, SkillTag (catalog), ApplicantSkill, MediaPortfolio, Resume, SearchProfile.

### 1.1 Applicant
- `applicantId` : string (uuid) — PK  
- `fullName` : string (max 200) — required  
- `email` : string — required, unique **within global index** (store unique constraint in Auth Service as well)  
- `country` : string (ISO-2 / ISO-3 or canonical) — required — **shardKey**  
- `city` : string (optional)  
- `phoneNumber` : string (optional) — validated E.164 format (regex)  
- `streetAddress` : string (optional)  
- `profileImageUrl` : string (URL) — optional  
- `isActivated` : boolean — default false  
- `createdAt`, `updatedAt`, `deletedAt` : timestamps  
- `isArchived` : boolean — default false (for soft-delete)  
**internalOnly:** none (profile fields are shareable)  
**Indexes:** `{country, email}`, `email`(unique across shards via central index or Auth check), `createdAt`  
**Notes:** Store applicant in the shard corresponding to `country`. Use UUIDs for `applicantId`.

---

### 1.2 Education
- `educationId` : uuid  
- `applicantId` : uuid (reference) — **no DB FK**, must be co-located (same shard)  
- `degree` : string  
- `institution` : string  
- `fromYear` : integer (YYYY)  
- `toYear` : integer or `null`  
- `gpa` : number (0-100) optional  
- `createdAt`, `updatedAt`  
**Indexes:** `applicantId`

---

### 1.3 WorkExperience
- `workExpId` : uuid  
- `applicantId` : uuid (reference, same shard)  
- `jobTitle` : string (FTS candidate)  
- `companyName` : string  
- `from` : date (mm-yyyy or ISO)  
- `to` : date or null  
- `description` : text (FTS)  
- `createdAt`, `updatedAt`  
**FTS:** `jobTitle`, `description`  
**Indexes:** `applicantId`

---

### 1.4 SkillTag (catalog)
- `skillId` : uuid  
- `name` : string (unique, normalized lowercase)  
- `category` : string (optional)  
- `createdAt`  
**Sharding:** NOT sharded — global small catalog (replicated).  
**Indexes:** `name` (unique)

---

### 1.5 ApplicantSkill (join)
- `id` : uuid  
- `applicantId` : uuid (same shard)  
- `skillId` : uuid (ref to SkillTag)  
- `proficiency` : enum(`Beginner`, `Intermediate`, `Advanced`)  
- `endorsedBy` : optional array of uuid (if endorsements implemented)  
- `createdAt`, `updatedAt`  
**Source of truth:** ApplicantSkill is canonical for skill relationships. Resume.skills may be a cached/denormalized copy.  
**Indexes:** `{applicantId}`, `{skillId}`

---

### 1.6 MediaPortfolio (metadata)
- `mediaId` : uuid  
- `applicantId` : uuid (same shard)  
- `fileUrl` : string (S3 or CDN)  
- `mediaType` : enum(`image`,`video`)  
- `title` : string  
- `description` : text  
- `visibility` : enum(`public`,`private`)  
- `createdAt`  
**Files:** store actual binary in S3/minio; metadata in DB.  
**Indexes:** `applicantId`

---

### 1.7 Resume
- `resumeId` : uuid  
- `applicantId` : uuid (same shard)  
- `headline` : string  
- `objective` : text (FTS)  
- `education` : array of educationId refs or embedded (choose one)  
- `experience` : array of workExpId refs or embedded  
- `skills` : array[string] — **denormalized** ONLY; canonical = ApplicantSkill  
- `certifications` : array[string]  
- `createdAt`, `updatedAt`  
**FTS:** `objective`  
**Notes:** Keep `skills` in sync via events: `ApplicantSkillsUpdated`.

---

### 1.8 SearchProfile (saved search preferences)
- `searchProfileId` : uuid  
- `applicantId` : uuid (same shard)  
- `profileName` : string  
- `desiredCountry` : string (filter)  
- `desiredMinSalary` : decimal(10,2)  
- `desiredMaxSalary` : decimal(10,2)  
- `jobTitles` : array[string]  
- `technicalBackground` : array[skillId or skill name]  
- `employmentStatus` : array[enum]  
- `isActive` : boolean  
- `createdAt`, `updatedAt`  
**Indexes:** `applicantId`, `desiredCountry`

---

# 2. Auth Service Entities (Global DB + Redis)
Owned: AuthToken / AuthAccount

### 2.1 AuthAccount
- `authId` : uuid  
- `applicantId` : uuid (reference to Profile service) — **no FK**  
- `email` : string — required, unique (Auth service must coordinate uniqueness with Profile during signup)  
- `passwordHash` : string — **internalOnly**  
- `ssoProvider` : enum(`local`,`google`,`github`,...)  
- `ssoId` : string (when SSO used)  
- `isActivated` : boolean  
- `createdAt`, `updatedAt`  
**Security:** Use Argon2/Bcrypt hashed passwords; never store plaintext.  
**Indexes:** `email` (unique)

---

### 2.2 AuthToken (metadata)
- `tokenId` : uuid  
- `authId` : uuid (reference)  
- `issuedAt` : timestamp  
- `expiresAt` : timestamp  
- `tokenType` : enum(`access`,`refresh`)  
- `isRevoked` : boolean (soft)  
- `failedAttempts` : integer (for lockout policy)  
- `createdAt`, `updatedAt`  
**Storage pattern:**  
- Store `tokenId` + metadata in Auth DB (Postgres/Mongo) and store active/blocked token IDs in Redis (fast denylist).  
- Access tokens should be short-lived JWE/JWT; use `tokenId` pattern for server revoke (JWT contains tokenId in payload).  
**Revocation:** Use Redis set `revoked:tokenIds` or a bloom filter TTL-based approach for performance.

---

# 3. Application Service Entities (Global DB)
Owned: Application, CV/CoverLetter metadata

### 3.1 Application
- `applicationId` : uuid  
- `applicantId` : uuid (reference) — stored as string, no FK constraint  
- `jobPostId` : string (external ID from Job Manager) — external reference only  
- `companyId` : string (external)  
- `status` : enum(`Pending`,`Viewed`,`Accepted`,`Rejected`)  
- `submissionDate` : timestamp  
- `updatedAt` : timestamp  
- `feedback` : text (internal)  
- `applicantCV` : CVFileReference id (optional)  
- `coverLetter` : CoverLetterReference id (optional)  
- `createdAt`, `isArchived` (soft-delete flag)  
**Indexes:** `{jobPostId}`, `{applicantId}`, `{status}`  
**Notes:** Do not cascade delete on applicant removal — Application service receives `ApplicantDeleted` event and will anonymize or keep audit per retention policy.

---

### 3.2 CVFileReference / CoverLetterReference
- `fileId` : uuid  
- `applicationId` : uuid  
- `fileUrl` : string (S3)  
- `fileType` : string (pdf/docx)  
- `createdAt`  
**PII:** ensure file access requires auth check.

---

# 4. Subscription & Payment Entities (Global DB)
Owned: Subscription Service, Payment Service (may be separate microservices)

### 4.1 Subscription
- `subscriptionId` : uuid  
- `applicantId` : uuid (reference)  
- `planType` : enum(`Free`,`Premium`)  
- `startDate` : timestamp  
- `expiryDate` : timestamp  
- `isActive` : boolean  
- `createdAt`, `updatedAt`  
**Indexes:** `applicantId`, `isActive`

---

### 4.2 PaymentTransaction
- `transactionId` : uuid  
- `applicantId` : uuid (reference)  
- `email` : string (payer email)  
- `amount` : decimal(10,2)  
- `currency` : string (ISO)  
- `gateway` : enum(`Stripe`,`PayPal`)  
- `status` : enum(`Success`,`Failed`)  
- `timestamp` : timestamp  
**Security:** **Never store raw card numbers / CVV.** Store payment gateway reference tokens only.

---

# 5. Notification Service Entities (Global DB)
Owned: Notification Service

### 5.1 Notification
- `notificationId` : uuid  
- `recipientId` : uuid (applicantId or companyId)  
- `type` : enum(`ApplicationUpdate`,`JobMatch`,`System`)  
- `message` : text  
- `isRead` : boolean  
- `timestamp` : timestamp  
**Retention:** keep history for X days — configurable.  
**Indexes:** `recipientId`, `isRead`

---

# 6. Admin Service Entities (Global DB)
### 6.1 SystemAdmin
- `adminId` : uuid  
- `fullName` : string  
- `email` : string  
- `passwordHash` : string — **internalOnly**  
- `role` : enum(`Moderator`,`SystemAdmin`)  
- `lastLogin` : timestamp

---

## Shard Migration Procedure (when applicant changes `country`)
(Required by SRS Ultimo: user's country change triggers data migration) 

**Goal:** Move the applicant record and collocated data (Resume, ApplicantSkill, MediaPortfolio, Education, WorkExperience, SearchProfile) atomically or in safe steps between shards.

**Procedure (high-level):**
1. **Lock write** for that applicant at Profile service (set `migrationInProgress` flag).  
2. **Create a copy** of the applicant record and its dependent docs in the target shard (write-only state).  
3. **Publish** Kafka event `ApplicantShardMoved` with payload `{ applicantId, fromShard, toShard, timestamp }`. (Consumers may update caches/routing.)  
4. **Switch reads/writes** to the target shard for this applicant (update routing registry).  
5. **Perform consistency checks** (count documents, checksums).  
6. **Soft-delete or mark old docs as migrated** in source shard (do not hard delete immediately).  
7. **Publish** `ApplicantShardMoveCompleted` event.  
8. **Background cleanup** after TTL (archive old docs after 7 days or policy window).  
**Notes:** Plan for partial failure: if copy fails, rollback target writes and release lock; use idempotent writes and correlation IDs for events.

---

## Event list (key domain events) — to include in relationships.md / Kafka topics
**(Short list; expand in CT-04 as required)**

- `ApplicantCreated` — Producer: Profile Service — Consumers: Auth (maybe), Notification — payload `{applicantId, email, country, createdAt}`.
- `ProfileUpdated` — Producer: Profile Service — Consumers: Notification, Search Indexer, Job Manager (if subscribed) — payload `{applicantId, changedFields, timestamp}`.
- `ApplicantDeleted` — Producer: Profile Service — Consumers: Application, Payment, Notification — payload `{applicantId, deletedAt}`.  
- `ApplicantShardMoved` — Producer: Profile Service — Consumers: All services holding caches/routing — payload `{applicantId, fromShard, toShard, timestamp}`.  
- `ApplicationSubmitted` — Producer: Application Service — Consumers: Notification Service, Job Manager (JM) — payload `{applicationId, applicantId, jobPostId, submissionDate}`.  
- `PaymentSucceeded` / `PaymentFailed` — Producer: Payment Service — Consumers: Subscription Service, Notification — payload `{transactionId, subscriptionId, status}`.

---

## DTO visibility & mapping (internal vs external)
Each entity lists `internalOnly` fields:
- `AuthAccount.passwordHash` — `internalOnly`  
- `AuthToken.*` (token raw) — `internalOnly`  
- `PaymentTransaction` — do NOT include gateway tokens or sensitive fields in external DTOs  
All other profile fields can be in external DTOs, but apply PII rules (GDPR): when `isArchived` or `deletedAt` present, external DTO should mask or remove PII.

---

## Indexing & Search recommendations
- **Profile DB (shard):** compound index on `{country, applicantId}`, FTS index on `Resume.objective`, `WorkExperience.description`.  
- **ApplicantSkill:** index on `skillId` for reverse lookups.  
- **Application:** index on `{jobPostId, status}` for company-side queries.  
- Use materialized read-models or search index (Elastic / Postgres FTS / Mongo Atlas Search) for global search across shards (route queries to relevant shards if `country` present, otherwise multi-shard queries).

---

## Example minimal JSON payloads (for reference)

**ApplicantCreated event**
```json
{
  "event": "ApplicantCreated",
  "payload": {
    "applicantId": "uuid-v4",
    "email": "jane.doe@example.com",
    "country": "VN",
    "createdAt": "2025-11-10T10:00:00Z"
  }
}
```

**ApplicationSubmitted event**
```json
{
  "event": "ApplicationSubmitted",
  "payload": {
    "applicationId": "uuid-v4",
    "applicantId": "uuid-v4",
    "jobPostId": "jm-1234",
    "submissionDate": "2025-11-12T14:25:00Z"
  }
}
```

---

## Security & privacy reminders (must be in report)
- Passwords hashed (Argon2/Bcrypt).  
- Payment card data handled only by payment provider (Stripe/PayPal). Store reference tokens only.
- Token revocation: use Redis denylist for quick checks and store token metadata in Auth DB.  
- For deletions: prefer soft-delete + anonymization; only hard-delete by policy or admin request with confirmation.