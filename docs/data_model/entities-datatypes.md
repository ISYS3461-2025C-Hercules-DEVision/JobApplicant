# DM-02 — Entities, Data Types & Validation (Ultimo / Microservices, MongoDB Edition)
> Source: EEET2582_DevVision-JobApplicant-v1.1.pdf  
> Applies to sections 1–6  
> This version uses **MongoDB-native BSON types** for all entities.

All attributes are preliminary for ER Model v2.

---

# Conventions & Notes

- **ID format:** `uuid` (stored as MongoDB `String`).  
- **Timestamps:** BSON `Date` (ISO-8601).  
  Example: `ISODate("2025-01-10T08:30:00Z")`.  
- **Text:** `String`  
- **Numbers:** `Number` (int or double)  
- **Money:** `Decimal128`  
- **Boolean:** `Boolean`  
- **Arrays:** `Array<T>`  
- **No cross-database FK constraints** across microservices.  
- **Internal vs external fields**: Sensitive fields are marked `internalOnly`.  
- **Sharding:** Profile DB is sharded by `country` (shardKey = country).  
- **FTS:** Implemented via MongoDB text indexes.

---

# 1. Profile Service Entities  
### Database: **MongoDB Sharded Cluster**  
### Shard Key: `country`

Entities: Applicant, Education, WorkExperience, SkillTag, ApplicantSkill, MediaPortfolio, Resume, SearchProfile

---

## 1.1 Applicant  
```
applicantId       : String (uuid)  PK
fullName          : String (max 200)
email             : String (unique)
country           : String  <-- shardKey
city              : String | null
streetAddress     : String | null
phoneNumber       : String | null
profileImageUrl   : String | null
isActivated       : Boolean  (default: false)
isArchived        : Boolean  (default: false)
createdAt         : Date
updatedAt         : Date
deletedAt         : Date | null
```

**Indexes:**  
- `{ country: 1 }` (sharding)  
- `{ email: 1 }` (unique, global)  
- `{ createdAt: 1 }`

---

## 1.2 Education  
```
educationId : String (uuid)
applicantId : String (uuid)
degree      : String
institution : String
fromYear    : Number
toYear      : Number | null
gpa         : Number | null
createdAt   : Date
updatedAt   : Date
```

**Indexes:** `{ applicantId: 1 }`

---

## 1.3 WorkExperience  
```
workExpId   : String (uuid)
applicantId : String (uuid)
jobTitle    : String
companyName : String
from        : Date   (ISO)
to          : Date | null
description : String
createdAt   : Date
updatedAt   : Date
```

**Text Index:** `jobTitle`, `description`  
**Indexes:** `{ applicantId: 1 }`

---

## 1.4 SkillTag (Global Catalog)
```
skillId   : String (uuid)
name      : String (unique, lowercase)
category  : String | null
createdAt : Date
```

**Indexes:** `{ name: 1 }` (unique)

---

## 1.5 ApplicantSkill  
```
id          : String (uuid)
applicantId : String (uuid)
skillId     : String (uuid)
proficiency : String (enum: Beginner | Intermediate | Advanced)
endorsedBy  : Array<String(uuid)>
createdAt   : Date
updatedAt   : Date
```

**Indexes:**  
- `{ applicantId: 1 }`  
- `{ skillId: 1 }`

---

## 1.6 MediaPortfolio  
```
mediaId     : String (uuid)
applicantId : String (uuid)
fileUrl     : String
mediaType   : String (enum: image | video)
title       : String
description : String
visibility  : String (enum: public | private)
createdAt   : Date
```

**Indexes:** `{ applicantId: 1 }`

---

## 1.7 Resume  
```
resumeId     : String (uuid)
applicantId  : String (uuid)
headline     : String
objective    : String
education    : Array<String(uuid)>  OR embedded objects
experience   : Array<String(uuid)>  OR embedded objects
skills       : Array<String>  // denormalized skill names
certifications : Array<String>
createdAt    : Date
updatedAt    : Date
```

**Text Index:** objective  
**Notes:** Resume.skills is denormalized; ApplicantSkill is canonical.

---

## 1.8 SearchProfile  
```
searchProfileId     : String (uuid)
applicantId         : String (uuid)
profileName         : String
desiredCountry       : String
desiredMinSalary     : Decimal128
desiredMaxSalary     : Decimal128
jobTitles            : Array<String>
technicalBackground  : Array<String>  // skill names or skillIds
employmentStatus     : Array<String>  // enum: Full-time, Part-time, ...
isActive             : Boolean
createdAt            : Date
updatedAt            : Date
```

**Indexes:** `{ applicantId: 1 }`, `{ desiredCountry: 1 }`

---

# 2. Auth Service Entities  
### Database: MongoDB + Redis

## 2.1 AuthAccount  
```
authId        : String (uuid)
applicantId   : String (uuid)
email         : String (unique)
passwordHash  : String   // internalOnly
ssoProvider   : String   // enum
ssoId         : String | null
isActivated   : Boolean
createdAt     : Date
updatedAt     : Date
failedAttempts: Number
```

**Indexes:** `{ email: 1 } (unique)`

---

## 2.2 AuthToken  
```
tokenId       : String (uuid)
authId        : String (uuid)
issuedAt      : Date
expiresAt     : Date
tokenType     : String (access | refresh)
isRevoked     : Boolean
createdAt     : Date
updatedAt     : Date
```

Redis stores:
- tokenId denylist  
- TTL for expiry  

---

# 3. Application Service Entities  
### Database: MongoDB (Global)

## 3.1 Application  
```
applicationId : String (uuid)
applicantId   : String (uuid)   // reference only
jobPostId     : String          // external JM ID
companyId     : String          // external JM ID
status        : String (Pending | Viewed | Accepted | Rejected)
submissionDate: Date
updatedAt     : Date
feedback      : String
applicantCV   : String | null   // CVFileReference.fileId
coverLetter   : String | null   // CoverLetter.fileId
createdAt     : Date
deletedAt     : Date | null
isArchived    : Boolean
```

**Indexes:** `{ applicantId: 1 }`, `{ jobPostId: 1 }`, `{ status: 1 }`

---

## 3.2 CVFileReference / CoverLetterReference  
```
fileId       : String (uuid)
applicationId: String (uuid)
fileUrl      : String
fileType     : String (pdf | docx)
createdAt    : Date
```

---

# 4. Subscription & Payment Service Entities  
### Databases: MongoDB (Global)

## 4.1 Subscription  
```
subscriptionId : String (uuid)
applicantId    : String (uuid)
planType       : String (Free | Premium)
startDate      : Date
expiryDate     : Date
isActive       : Boolean
createdAt      : Date
updatedAt      : Date
```

---

## 4.2 PaymentTransaction  
```
transactionId  : String (uuid)
applicantId    : String (uuid)
email          : String
amount         : Decimal128
currency       : String
gateway        : String (Stripe | PayPal)
status         : String (Success | Failed)
timestamp      : Date
```

---

# 5. Notification Service Entities  
### Database: MongoDB (Global)

## 5.1 Notification  
```
notificationId : String (uuid)
recipientId    : String (uuid)
type           : String (ApplicationUpdate | JobMatch | System)
message        : String
isRead         : Boolean
timestamp      : Date
```

**Indexes:** `{ recipientId: 1 }`, `{ isRead: 1 }`

---

# 6. Admin Service Entities  
### Database: MongoDB (Global)

## 6.1 SystemAdmin  
```
adminId     : String (uuid)
fullName    : String
email       : String
passwordHash: String  // internalOnly
role        : String (Moderator | SystemAdmin)
lastLogin   : Date
```

---

# SHARD MIGRATION PROCEDURE  
*(MongoDB version)*

(unchanged logic, fully compatible with sharded cluster)

---

# EVENTS  
*(MongoDB storage-compatible; JSON payloads remain unchanged)*

---

# DTO VISIBILITY, INDEXING, SECURITY  
*(unchanged and fully valid for MongoDB)*
