# Attributes, Types & Validation Rules (DM-02)
> Source: EEET2582_DevVision-JobApplicant-v1.1.pdf  
> Scope: Sections 1 – 6  
> Milestone 1 Deliverable – Data Model (Level Simplex → Ultimo)


All attributes are preliminary for ER Model v1

## Conventions

- **Type (FE)**: UI form type (text, email, select, file, etc.).
- **Type (BE)**: Database/storage type (PostgreSQL unless noted).
- **Constraint**: `PK`, `FK`, `UNIQUE`, `NOT NULL`, `NULL`, `CHECK`, `INDEX`, `DEFAULT`.
- **Regex** samples are language‑agnostic (PCRE/ECMAScript compatible).
- **Shared FE/BE validators** are defined once and reused across forms and DTOs.

### Shared Validation Library (reuse everywhere)

- **Email**
    - Rules: exactly one `@`; at least one `.` after `@`; total length < 255; no spaces; forbid `()[];:`.
    - Regex (syntax check): `^(?!.*[()\[\];:])[^\s@]+@[^\s@]+\.[^\s@]+$`
    - BE: `CHECK (length(email) < 255)` + `LOWER(email)` unique index.
- **Password strength**
    - Rules: ≥ 8 chars; ≥ 1 digit; ≥ 1 special; ≥ 1 uppercase.
    - Regex: `^(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$`
- **Phone (optional)**
    - Rules: starts with `+` and valid dial code; digits only after `+`; digits *after* dial code ≤ 12.
    - Regex (format): `^\+[1-9]\d{0,2}\d{1,12}$`
    - BE: custom `CHECK` to enforce local‑part length ≤ 12 after dial code.
- **Country**
    - FE: dropdown sourced from ISO‑3166‑1 alpha‑2 list.
    - BE: `CHAR(2)` with `CHECK (country ~ '^[A-Z]{2}$')`.
- **Image file**
    - FE: accept `image/*`, max 5MB.
    - BE: content‑type sniff + size cap; auto‑resize (e.g., 512×512) on upload; store URL only.
- **Timestamps**: `TIMESTAMPTZ`; default `NOW()` on create; update triggers for `updatedAt`.

---

## 1. Applicant

| Attribute | Type (FE) | Type (BE) | Constraint | Frontend validation rule | Backend validation rule | Example values / edge cases |
| --- | --- | --- | --- | --- | --- | --- |
| applicantId | – | UUID | PK | – | `PRIMARY KEY` | `3e0f…` (UUID v4) |
| fullName | text | VARCHAR(150) | NOT NULL | 1–150 chars; trim; collapse doublespaces | `CHECK (length(fullName) BETWEEN 1 AND 150)` | `Nguyen Thuy Dung` |
| email | email | CITEXT | UNIQUE, NOT NULL | email regex; length<255 | unique index on `LOWER(email)`; email `CHECK` | `dung.nguyen@example.com` |
| passwordHash | – | VARCHAR(255) | NULL (SSO) / NOT NULL (local) | – | generate via Argon2id/BCrypt; `NULL` if `ssoProvider!='local'` | `$argon2id$v=19$…` |
| country | select | CHAR(2) | NOT NULL, INDEX, shard key | must pick from list | `CHECK (country ~ '^[A-Z]{2}$')` | `VN` |
| city | text | VARCHAR(120) | NULL | ≤120 chars | `CHECK (length(city) <= 120)` | `Ho Chi Minh City` |
| streetAddress | text | VARCHAR(180) | NULL | ≤180 chars | `CHECK (length(streetAddress) <= 180)` | `12 Nguyen Hue, Dist. 1` |
| phoneNumber | tel | VARCHAR(20) | NULL | phone regex | phone regex + custom `CHECK` | `+84xxxxxxxxx` |
| profileImage | file (image) | TEXT (URL) | NULL | accept image/*; ≤5MB | store URL; resize to 512×512 | `https://cdn/.../avatar.png` |
| isActivated | – | BOOLEAN | DEFAULT false | – | defaults false; set true after email activation | `false` → `true` |
| createdAt | – | TIMESTAMPTZ | DEFAULT NOW() | – | – | `2025‑11‑12T05:30:00Z` |
| updatedAt | – | TIMESTAMPTZ | – | – | trigger to auto‑update | – |
| ssoProvider | select | ENUM('local','google','microsoft','facebook','github') | DEFAULT 'local' | single choice (project selects **one** for SSO enablement) | enforce only configured provider allowed | `google` |
| ssoId | – | VARCHAR(128) | UNIQUE NULLABLE | – | unique when not null | `google-oauth2 |
| shardKey | – | CHAR(2) | NOT NULL | – | equals `country`; used for routing and migrations | `VN` |

**Notes**

- Email activation: send activation email on registration; `isActivated=false` until verified; login blocked otherwise.
- Country change triggers **data migration** to new shard; wrap in transaction and publish Kafka event (§3.3.2).

---

## 2. AuthToken

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| tokenId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK → Applicant | – | FK; `ON DELETE CASCADE` | – |
| accessToken | – | TEXT | NOT NULL | – | JWE (encrypted) for non‑SSO; store hash or opaque id; set TTL | `eyJhbGciOiJ…` |
| refreshToken | – | TEXT | NULLABLE | – | rotate; store hash; TTL (e.g., 30d) | – |
| issuedAt | – | TIMESTAMPTZ | NOT NULL | – | – | – |
| expiresAt | – | TIMESTAMPTZ | NOT NULL | – | – | – |
| isRevoked | – | BOOLEAN | DEFAULT false | – | mirror Redis revocation cache | – |
| failedAttempts | – | SMALLINT | DEFAULT 0 | – | throttle: block after ≥5 fails within 60s | – |

**Notes**

- Brute force: maintain rolling window; lock account and/or IP; expose user‑safe error message.
- Logout: revoke access token in Redis; add to deny‑list until expiry.

---

## 3. Resume

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| resumeId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | FK | – |
| headline | text | VARCHAR(120) | NULL | ≤120 chars | length check | `Backend Developer` |
| objective | textarea | TEXT | NULL | ≤2,000 chars | length check | Short career goal |
| education | dynamic list | JSONB | NULL | degree, institution, startYear, endYear, gpa? | schema validate; gpa 0–100 | `{degree:"BSc",…}` |
| experience | dynamic list | JSONB | NULL | title, company, start, end, desc | schema validate; dates mm‑yyyy | `{jobTitle:"SE",…}` |
| skills | tag input | JSONB | NULL | tag set; dedupe case‑insensitive | normalise to SkillTag | `["Kafka","React"]` |
| certifications | tag input | JSONB | NULL | ≤50 items | – | – |
| createdAt | – | TIMESTAMPTZ | – | – | – | – |
| updatedAt | – | TIMESTAMPTZ | – | – | trigger | – |

---

## 4. ApplicantSkill (junction)

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| applicantId | – | UUID | PK part, FK | – | – | – |
| skillId | – | UUID | PK part, FK | – | – | – |
| proficiency | select | ENUM('Beginner','Intermediate','Advanced') | NULL | single choice | enum check | `Intermediate` |

Composite PK `(applicantId, skillId)`; unique per pair.

---

## 5. SkillTag

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| skillId | – | UUID | PK | – | – | – |
| name | text | CITEXT | UNIQUE, NOT NULL | 1–50 chars | unique case‑insensitive | `React` |
| category | text | VARCHAR(50) | NULL | optional | – | `Frontend` |
| createdAt | – | TIMESTAMPTZ | – | – | – | – |

---

## 6. MediaPortfolio

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| mediaId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | – | – |
| fileUrl | – | TEXT | NOT NULL | – | signed URL; virus scan | `https://…/file.mp4` |
| mediaType | select | ENUM('image','video') | NOT NULL | radio/select | enum check | `image` |
| title | text | VARCHAR(120) | NULL | ≤120 | – | – |
| description | textarea | TEXT | NULL | ≤1,000 | – | – |
| uploadDate | – | TIMESTAMPTZ | DEFAULT NOW() | – | – | – |
| visibility | select | ENUM('public','private') | DEFAULT 'private' | – | – | – |

---

## 7.  Application

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| applicationId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | – | – |
| jobPostId | – | UUID/TEXT | External ref | – | FK to JM store | – |
| companyId | – | UUID/TEXT | External ref | – | – | – |
| status | badge | ENUM('Pending','Viewed','Accepted','Rejected') | NOT NULL | – | enum check | `Pending` |
| submissionDate | – | TIMESTAMPTZ | NOT NULL | – | default NOW() | – |
| updatedAt | – | TIMESTAMPTZ | – | – | trigger | – |
| feedback | textarea | TEXT | NULL | ≤2,000 | – | – |
| applicantCV | file | TEXT (URL) | NULL | PDF/DOCX ≤10MB | MIME/size check | – |
| coverLetter | file | TEXT (URL) | NULL | PDF/DOCX ≤10MB | – | – |
| fresherFlag | – | BOOLEAN | NULL | – | derived from JobPost | – |

---

## 8. SearchProfile (Premium)

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| searchProfileId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | – | – |
| profileName | text | VARCHAR(100) | NULL | ≤100 | – | `SG SWE roles` |
| desiredCountry | select | CHAR(2) | NULL | ISO list | `CHECK` | `VN` |
| desiredMinSalary | number | NUMERIC(10,2) | DEFAULT 0 | ≥0 | `CHECK (desiredMinSalary>=0)` | `0.00` |
| desiredMaxSalary | number | NUMERIC(10,2) | NULL | ≥ min | `CHECK (desiredMaxSalary IS NULL OR desiredMaxSalary>=desiredMinSalary)` | – |
| jobTitles | textarea | TEXT | NULL | semicolon‑separated; trim tokens | BE to normalise and dedupe | `"Software Engineer; Backend Developer"` |
| technicalBackground | tags | JSONB | NULL | tags | validate array of strings | `["Kafka","React"]` |
| employmentStatus | multiselect | JSONB | NULL | any of {Full‑time, Part‑time, Fresher, Internship, Contract} | validate set; if neither FT nor PT ⇒ include both in matcher | `["Full-time","Internship"]` |
| createdAt | – | TIMESTAMPTZ | – | – | – | – |
| updatedAt | – | TIMESTAMPTZ | – | – | – | – |
| isActive | toggle | BOOLEAN | DEFAULT true | – | – | true |

---

## 9. Subscription

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| subscriptionId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | – | – |
| planType | select | ENUM('Free','Premium') | NOT NULL | – | enum | `Premium` |
| startDate | – | TIMESTAMPTZ | NOT NULL | – | – | – |
| expiryDate | – | TIMESTAMPTZ | NULL | – | – | – |
| isActive | toggle | BOOLEAN | NOT NULL | – | computed from dates + last payment | true |

---

## 10. PaymentTransaction

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| transactionId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | – | – |
| email | email | CITEXT | NOT NULL | email regex | same as Applicant | billing email |
| amount | – | NUMERIC(10,2) | NOT NULL | fixed 10.00 | `CHECK (amount=10.00)` | `10.00` |
| currency | select | CHAR(3) | NOT NULL | fixed `USD` | `CHECK (currency='USD')` | `USD` |
| gateway | select | ENUM('Stripe','PayPal') | NOT NULL | selected provider | enum | `Stripe` |
| timestamp | – | TIMESTAMPTZ | NOT NULL | – | – | – |
| status | badge | ENUM('Success','Failed') | NOT NULL | – | enum | `Success` |

---

## 11. Notification

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| notificationId | – | UUID | PK | – | – | – |
| applicantId | – | UUID | FK | – | – | – |
| type | badge | ENUM('ApplicationUpdate','Recommendation','System') | NOT NULL | – | enum | `Recommendation` |
| message | – | TEXT | NOT NULL | – | ≤2,000 chars | – |
| isRead | toggle | BOOLEAN | DEFAULT false | – | – | false |
| timestamp | – | TIMESTAMPTZ | DEFAULT NOW() | – | – | – |

---

## 12. SystemAdmin

| Attribute | Type (FE) | Type (BE) | Constraint | FE rule | BE rule | Example |
| --- | --- | --- | --- | --- | --- | --- |
| adminId | – | UUID | PK | – | – | – |
| fullName | text | VARCHAR(120) | NOT NULL | ≤120 | length check | – |
| email | email | CITEXT | UNIQUE, NOT NULL | email regex | unique index | – |
| passwordHash | – | VARCHAR(255) | NOT NULL | – | Argon2id/BCrypt | – |
| role | select | ENUM('Moderator','SystemAdmin') | NOT NULL | – | enum | `SystemAdmin` |
| lastLogin | – | TIMESTAMPTZ | NULL | – | – | – |

---

## Search & Sharding Notes (SRS §4.3.1)

- **Shard key**: `country` (Applicant) – used for routing profile reads/writes; Job search defaults to `VN`.
- **FTS** (Job posts – external JM): ensure index on Title, Description, RequiredSkills; Applicant side passes case‑insensitive title and selected country to narrow to a single shard.

---

## Edge‑Case Examples (QA data pack)

- **Email**: `very.long+label-1234567890@sub.sub2.sub3.example-travel.agency` (length 80, valid), `a@b.co` (shortest practical).
- **Password**: `A1!aaaaa` (min valid), `A....1!....` (unicode punctuation allowed).
- **Phone**: `+49` + `123456789012` (12 local digits = max), reject `+490123` (leading 0 after dial code if business rule forbids).
- **Address i18n**: `Đường Lê Lợi 12, Quận 1` (unicode, accents).
- **Skills**: tags dedupe `react`, `React`, `REACT` → `React`.
- **Media**: upload a 12MB image → FE blocks; BE re‑checks; rejection message localized.
- **Shard migration**: change `VN` → `SG` performs atomic copy to `SG` shard, updates `shardKey`, publishes Kafka `profile.updated` (keys: applicantId, oldCountry, newCountry, changedFields).
- **Token security**: try to use revoked JWE → Redis deny‑list hit; 401 returned.


## Responsibilities & Traceability

- **Frontend:** wire FE validators to each form field per tables; enforce dropdowns (Country), multiselect (Employment Status), lazy load lists; responsive SRS §4.2.5.
- **Backend:** enforce constraints, indexes, uniqueness, Argon2id hashing, JWE issue/refresh/revoke, Redis revocation cache, brute‑force guard, shard routing & migration hooks; Kafka publish on skills/country changes (§3.3.1–§3.3.2).
- **QA:** use edge‑case pack to assert FE/BE parity and error messaging clarity.