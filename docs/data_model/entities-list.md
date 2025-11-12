# Entity Inventory – Job Applicant Subsystem (DM-01)
> Source: EEET2582_DevVision-JobApplicant-v1.1.pdf  
> Scope: Sections 1 – 6  
> Milestone 1 Deliverable – Data Model (Level Simplex → Ultimo)

Each entity represents a persistent data object required by the SRS.  
All attributes are preliminary for ER Model v1 (to be refined in DM-02).

---

## 1. Applicant
Stores the core identity, contact, and authentication data.  
📖 SRS §1 (Job Applicant Registration) & §2 (Login)

| Attribute | Type | Description | Notes |
|------------|------|-------------|-------|
| applicantId | string | Unique identifier | PK |
| fullName | string | Applicant’s full name | Required |
| email | string | Unique login credential | Unique, validated |
| passwordHash | string | Hashed + salted password | Backend validation §1.2 |
| country | string | ISO code, shard key | Required, dropdown §1.1.4 |
| city | string | City name | Optional |
| streetAddress | string | Street name / number | Optional |
| phoneNumber | string | E.164 format | Optional §1.2.3 |
| profileImage | string (URL) | Avatar path (auto-resized) | §3.2.1 |
| isActivated | boolean | Email verified | §1.1.3 |
| createdAt | datetime | Registration time |  |
| updatedAt | datetime | Last profile update |  |
| ssoProvider | enum(`local`,`google`,`microsoft`,`facebook`,`github`) | Auth source | §1.3.1 |
| ssoId | string | External user ID | §1.3.2 |
| shardKey | string | Country partition key | §1.3.3 |

---

## 2. AuthToken
Session and security token metadata.  
📖 SRS §2 (Login Authentication & Token Management)

| Attribute | Type | Description | Notes |
|------------|------|-------------|-------|
| tokenId | string | Unique token ID | PK |
| applicantId | FK → Applicant | Linked account |  |
| accessToken | string | Encrypted JWE token | §2.2.1 |
| refreshToken | string | Long-lived token | §2.3.3 |
| issuedAt | datetime | Issue time |  |
| expiresAt | datetime | Expiration |  |
| isRevoked | boolean | Revocation status | §2.2.3 → Redis cache §2.3.2 |
| failedAttempts | int | Brute-force counter | §2.2.2 |

---

## 3. Resume
Structured résumé data managed by the applicant.  
📖 SRS §3.1 (Profile Management)

| Attribute | Type | Description |
|------------|------|-------------|
| resumeId | string | PK |
| applicantId | FK → Applicant |  |
| headline | string | Short professional summary |
| objective | text | Objective statement |
| education | `array<object>` | {degree, institution, startYear, endYear, gpa?} §3.1.3 |
| experience | `array<object>` | {jobTitle, company, start, end, description} §3.1.4 |
| skills | `array<string>` | Tags §3.2.2 |
| certifications | `array<string>` | Optional |
| createdAt | datetime |  |
| updatedAt | datetime |  |

---

## 4. ApplicantSkill
Normalized link between Applicant ↔ SkillTag.  
📖 SRS §3.2.2 (Technical Skills)

| Attribute | Type | Description |
|------------|------|-------------|
| applicantId | FK → Applicant |  |
| skillId | FK → SkillTag |  |
| proficiency | enum(`Beginner`,`Intermediate`,`Advanced`) | Optional |

---

## 5. SkillTag
Catalog of all technical skills and competencies.  
📖 SRS §3.2.2 and §5.2.2

| Attribute | Type | Description |
|------------|------|-------------|
| skillId | string | PK |
| name | string | e.g. “React”, “Kafka” |
| category | string | Optional grouping |
| createdAt | datetime |  |

---

## 6. MediaPortfolio
Images / videos uploaded to showcase abilities.  
📖 SRS §3.2.3

| Attribute | Type | Description |
|------------|------|-------------|
| mediaId | string | PK |
| applicantId | FK → Applicant | Owner |
| fileUrl | string | Object storage path |
| mediaType | enum(`image`,`video`) | File type |
| title | string | Optional caption |
| description | text | Optional |
| uploadDate | datetime |  |
| visibility | enum(`public`,`private`) | Access control |

---

## 7. Application
Stores every job application submitted by the applicant.  
📖 SRS §4 (Job Search and Application)

| Attribute | Type | Description |
|------------|------|-------------|
| applicationId | string | PK |
| applicantId | FK → Applicant |  |
| jobPostId | External → JM JobPost |  |
| companyId | External → JM Company |  |
| status | enum(`Pending`,`Viewed`,`Accepted`,`Rejected`) |  |
| submissionDate | datetime |  |
| updatedAt | datetime |  |
| feedback | text | Optional |
| applicantCV | url | CV file upload §4.3.2 |
| coverLetter | url | Optional §4.3.2 |
| fresherFlag | boolean | Derived §4.2.2 |

---

## 8. SearchProfile
Saved search settings for premium users.  
📖 SRS §5.2.1 – §5.2.4

| Attribute | Type | Description |
|------------|------|-------------|
| searchProfileId | string | PK |
| applicantId | FK → Applicant |  |
| profileName | varchar(100) | Optional label |
| desiredCountry | varchar(100) |  |
| desiredMinSalary | decimal(10,2) | Default 0 §5.2.4 |
| desiredMaxSalary | decimal(10,2) | Nullable (no limit) §5.2.4 |
| jobTitles | text | Semicolon-separated §5.2.1 |
| technicalBackground | `array<string>` | Tags §5.2.2 |
| employmentStatus | `array<enum>` | `Full-time`, `Part-time`, `Internship`… §5.2.3 |
| createdAt | datetime |  |
| updatedAt | datetime |  |
| isActive | boolean |  |

---

## 9. Subscription
Tracks premium plan and validity.  
📖 SRS §5.1.1 – §5.1.2

| Attribute | Type | Description |
|------------|------|-------------|
| subscriptionId | string | PK |
| applicantId | FK → Applicant |  |
| planType | enum(`Free`,`Premium`) |  |
| startDate | datetime |  |
| expiryDate | datetime |  |
| isActive | boolean |  |

---

## 10. PaymentTransaction
Records third-party payment events.  
📖 SRS §5.1.2

| Attribute | Type | Description |
|------------|------|-------------|
| transactionId | string | PK |
| applicantId | FK → Applicant |  |
| email | string | Billing email |
| amount | decimal(10,2) | Always 10 USD |
| currency | string | “USD” |
| gateway | enum(`Stripe`,`PayPal`) |  |
| timestamp | datetime |  |
| status | enum(`Success`,`Failed`) |  |

---

## 11. Notification
All in-system and real-time Kafka messages.  
📖 SRS §5.3.1

| Attribute | Type | Description |
|------------|------|-------------|
| notificationId | string | PK |
| applicantId | FK → Applicant |  |
| type | enum(`ApplicationUpdate`,`Recommendation`,`System`) |  |
| message | text |  |
| isRead | boolean |  |
| timestamp | datetime |  |

---

## 12. SystemAdmin
Administrative user accounts for moderation.  
📖 SRS §6 (Admin Panel)

| Attribute | Type | Description |
|------------|------|-------------|
| adminId | string | PK |
| fullName | string |  |
| email | string |  |
| passwordHash | string |  |
| role | enum(`Moderator`,`SystemAdmin`) |  |
| lastLogin | datetime |  |

---
## Traceability Summary

| SRS Section | Key Entities |
|--------------|--------------|
| §1 Registration | Applicant |
| §2 Login Security | AuthToken |
| §3 Profile Management | Applicant, Resume, ApplicantSkill, SkillTag, MediaPortfolio |
| §4 Job Search & Application | Application |
| §5 Premium Subscription & Notifications | Subscription, PaymentTransaction, SearchProfile, Notification |
| §6 Admin Panel | SystemAdmin |

---
