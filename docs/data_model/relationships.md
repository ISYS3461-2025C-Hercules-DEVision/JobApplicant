# DEVision – Job Applicant Subsystem  
## DM-03 — Model Relationships & Cardinalities

This document defines all entity-to-entity relationships for the Job Applicant subsystem, based on:

- DM-01 (`entities-list.md`)
- DM-02 (`entities-datatypes.md`)
- Full Job Applicant Functional Requirements
- Microservice + sharding requirements (Ultimo)

It includes:

- Relationship type (1:1, 1:N, N:M)  
- Cardinality & optionality  
- Cascade rules  
- Join tables  
- Sharding + microservice considerations  
- Explicit distinction between relational vs. embedded structures  

---

# 1. Applicant-Centric Relationships

## 1.1 Applicant ↔ Resume  
**Type:** 1 : 1  
**Cardinality:**  
- Applicant → exactly **1**  
- Resume → **0 or 1**  

**FK:** `Resume.applicantId`  
**Cascade:** Delete Applicant → delete Resume  
**Notes:** Resume is optional until the user creates profile text fields.  
**SRS:** §3.1

---

## 1.2 Applicant ↔ ApplicantSkill (junction / true N:M)  
**Type:** 1 : N (Applicant to ApplicantSkill)  
Represents the normalized **N:M** relationship between Applicant and SkillTag.  
**Cardinality:**  
- Applicant → **0..n** ApplicantSkill rows  
- SkillTag → **0..n** ApplicantSkill rows  

**Cascade:**  
- Delete Applicant → delete ApplicantSkill  
- Delete SkillTag → delete ApplicantSkill (or soft-delete SkillTag)  

**Notes:**  
- This is the **only N:M table** in the subsystem.  
- Resume.skills (JSONB array) does NOT replace ApplicantSkill.  

**SRS:** §3.2.2

---

## 1.3 Applicant ↔ MediaPortfolio  
**Type:** 1 : N  
**Cardinality:**  
- Applicant → **0..n** media items 

**Cascade:** Delete Applicant → delete MediaPortfolio  
**Notes:** Stores only file URLs.  
**SRS:** §3.2.3

---

## 1.4 Applicant ↔ Application  
**Type:** 1 : N  
**Cardinality:**  
- Applicant → **0..n** applications  

**Cascade:** **Soft-delete Applicant**, DO NOT delete Applications  
**Reason:** Job Manager must retain historical applications.  
**Notes:** Application references JobPost + Company from external system.  
**SRS:** §4.2.3

---

## 1.5 Applicant ↔ SearchProfile  
**Type:** 1 : N  
**Cardinality:** Applicant → **0..n** saved search profiles  
**Cascade:** Delete Applicant → delete SearchProfiles  
**SRS:** §5.2.x

---

## 1.6 Applicant ↔ Subscription  
**Type:** 1 : N  
**Cardinality:**  
- Applicant → **0..n** subscriptions (renewals/history)  

**Cascade:** Delete Applicant → delete Subscription rows  
**Notes:** There is only **1 active** subscription at a time.  
**SRS:** §5.1

---

## 1.7 Applicant ↔ PaymentTransaction  
**Type:** 1 : N  
**Cardinality:**  
- Applicant → **0..n** payment transactions  

**Cascade:** Delete Applicant → delete PaymentTransactions  
**Notes:**  
- PaymentTransaction also links to Subscription (see Section 2.3).  
- Contains billing email, gateway, amount, currency.  

**SRS:** §5.1.2

---

## 1.8 Applicant ↔ Notification  
**Type:** 1 : N  
**Cardinality:**  
- Applicant → **0..n** notifications  

**Cascade:** Delete Applicant → delete Notifications  
**SRS:** §5.3

---

## 1.9 Applicant ↔ AuthToken  
**Type:** 1 : N  
**Cardinality:**  
- Applicant → **0..n** tokens  

**Cascade:** Delete Applicant → delete AuthToken  
**Notes:** Redis stores token revocation state.  
**SRS:** §2.2–2.3

---

# 2. Resume-Embedded Structures (Non-Relationships)

## 2.1 Resume → Education (JSONB array)  
**Type:** Embedded list  
**Notes:** Not a separate entity; stored within Resume.  
**SRS:** §3.1.3

## 2.2 Resume → WorkExperience (JSONB array)  
**Type:** Embedded list  
**Notes:** Not a separate entity.  
**SRS:** §3.1.4

## 2.3 Resume → Skills (JSONB array of strings)  
**Type:** Embedded tag list  
**Notes:**  
- Does NOT form relationships.  
- ApplicantSkill is the official relational N:M link.

## 2.4 Resume → Certifications (JSONB array of strings)  
**Type:** Embedded list  
**Notes:** No relationship.

---

# 3. Skill Relationships

## 3.1 SkillTag ↔ ApplicantSkill  
**Type:** 1 : N  
**Cardinality:**  
- SkillTag → **0..n** ApplicantSkill rows  

**Cascade:** Recommended soft-delete SkillTag  
**Notes:** Prevents broken applicant profiles.  
**SRS:** §3.2.2, §5.2.2

## 3.2 Applicant ↔ SkillTag (N:M)  
Implemented entirely via ApplicantSkill.

---

# 4. Application External References (Cross-Subsystem)

## 4.1 Application ↔ JobPost (Job Manager subsystem)  
**Type:** N : 1  
**Cardinality:**  
- Application → must reference 1 JobPost  

**Cascade:** None (external DB)  
**Notes:** jobPostId stored as text/UUID but FK not enforced.  
**SRS:** JM §4

## 4.2 Application ↔ Company (Job Manager subsystem)  
**Type:** N : 1  
**Cardinality:**  
- Application → must reference 1 Company  

**Cascade:** None  
**Notes:** companyId stored as external ID.  
**SRS:** JM §1–4

---

# 5. Subscription ↔ PaymentTransaction

## 5.1 Subscription ↔ PaymentTransaction  
**Type:** 1 : N  
**Cardinality:**  
- Subscription → **0..n** PaymentTransactions  
- PaymentTransaction → exactly **1** Subscription  

**FK:** `PaymentTransaction.subscriptionId`  
**Cascade:** Delete Subscription → delete PaymentTransactions  
**Notes:**  
- PaymentTransaction also keeps `applicantId` redundantly for query convenience.  
- Supports multi-month renewals and recurring billing.  

**SRS:** §5.1–5.2

---

# 6. SystemAdmin (Independent)

## 6.1 SystemAdmin  
**Type:** Independent entity  
**Relationships:** None  
**Notes:** Admin actions do not use FK relationships.  
**SRS:** §6

---

# 7. Sharding & Microservice Considerations

- **Shard Key:** `Applicant.country`  
- All dependent entities (Resume, ApplicantSkill, Media, Application, Notification, SearchProfile, Subscription, PaymentTransaction, AuthToken) must live in the **same shard**.  
- Cross-subsystem relations are **references only**, no physical FK (Application → JobPost, Company).  
- Microservices own their entities; only Applicant-related data is sharded.

---

# 8. Relationship Summary Table

| Relationship | Type | Cardinality | Optionality | Cascade |
|-------------|------|-------------|-------------|---------|
| Applicant — Resume | 1 : 1 | 1 ↔ 0/1 | Resume optional | Cascade |
| Applicant — ApplicantSkill | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| SkillTag — ApplicantSkill | 1 : N | 1 ↔ 0..n | Optional | Soft-delete SkillTag |
| Applicant — MediaPortfolio | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Applicant — Application | 1 : N | 1 ↔ 0..n | Optional | Soft-delete Applicant |
| Applicant — SearchProfile | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Applicant — Subscription | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Applicant — PaymentTransaction | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Subscription — PaymentTransaction | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Applicant — Notification | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Applicant — AuthToken | 1 : N | 1 ↔ 0..n | Optional | Cascade |
| Application — JobPost | N : 1 | n ↔ 1 | Mandatory | No cascade |
| Application — Company | N : 1 | n ↔ 1 | Mandatory | No cascade |
