JobApplicant_Milestone1_Backlog.md
# 🧾 DEVision – Job Applicant Subsystem  
## Milestone 1 Detailed Product Backlog (Squad Hercules)

> Architecture Type: Microservice Backend (Ultimo) + Componentized Frontend (Medium)  
> Tooling: Visual Paradigm • React • Spring Boot • Kafka • Redis • Docker  
> Members:  
> @ElwizScott (Kien) • @ctungnguyen (Nguyen) • @HoangSon0810 (Son) • @xuandat2001 (Dat) • @phat041102 (Phat)

---

# EPIC 1 – Data Model Design (Sprint 1)

DM-01 – Identify Core Entities and Attributes
## DM-01 – Identify Core Entities and Attributes
🏷️ Labels: type:data-model, milestone:1, sprint:1, priority:high

### 🎯 Goal
Collect and list every entity implied by the SRS (registration, profile, education, experience, skills, applications, subscription, admin, company references). For each entity provide a short description and the main attributes. This creates the canonical inventory that all diagrams and services will reference.

### 📄 Deliverables
- `/docs/data-model/entities-list.md` (table of entities)
- For each entity: name, short description, suggested attribute list (name, type, required/optional)
- One-line rationale linking each entity to SRS sections (with citations)

### 🧰 Tools / Resources
- EEET2582_DevVision-JobApplicant-v1.1.pdf (functional reqs)
- Visual Paradigm (for later ERD)
- ProjectCharterJobApplicant.docx (context)

### 👥 Assignees
@ElwizScott, @ctungnguyen

### ✅ Definition of Done
- [ ] Entities list committed to `/docs/data-model/entities-list.md`
- [ ] At least one architecture reviewer (@HoangSon0810) has given feedback in a GitHub Discussion
- [ ] Entities referenced by IDs in later ERD tasks

---

DM-02 – Define Attributes, Types & Validation Rules
## DM-02 – Define Attributes, Types & Validation Rules
🏷️ Labels: type:data-model, milestone:1, sprint:1, priority:high

### 🎯 Goal
For each entity from DM-01, define attribute data types, constraints (unique, nullable), example values, and frontend/backend validation rules (e.g., email regex, password strength). This ensures consistency between ERD, DTOs and UI forms.

### 📄 Deliverables
- `/docs/data-model/attributes-and-validations.md`
- Table: Entity → Attribute → Type → Constraint → Frontend validation rule → Backend validation rule
- Example values for edge-cases (max length, unicode, empty fields)

### 🧰 Tools / Resources
- JSON Schema examples for validation
- SRS validation rules (password strength, phone format)

### 👥 Assignees
@ctungnguyen, @ElwizScott

### ✅ Definition of Done
- [ ] Attributes file committed
- [ ] Validation rules mapped to specific frontend form fields (for Dat)
- [ ] Backend constraints (unique email) documented for Son

---

DM-03 – Model Relationships & Cardinalities (ERD draft)
## DM-03 – Model Relationships & Cardinalities (ERD draft)
🏷️ Labels: type:data-model, milestone:1, sprint:1, review:required

### 🎯 Goal
Define and document all relationships (1:1, 1:n, n:m) and required join entities (e.g., Applicant ↔ Skill via ApplicantSkill). Include cardinality, optionality, and cascade behavior (delete/update).

### 📄 Deliverables
- Updated ERD notes in `/docs/data-model/relationships.md`
- List of join tables / link documents for many-to-many relations
- Decisions on cascade rules and soft-delete vs hard-delete

### 🧰 Tools / Resources
- Visual Paradigm for diagram creation
- Reference DB design patterns (many-to-many, soft delete)

### 👥 Assignees
@ElwizScott, @ctungnguyen

### ✅ Definition of Done
- [ ] Relationship doc committed
- [ ] At least 2 reviewers (@HoangSon0810, @xuandat2001) have commented
- [ ] Changes applied to ERD v1 in DM-04

---

DM-04 – Build ER Diagram v1 (Visual Paradigm)
## DM-04 – Build ER Diagram v1 (Visual Paradigm)
🏷️ Labels: type:data-model, milestone:1, sprint:1, priority:high

### 🎯 Goal
Produce the first conceptual ERD (high-level, normalized) in Visual Paradigm showing entities, attributes (key attributes only), and relationships (cardinalities). This is the first visual artifact for peer review.

### 📄 Deliverables
- `/docs/data-model/ERD-JA-v1.vpp` (Visual Paradigm file)
- `/docs/data-model/ERD-JA-v1.png` and `.pdf` (exports)
- Short README explaining notations used and assumptions

### 🧰 Tools / Resources
- Visual Paradigm
- ERD notation guide (Crow’s Foot recommended)

### 👥 Assignees
@ElwizScott, @ctungnguyen

### ✅ Definition of Done
- [ ] Files uploaded to `/docs/data-model/`
- [ ] PR opened and assigned to @HoangSon0810 and @xuandat2001 for review
- [ ] Peer comments addressed or recorded

---

DM-05 – Refine ERD to Support Microservice Boundaries (ERD v2)
## DM-05 – Refine ERD to Support Microservice Boundaries (ERD v2)
🏷️ Labels: type:data-model, type:architecture, milestone:1, sprint:1, review:required

### 🎯 Goal
Adjust ERD to show how data ownership maps to microservice boundaries (Auth, Profile, Application, Subscription). Mark which entities live in which service DB and any duplication or denormalization choices required for performance/decoupling.

### 📄 Deliverables
- `/docs/data-model/ERD-JA-v2.vpp` and export images
- Table mapping Entity → Owning Service → DB type (e.g., MongoDB shard)
- Notes on denormalization, replication, and consistency strategies

### 🧰 Tools / Resources
- Discussions with Son (backend) and Phat (devops)
- SRS requirements about sharding (Country as candidate)

### 👥 Assignees
@ElwizScott, @ctungnguyen — reviewers: @HoangSon0810, @phat041102

### ✅ Definition of Done
- [ ] ERD v2 committed
- [ ] Ownership table complete
- [ ] Design decisions captured in `/docs/data-model/design-decisions.md`

---

DM-06 – Sharding Strategy & Indexing Plan
## DM-06 – Sharding Strategy & Indexing Plan
🏷️ Labels: type:data-model, milestone:1, sprint:1, priority:medium

### 🎯 Goal
Propose a database sharding/key strategy (suggest Country as shard key per SRS), outline index strategy for frequent queries (search by skills, location), and list implications for cross-shard queries.

### 📄 Deliverables
- `/docs/data-model/sharding-and-indexing.md`
- Index list for primary search operations (FTS, tag lookup)
- Example queries showing planned routing (pseudo-SQL/NoSQL)

### 🧰 Tools / Resources
- MongoDB sharding docs
- Example search query patterns from SRS (job search, applicant search)

### 👥 Assignees
@ElwizScott (lead), @ctungnguyen (co-author) — consult: @HoangSon0810

### ✅ Definition of Done
- [ ] Sharding doc committed
- [ ] Index recommendations reviewed by Son and Phat
- [ ] Potential performance trade-offs noted

---

DM-07 – DTO & API Data Contract Mapping (Internal vs External DTOs)
## DM-07 – DTO & API Data Contract Mapping (Internal vs External DTOs)
🏷️ Labels: type:data-model, milestone:1, sprint:2, review:required

### 🎯 Goal
Define DTOs for inter-service communication and frontend consumption. Distinguish internal DTOs (full entities) vs external DTOs (sanitized public versions). Map ER entities to API payloads and note sensitive fields to exclude.

### 📄 Deliverables
- `/docs/data-model/dto-mapping.md`
- Example JSON payloads for Applicant profile, Application submission, Subscription status
- Listing of fields removed/obfuscated for external DTOs (e.g., payment details, raw password hashes)

### 🧰 Tools / Resources
- API contract template (used later in CT-02)
- Security & privacy guidance

### 👥 Assignees
@ctungnguyen, @ElwizScott — reviewers: @HoangSon0810

### ✅ Definition of Done
- [ ] DTO mapping file committed
- [ ] External DTOs reviewed for privacy by team
- [ ] Mapping referenced in API contract draft (integration)

---

DM-08 – Sample DB Seed Script & Example Data
## DM-08 – Sample DB Seed Script & Example Data
🏷️ Labels: type:data-model, milestone:1, sprint:2

### 🎯 Goal
Create a small seed script (JSON or JS) that inserts representative example data for ERD validation and presentation demos. Include one premium applicant, multiple skills, and sample applications.

### 📄 Deliverables
- `/docs/data-model/sample-seed.json`
- README explaining how to load into local MongoDB (or mock)
- Example queries showing expected results

### 🧰 Tools / Resources
- MongoDB import examples
- SRS sample data requirements for Milestone 2

### 👥 Assignees
@ElwizScott, @ctungnguyen, @phat041102 (assist with load instructions)

### ✅ Definition of Done
- [ ] Seed file committed
- [ ] Instructions validated locally by Phat
- [ ] Used in at least one demo slide

---

DM-09 – Data Model Justification Section (single location)
## DM-09 – Data Model Justification Section (single location)
🏷️ Labels: type:data-model, type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Write the required single-section justification that lists advantages and drawbacks of the chosen data model (do NOT split advantages/drawbacks across separate sections to avoid penalty). Provide concise reasoning against the six architecture criteria as they relate to the data model.

### 📄 Deliverables
- `/docs/report/data-model-justification.md` (single section)
- Short bullet list: Advantages / Drawbacks (in same section)
- Cross-reference to ERD v2 and sharding decisions

### 🧰 Tools / Resources
- Submission rubric
- ERD files

### 👥 Assignees
@ElwizScott (lead writer), @ctungnguyen (co-editor)

### ✅ Definition of Done
- [ ] Justification file committed
- [ ] Peer-reviewed by Son for technical accuracy
- [ ] Included in Milestone1_Report.docx (RP-01)

---

DM-10 – Peer Review & Sign-off Log for Data Model
## DM-10 – Peer Review & Sign-off Log for Data Model
🏷️ Labels: documentation, milestone:1, sprint:2

### 🎯 Goal
Record the peer review process, comments, and final sign-off from team members. This is evidence of contribution and will be appended to the report appendix.

### 📄 Deliverables
- `/docs/data-model/review-log.md` with timestamped entries
- Pull requests or issue links for each review
- Final sign-off note with names and dates

### 🧰 Tools / Resources
- GitHub PRs and Discussions
- Team meeting minutes

### 👥 Assignees
@ElwizScott, @ctungnguyen

### ✅ Definition of Done
- [ ] Review log committed
- [ ] At least two reviewers (Son & Dat) have signed off
- [ ] Log referenced in report appendix

---

# 🏗️ EPIC 2 – System Architecture Design (Sprint 1–2)

AR-01 – Define System Scope and Boundaries
## AR-01 – Define System Scope and Boundaries
🏷️ Labels: type:architecture, milestone:1, sprint:1, priority:high

### 🎯 Goal
Identify the full system scope of the Job Applicant subsystem. Determine which features belong to this subsystem (Applicant registration, Application submission, Profile, Subscription) and which depend on external systems (Job Manager, Kafka, Redis).  
Clearly define boundaries to prevent overlap and integration conflicts across services.

### 📄 Deliverables
- `/docs/architecture/system-scope.md`
- Context diagram separating internal vs. external modules
- Short rationale table for each boundary choice

### 🧰 Tools / Resources
- EEET2582_DevVision-JobApplicant-v1.1.pdf  
- ProjectCharterJobApplicant.docx  
- Visual Paradigm (for diagram)

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] System scope file committed to `/docs/architecture/`
- [ ] Reviewed by @ElwizScott for alignment with data model
- [ ] Used as introduction in report architecture section

---

AR-02 – Create C4 System Context Diagram
## AR-02 – Create C4 System Context Diagram
🏷️ Labels: type:architecture, milestone:1, sprint:1, priority:high

### 🎯 Goal
Develop a **C4 System Context Diagram** illustrating Job Applicant interactions with users and external systems (Job Manager, Kafka Broker, Redis, MongoDB).  
The diagram provides a high-level map of how external dependencies interact with the subsystem and ensures alignment with the overall DEVision ecosystem.

### 📄 Deliverables
- `/docs/architecture/C4-SystemContext.vpp`
- Exported `.png` and `.pdf` for Milestone report
- Short legend and textual explanation in `/docs/architecture/README.md`

### 🧰 Tools / Resources
- Visual Paradigm C4 template  
- DevVision architecture guidelines

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Diagram reviewed by @ElwizScott  
- [ ] Matches component responsibilities from DM-05  
- [ ] Version 1 added to report

---

AR-03 – Create C4 Container Diagram (Subsystem Internal View)
## AR-03 – Create C4 Container Diagram (Subsystem Internal View)
🏷️ Labels: type:architecture, milestone:1, sprint:1, priority:high

### 🎯 Goal
Design the **C4 Container Diagram** to break the subsystem into core deployable containers: Frontend, Backend Microservices, Kafka, Redis, Database.  
This will visualize how each container communicates and how data flows between them.

### 📄 Deliverables
- `/docs/architecture/C4-Container.vpp`
- `.png` and `.pdf` exports  
- Table mapping containers → primary responsibilities → communication protocols

### 🧰 Tools / Resources
- Visual Paradigm  
- EEET2582_DevVision-JobApplicant-v1.1.pdf (functional flow)  
- Data model from DM-05

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Container diagram committed  
- [ ] Integration points for Kafka and Redis marked  
- [ ] Approved by @ElwizScott

---

AR-04 – Define Backend Microservice Boundaries
## AR-04 – Define Backend Microservice Boundaries
🏷️ Labels: type:architecture, milestone:1, sprint:1, priority:high

### 🎯 Goal
Define the boundaries of each backend microservice — Auth, Profile, Application, Subscription — and describe their owned entities, APIs, and event responsibilities.  
Ensure clear service separation and minimal coupling, while supporting data ownership from the ERD.

### 📄 Deliverables
- `/docs/architecture/microservice-boundaries.md`
- Table of Service → Owned Entities → Exposed APIs → Kafka Topics
- Sequence diagram for service interactions

### 🧰 Tools / Resources
- Visual Paradigm Sequence Diagram  
- Data Model (DM-05)  
- CT-01 Kafka topic documentation

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Boundaries documented  
- [ ] File reviewed by @ElwizScott  
- [ ] Referenced in final justification

---

AR-05 – C4 Component Diagram for Backend Services
## AR-05 – C4 Component Diagram for Backend Services
🏷️ Labels: type:architecture, milestone:1, sprint:2, review:required

### 🎯 Goal
Produce a **C4 Component Diagram** for backend microservices showing internal layers: Controller, Service, Repository, DTO, Config.  
Highlight reusable modules and cross-service dependencies to help Son, Dat, and Phat plan development consistency.

### 📄 Deliverables
- `/docs/architecture/C4-Component-Backend.vpp`
- Export `.png` + `.pdf`
- Accompanying component-role table

### 🧰 Tools / Resources
- Visual Paradigm  
- Spring Boot architecture guide  

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] All microservices represented  
- [ ] Diagram peer-reviewed by @ElwizScott  
- [ ] Added to Milestone report

---

AR-06 – Frontend Component Hierarchy and State Flow
## AR-06 – Frontend Component Hierarchy and State Flow
🏷️ Labels: type:architecture, milestone:1, sprint:2, review:required

### 🎯 Goal
Develop a **React Component Hierarchy Diagram** representing all pages and components (Login, Register, Profile, Job Search, Application, Subscription).  
Document data flow (props, state, API fetches) and interaction with REST helpers for maintainability.

### 📄 Deliverables
- `/docs/architecture/Frontend-ComponentHierarchy.vpp`
- `/docs/architecture/Frontend-ComponentHierarchy.png`
- `/docs/frontend/component-flow.md`

### 🧰 Tools / Resources
- Visual Paradigm  
- React state management flow examples  

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Diagram reviewed by @ElwizScott  
- [ ] Consistency checked against API endpoints  
- [ ] Uploaded to `/docs/architecture/`

---

AR-07 – API Gateway and Request Flow Diagram
## AR-07 – API Gateway and Request Flow Diagram
🏷️ Labels: type:architecture, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Map how frontend requests flow through the API Gateway to backend microservices. Include authentication (JWT), routing, and load balancing.  
Clarify data flow for key user actions (Login, Profile Update, Job Apply).

### 📄 Deliverables
- `/docs/architecture/api-gateway-flow.md`
- Sequence diagram (Request–Gateway–Service–DB)
- Notes on error handling and retries

### 🧰 Tools / Resources
- Visual Paradigm Sequence Diagram  
- Spring Cloud Gateway documentation  

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Flow validated by team  
- [ ] Diagram integrated into final report  
- [ ] Reviewed by @ElwizScott

---

AR-08 – Message Flow and Kafka Integration
## AR-08 – Message Flow and Kafka Integration
🏷️ Labels: type:architecture, type:integration, milestone:1, sprint:2, review:required

### 🎯 Goal
Model event-driven architecture and Kafka topic integration across microservices.  
Show producers, consumers, and payload schemas (ApplicationCreated, ProfileUpdated, NotificationSent).  
Highlight how Job Manager subsystem subscribes to JobApplicant events.

### 📄 Deliverables
- `/docs/architecture/Kafka-MessageFlow.vpp`
- `/docs/integration/kafka-topics.md`
- Example message payloads

### 🧰 Tools / Resources
- Kafka integration doc (CT-01)  
- Visual Paradigm Message Diagram  

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Topics reviewed by both teams  
- [ ] Diagram uploaded to `/docs/integration/`  
- [ ] Included in Milestone report

---

AR-09 – Deployment Topology and Container Network Diagram
## AR-09 – Deployment Topology and Container Network Diagram
🏷️ Labels: type:architecture, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Design the **Deployment Topology Diagram** showing all Docker containers, services, databases, and network connections.  
This visualizes how the Job Applicant subsystem runs locally and connects to shared services in integration.

### 📄 Deliverables
- `/docs/architecture/deployment-topology.vpp`
- `/docs/architecture/deployment-topology.png`
- `/docs/architecture/deployment-config.md`

### 🧰 Tools / Resources
- Docker Compose configuration  
- Visual Paradigm Network Diagram  

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Network diagram reviewed by DevOps (Phat)
- [ ] Tested in local Compose setup
- [ ] Committed and linked in report

---

AR-10 – Architecture Justification and Trade-offs
## AR-10 – Architecture Justification and Trade-offs
🏷️ Labels: type:architecture, type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Write the **Architecture Justification** section combining advantages and drawbacks into one cohesive discussion (as required by marking guide).  
Explain why the microservice and componentized frontend architecture best fits scalability, maintainability, and independent deployability.  
Mention trade-offs such as increased deployment complexity and message consistency risks.

### 📄 Deliverables
- `/docs/report/architecture-justification.md`
- Comparative table: Architecture Alternative vs. Reason for Rejection
- Six-criteria summary with brief examples

### 🧰 Tools / Resources
- EEET2582_DevVision-JobApplicant-v1.1.pdf  
- Visual Paradigm diagrams AR-02 through AR-09  

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102

### ✅ Definition of Done
- [ ] Justification text approved by @ElwizScott  
- [ ] Included in Milestone1_Report.docx  
- [ ] Peer-reviewed for clarity and technical soundness

---

# EPIC 3 - Report Compilation

RP-01 – Assemble Report Structure and Content Framework
## RP-01 – Assemble Report Structure and Content Framework
🏷️ Labels: type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Define the overall layout and flow of the Milestone 1 Report.  
The structure should integrate both Data Model and Architecture sections consistently, with agreed page flow and style.  
Each section lead (Kien/Nguyen for data, Son/Dat/Phat for architecture) should know where to insert their content.

### 📄 Deliverables
- `/docs/Milestone1_Report.docx` skeleton with TOC, headers, numbering  
- `/docs/report/section-ownership.md` assigning who writes what  
- RMIT-compliant formatting (fonts, margins, figure captions)

### 🧰 Tools / Resources
- RMIT report format guide  
- Previous project charter and backlog content

### 👥 Assignees
@ElwizScott, @ctungnguyen, @HoangSon0810, @xuandat2001, @phat041102  

### ✅ Definition of Done
- [ ] Outline approved by all five members  
- [ ] Shared editable version in repo  
- [ ] Becomes base document for later sections

---

RP-02 – Integrate Diagrams and Visual Paradigm Exports
## RP-02 – Integrate Diagrams and Visual Paradigm Exports
🏷️ Labels: type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Collect and embed all diagrams from both sub-teams (ERDs by Data Model team and C4/Deployment by Architecture team) into the main report.  
Ensure consistent titles, captions, numbering, and sizing.

### 📄 Deliverables
- `/docs/report/assets/` folder with exported diagrams (.png + .pdf)  
- Updated `/docs/Milestone1_Report.docx` with figure captions  
- `/docs/report/diagram-index.md` listing filenames ↔ figure numbers

### 🧰 Tools / Resources
- Visual Paradigm exports  
- Report template from RP-01  

### 👥 Assignees
@ElwizScott, @ctungnguyen (Data Model visuals)  
@HoangSon0810, @xuandat2001, @phat041102 (Architecture visuals)

### ✅ Definition of Done
- [ ] All diagrams inserted with correct numbering  
- [ ] Consistent formatting verified  
- [ ] Reviewed by Kien and Son jointly

---

RP-03 – Write Data Model Justification Section
## RP-03 – Write Data Model Justification Section
🏷️ Labels: type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Write a concise justification for the Data Model design, explaining entity choices, relationships, and sharding strategy.  
Include how the model ensures scalability, consistency, and supports the microservice boundaries defined in architecture.

### 📄 Deliverables
- `/docs/report/data-model-justification.md`  
- Embedded snippets from DM-05 (ERD v2) and DM-06 (sharding plan)  
- Summary table: Design Decision → Reason → Impact on Architecture

### 🧰 Tools / Resources
- ERD v2 + Data Model notes  
- RMIT marking criteria (Justification section)  

### 👥 Assignees
@ElwizScott, @ctungnguyen  
Reviewer: @HoangSon0810  

### ✅ Definition of Done
- [ ] File committed and linked in report  
- [ ] Peer reviewed by architecture team  
- [ ] Referenced in final Milestone1_Report.docx

---

RP-04 – Write Architecture Justification Section
## RP-04 – Write Architecture Justification Section
🏷️ Labels: type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Write the justification for the chosen Microservice + Componentized Frontend architecture.  
Describe how this approach meets six criteria (scalability, maintainability, reusability, security, consistency, deployability).  
Include trade-offs and mitigation strategies.

### 📄 Deliverables
- `/docs/report/architecture-justification.md`  
- Table comparing alternatives (Monolith, Layered, Serverless)  
- Reference to diagrams AR-02 to AR-09

### 🧰 Tools / Resources
- EEET2582_DevVision-JobApplicant-v1.1.pdf  
- Visual Paradigm C4 diagrams

### 👥 Assignees
@HoangSon0810, @xuandat2001, @phat041102  
Reviewer: @ElwizScott  

### ✅ Definition of Done
- [ ] Section merged into main report  
- [ ] Reviewed for clarity and coherence with data model section  
- [ ] Approved by Kien before submission

---

RP-05 – Compose Executive Summary and Introduction
## RP-05 – Compose Executive Summary and Introduction
🏷️ Labels: type:report, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Write an executive summary outlining DEVision’s goal, the Job Applicant subsystem’s role, and the team’s Milestone 1 deliverables.  
This section gives readers quick context and the problem–solution overview.

### 📄 Deliverables
- `/docs/report/intro-summary.md`  
- Final copy merged into `/docs/Milestone1_Report.docx`

### 🧰 Tools / Resources
- Project Charter JobApplicant.docx  
- Squad README.md roles table  

### 👥 Assignees
@ElwizScott, @ctungnguyen (draft)  
@xuandat2001 (edit + merge)

### ✅ Definition of Done
- [ ] Section finalized and included in report  
- [ ] Reviewed by @HoangSon0810 for accuracy  
- [ ] Readable within 300 words

---

RP-06 – Draft Limitations, Risks, and Future Improvements
## RP-06 – Draft Limitations, Risks, and Future Improvements
🏷️ Labels: type:report, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Identify and document current system limitations, technical risks, and planned improvements for Milestone 2.  
Provide balanced reflection on what went well and what needs refinement (e.g., Kafka complexity, UI modularity, testing gaps).

### 📄 Deliverables
- `/docs/report/limitations-risks.md`  
- Bullet summary for report conclusion  

### 🧰 Tools / Resources
- Team retrospective notes  
- Architecture + Data Model review logs  

### 👥 Assignees
@HoangSon0810, @phat041102 (technical risks)  
@ElwizScott, @ctungnguyen (reflection on data design)

### ✅ Definition of Done
- [ ] Risks classified by type (Technical / Process / Integration)  
- [ ] Reviewed by @xuandat2001 for final report merge  
- [ ] Integrated into report conclusion

---

RP-07 – Format, Citations, and Reference Management
## RP-07 – Format, Citations, and Reference Management
🏷️ Labels: type:report, documentation, milestone:1, sprint:2

### 🎯 Goal
Unify styling, citations, and reference lists across sections.  
Ensure Harvard style citations, consistent figure numbering, and updated page headers/footers.  
Check that all contributors are credited in the authorship section.

### 📄 Deliverables
- `/docs/report/references.md` or `.bib`  
- Updated report with correct citations  
- Authorship page listing all five members

### 🧰 Tools / Resources
- RMIT citation guide  
- Word reference manager / Google Docs citations

### 👥 Assignees
@xuandat2001, @phat041102 (format + style)  
@ElwizScott (citations + authorship)

### ✅ Definition of Done
- [ ] Reference section validated by lecturer checklist  
- [ ] Authorship page completed  
- [ ] Document exported to PDF without format errors

---

RP-08 – Peer Review and Revision Log
## RP-08 – Peer Review and Revision Log
🏷️ Labels: documentation, milestone:1, sprint:2, review:required

### 🎯 Goal
Track peer review activities for each section (Data Model + Architecture + Report).  
Record who reviewed what, main feedback points, and applied changes.  
This serves as evidence of team collaboration.

### 📄 Deliverables
- `/docs/report/review-log.md` with timestamped entries  
- Screenshots or PR links showing feedback integration  

### 🧰 Tools / Resources
- GitHub Discussions & Pull Requests  
- Word “Track Changes” feature  

### 👥 Assignees
@All members  

### ✅ Definition of Done
- [ ] Each member has reviewed at least 2 sections  
- [ ] Log attached as appendix in final report  
- [ ] Signed off by team lead (@ElwizScott)

---

RP-09 – Final Compilation, PDF Export, and Sign-off
## RP-09 – Final Compilation, PDF Export, and Sign-off
🏷️ Labels: type:report, milestone:1, sprint:2, priority:high

### 🎯 Goal
Merge all sections and export the final Milestone 1 report as PDF for submission.  
Check file integrity, TOC, citations, and appendices.  
Add team sign-off page and ensure uploaded to Canvas.

### 📄 Deliverables
- `/docs/Milestone1_Report_Final.pdf`  
- `/submission/README.md` (summary + signatures)  
- GitHub release tag “M1-final”

### 🧰 Tools / Resources
- Word or Google Docs export  
- GitHub version history  

### 👥 Assignees
@All members (equal responsibility)

### ✅ Definition of Done
- [ ] PDF reviewed by every member  
- [ ] Digital signatures added in README  
- [ ] Submitted and confirmed on Canvas

---

# EPIC 4 – GitHub & Workflow Setup

GH-01 – Initialize Repository Structure
## GH-01 – Initialize Repository Structure
🏷️ Labels: type:workflow, milestone:1, sprint:1, priority:high

### 🎯 Goal
Set up the Job Applicant repository structure following microservice-based backend and componentized frontend principles.  
Ensure correct folder hierarchy, `.gitignore`, and basic files to support modular feature development and report storage.

### 📄 Deliverables
- `/frontend/` and `/backend/` folders
- `/docs/` for design reports
- `.github/` for templates and workflows
- `.gitignore`, `LICENSE`, and initial `README.md`

### 👥 Assignees
@phat041102 (lead setup)  
@HoangSon0810 (backend structure)  
@xuandat2001 (frontend folders)  
@ctungnguyen (documentation structure)  
Reviewer: @ElwizScott

### ✅ Definition of Done
- [ ] Repo created and pushed to org  
- [ ] Folder hierarchy consistent with design plan  
- [ ] README updated with project description

---

GH-02 – Configure GitHub Project Board and Workflow Columns
## GH-02 – Configure GitHub Project Board and Workflow Columns
🏷️ Labels: type:workflow, milestone:1, sprint:1, priority:high

### 🎯 Goal
Create and configure the **GitHub Project Board** for the Job Applicant team.  
Follow the Squad’s Agile flow: Product Backlog → Sprint Backlog → In-Progress → Review → Staging → Done → Meeting Minute.  
Enable auto-move of issues via GitHub Actions.

### 📄 Deliverables
- GitHub Project Board with all columns  
- Workflow automation (auto-move on merge)  
- Shared access with all 5 members

### 👥 Assignees
@ElwizScott (board setup)  
@ctungnguyen (label verification)  
@phat041102 (automation config)  
@HoangSon0810 (review + test)

### ✅ Definition of Done
- [ ] Columns added correctly  
- [ ] Automation tested successfully  
- [ ] Shared with Job Manager subteam

---

GH-03 – Create Issue and Pull Request Templates
## GH-03 – Create Issue and Pull Request Templates
🏷️ Labels: type:workflow, milestone:1, sprint:1, priority:high

### 🎯 Goal
Develop standardized Issue and PR templates for consistency.  
Each issue template must include a description, definition of done, assignees, and milestone field.

### 📄 Deliverables
- `.github/ISSUE_TEMPLATE/feature-request.md`
- `.github/ISSUE_TEMPLATE/bug-report.md`
- `.github/PULL_REQUEST_TEMPLATE.md`

### 👥 Assignees
@xuandat2001 (template author)  
@ctungnguyen (review for clarity)  
@phat041102 (test template function)  
Reviewer: @ElwizScott

### ✅ Definition of Done
- [ ] Templates created and tested  
- [ ] Default labels work automatically  
- [ ] Reviewed and approved by team

---

GH-04 – Link Repository to Squad Project and Milestone
## GH-04 – Link Repository to Squad Project and Milestone
🏷️ Labels: type:workflow, milestone:1, sprint:1, priority:high

### 🎯 Goal
Link the Job Applicant repository to the squad’s central DevVision Project Board.  
Create Milestone 1 with correct due dates and link all issues to it.

### 📄 Deliverables
- Linked GitHub project under organization  
- Milestone 1 created  
- Progress tracker enabled  

### 👥 Assignees
@ElwizScott (coordination with Job Manager)  
@ctungnguyen (check traceability of backlog links)  
@phat041102 (link setup + test)  

### ✅ Definition of Done
- [ ] Milestone visible under repo issues  
- [ ] Linked to organization-level board  
- [ ] Dates match syllabus

---

GH-05 – Create CI/CD Workflow with GitHub Actions
## GH-05 – Create CI/CD Workflow with GitHub Actions
🏷️ Labels: type:workflow, milestone:2, sprint:3, priority:medium

### 🎯 Goal
*(Planned for Milestone 2)*  
Set up GitHub Actions CI/CD workflow for frontend and backend builds and lint checks.

### 📄 Deliverables
- `.github/workflows/ci.yml`  
- Lint + test jobs  
- Build badge linked to README.md  

### 👥 Assignees
@phat041102 (DevOps lead)  
@xuandat2001 (frontend build test)  
@HoangSon0810 (backend build test)

---

GH-06 – Set Up Environment Variables and Secrets
## GH-06 – Set Up Environment Variables and Secrets
🏷️ Labels: type:workflow, milestone:2, sprint:3, priority:medium

### 🎯 Goal
*(Planned for Milestone 2)*  
Prepare `.env.example` and configure GitHub Secrets for sensitive credentials.

### 📄 Deliverables
- `.env.example`  
- Repo secrets stored securely  
- `/docs/dev-setup/env-guide.md`

### 👥 Assignees
@phat041102, @ctungnguyen  
Reviewer: @ElwizScott

---

GH-07 – Configure README and Badge Layout
## GH-07 – Configure README and Badge Layout
🏷️ Labels: type:workflow, documentation, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Design a clear and informative README for the Job Applicant repository.  
Add project summary, team members, structure overview, and badges for CI/CD and milestones.

### 📄 Deliverables
- `/README.md` with project overview  
- Team role table  
- Setup instructions and build badge  

### 👥 Assignees
@ElwizScott (content owner)  
@ctungnguyen (proofreading + data model summary)  
@phat041102 (badge setup)  
Reviewer: @xuandat2001  

### ✅ Definition of Done
- [ ] README complete and formatted  
- [ ] Badges functional  
- [ ] Shown correctly in organization home

---

GH-08 – Enable Branch Protection Rules and PR Reviews
## GH-08 – Enable Branch Protection Rules and PR Reviews
🏷️ Labels: type:workflow, milestone:2, sprint:3

### 🎯 Goal
*(Planned for Milestone 2)*  
Protect `main` branch to require at least one reviewer and CI pass before merge.

### 📄 Deliverables
- Branch protection rules  
- `/CODEOWNERS` file  
- `/docs/dev-setup/review-policy.md`

### 👥 Assignees
@phat041102, @ctungnguyen

---

GH-09 – Automation for Milestone Labels and Issue Templates
## GH-09 – Automation for Milestone Labels and Issue Templates
🏷️ Labels: type:workflow, milestone:2, sprint:3

### 🎯 Goal
*(Planned for Milestone 2)*  
Automate sprint and milestone label assignment using GitHub Action “Labeler”.

### 📄 Deliverables
- `.github/labeler.yml`  
- `/docs/dev-setup/automation.md`

### 👥 Assignees
@phat041102, @ctungnguyen  
Reviewer: @ElwizScott

---

GH-10 – Team Contribution Log (GitHub Insights Setup)
## GH-10 – Team Contribution Log (GitHub Insights Setup)
🏷️ Labels: documentation, milestone:1, sprint:2, priority:high

### 🎯 Goal
Collect evidence of contribution from GitHub Insights for marking and transparency.  
Summarize commits, issues, and reviews by member and include them in the report appendix.

### 📄 Deliverables
- `/docs/report/contribution-summary.md`  
- Screenshots from GitHub Insights  
- Markdown table: Member → Commits → Issues → Reviews  

### 👥 Assignees
@ElwizScott (summary)  
@ctungnguyen (data model contribution tracking)  
@HoangSon0810 (architecture section)  
@xuandat2001 (frontend work logs)  
@phat041102 (DevOps stats)

### ✅ Definition of Done
- [ ] Screenshots attached  
- [ ] Summary table complete  
- [ ] Linked in RP-09 report appendix

---

# EPIC 5 – Presentation Preparation

PR-01 – Define Presentation Outline and Role Allocation
## PR-01 – Define Presentation Outline and Role Allocation
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:high

### 🎯 Goal
Plan the structure and speaking order for the 10–12 minute presentation.  
Ensure all five members have equal speaking time and clear topics aligned with their roles in the project.  
Include introduction, data model, architecture, justification, and closing sections.

### 📄 Deliverables
- `/presentation/outline.md` (topics, timing, speakers)  
- Role assignment table  
- Estimated slide count per section  

### 🧰 Tools / Resources
- RMIT milestone presentation brief  
- Squad role table in README.md  

### 👥 Assignees
@All members (joint planning)  
Facilitator: @ElwizScott  

### ✅ Definition of Done
- [ ] Outline approved by all members  
- [ ] Role distribution finalized  
- [ ] Ready for slide creation

---

PR-02 – Create Slide Deck (Google Slides / PowerPoint)
## PR-02 – Create Slide Deck (Google Slides / PowerPoint)
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:high

### 🎯 Goal
Design a cohesive slide deck covering all sections of the Milestone 1 presentation.  
Maintain consistent visuals, concise bullet points, and diagram integration from Visual Paradigm and report figures.

### 📄 Deliverables
- `/presentation/DEVision_JobApplicant_M1.pptx` or Google Slides link  
- Diagram exports (ERD, C4, Deployment)  
- Shared editing access for all members  

### 👥 Assignees
@ElwizScott (intro + closing slides, layout design)  
@ctungnguyen (data model section slides)  
@HoangSon0810 (architecture diagrams slides)  
@xuandat2001 (system design workflow slides)  
@phat041102 (DevOps/integration visuals)

### ✅ Definition of Done
- [ ] All sections complete  
- [ ] Visuals readable and consistent  
- [ ] Uploaded to `/presentation/`

---

PR-03 – Write Speaker Notes and Talking Points
## PR-03 – Write Speaker Notes and Talking Points
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Prepare clear talking points for each section to ensure smooth delivery and consistent technical depth.  
Each speaker writes and rehearses their 2–3 minute part.

### 📄 Deliverables
- `/presentation/speaker-notes.md` with section-by-section notes  
- Estimated speaking time per person  
- Key transition sentences between speakers  

### 👥 Assignees
@ElwizScott (intro + transitions)  
@ctungnguyen (data model details)  
@HoangSon0810 (architecture flow)  
@xuandat2001 (frontend workflow)  
@phat041102 (deployment & DevOps summary)

### ✅ Definition of Done
- [ ] Notes written in Markdown  
- [ ] Reviewed by at least one peer  
- [ ] Finalized before rehearsal

---

PR-04 – Integrate Visuals and Animations
## PR-04 – Integrate Visuals and Animations
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Insert diagrams, animations, and transitions to make the presentation dynamic and clear.  
Ensure visuals highlight system flow between frontend ↔ backend ↔ database.  
Animations should enhance comprehension, not distract.

### 📄 Deliverables
- Updated slide deck with transitions  
- Animated architecture flow arrows  
- Highlighted data relationships  

### 👥 Assignees
@HoangSon0810 (architecture diagrams)  
@ctungnguyen (data model visuals)  
@xuandat2001 (frontend + navigation flow)  
@phat041102 (DevOps pipeline visual)  
Reviewer: @ElwizScott  

### ✅ Definition of Done
- [ ] Animations smooth and relevant  
- [ ] Visuals in correct resolution  
- [ ] Consistent color scheme across slides

---

PR-05 – Record and Review Dry Run (Internal Rehearsal)
## PR-05 – Record and Review Dry Run (Internal Rehearsal)
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:high

### 🎯 Goal
Conduct a full internal rehearsal of the presentation using Google Meet or MS Teams.  
Record the session for self-review to identify pacing and transitions issues.  

### 📄 Deliverables
- Recorded rehearsal link or file (`/presentation/rehearsal-recording.mp4`)  
- Feedback summary (`/presentation/rehearsal-notes.md`)  
- Revised script timing table  

### 👥 Assignees
@All members  
Recording managed by @phat041102  
Feedback summary by @ElwizScott  

### ✅ Definition of Done
- [ ] Full rehearsal completed  
- [ ] Feedback logged  
- [ ] Adjustments made before final presentation

---

PR-06 – Prepare Backup Slides and Demo Screenshots
## PR-06 – Prepare Backup Slides and Demo Screenshots
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Create backup slides and screenshots for potential technical issues.  
Include key diagrams and static visuals of microservice communications and ERD, in case of display failures.

### 📄 Deliverables
- `/presentation/backup-slides.pptx`  
- Folder `/presentation/screenshots/` with labeled PNGs  
- Slide for “Q&A” fallback section  

### 👥 Assignees
@HoangSon0810 (architecture backup)  
@ctungnguyen (data model visuals)  
@xuandat2001 (frontend UI flow)  
@phat041102 (DevOps diagram)  
Reviewer: @ElwizScott

### ✅ Definition of Done
- [ ] Backup materials ready  
- [ ] Images high resolution  
- [ ] Shared in Drive or repo

---

PR-07 – Final Presentation Rehearsal and Submission
## PR-07 – Final Presentation Rehearsal and Submission
🏷️ Labels: type:presentation, milestone:1, sprint:2, priority:high

### 🎯 Goal
Perform the final full-length rehearsal to confirm pacing (10–12 mins total), order, and clarity.  
Make minor timing or wording adjustments before official presentation.  

### 📄 Deliverables
- `/presentation/final-deck.pptx`  
- Final timing log (`/presentation/final-timing.md`)  
- Uploaded version link shared to Canvas  

### 👥 Assignees
@ElwizScott (host + timing coordination)  
@ctungnguyen (data segment)  
@HoangSon0810 (architecture)  
@xuandat2001 (frontend + integration)  
@phat041102 (infrastructure + closing)

### ✅ Definition of Done
- [ ] Final presentation rehearsed and timed  
- [ ] All members comfortable with order  
- [ ] Deck and recording uploaded for submission

---

# EPIC 6 – Cross-Team Integration (Design-Level for Milestone 1)

CT-01 – Define Cross-Subsystem Communication Overview
## CT-01 – Define Cross-Subsystem Communication Overview
🏷️ Labels: type:integration, milestone:1, sprint:2, priority:high

### 🎯 Goal
Document how the **Job Applicant (JA)** and **Job Manager (JM)** subsystems communicate at the architectural level.  
Summarize shared business events, REST API boundaries, and message types exchanged through Kafka.  
Provide a clear overview for both teams to align integration expectations.

### 📄 Deliverables
- `/integration/overview.md`  
- Diagram showing JA↔JM communication (REST + Kafka)  
- List of integration entities and message topics

### 👥 Assignees
@ElwizScott, @ctungnguyen (shared entities, conceptual design)  
@HoangSon0810, @xuandat2001, @phat041102 (message flow diagram)

### ✅ Definition of Done
- [ ] Overview diagram approved by both subteams  
- [ ] Topic names agreed and documented  
- [ ] Stored under `/integration/overview.md`

---

CT-02 – Identify Shared Data Entities and Contracts
## CT-02 – Identify Shared Data Entities and Contracts
🏷️ Labels: type:integration, milestone:1, sprint:2, priority:high

### 🎯 Goal
List all data entities shared between JA and JM (e.g., JobPost, CompanyProfile, ApplicationStatus).  
Define the minimal fields that must remain consistent across subsystems and version control rules for contracts.

### 📄 Deliverables
- `/integration/shared-entities.md`  
- Table mapping Entity → Source → Consumer → Sync Method  
- Reference to ERD relationships from both subteams

### 👥 Assignees
@ElwizScott, @ctungnguyen (data definition)  
Reviewer: @HoangSon0810

### ✅ Definition of Done
- [ ] All shared entities listed  
- [ ] Versioning method agreed  
- [ ] Referenced in final report justification

---

CT-03 – Design API Contracts and Endpoints
## CT-03 – Design API Contracts and Endpoints
🏷️ Labels: type:integration, milestone:1, sprint:2, priority:high

### 🎯 Goal
Define REST API endpoints that the Job Manager exposes and Job Applicant consumes.  
Specify request/response payloads, authentication method, and error format.  
Include OpenAPI-style mock examples.

### 📄 Deliverables
- `/integration/api-contracts.yaml` (OpenAPI schema)  
- `/integration/api-summary.md` (endpoint list + description)  
- Sample request/response JSONs

### 👥 Assignees
@ctungnguyen, @ElwizScott (API payload + spec)  
@HoangSon0810, @xuandat2001 (review from architecture perspective)

### ✅ Definition of Done
- [ ] Endpoints defined and verified  
- [ ] Shared with JM team for confirmation  
- [ ] Linked in report (Integration Section)

---

CT-04 – Draft Kafka Topic Schema and Event Flow
## CT-04 – Draft Kafka Topic Schema and Event Flow
🏷️ Labels: type:integration, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Design message schemas and topic names for event-based communication between JA and JM.  
Specify producer and consumer responsibilities for events like `job-post-created`, `application-submitted`, and `status-updated`.

### 📄 Deliverables
- `/integration/kafka-topics.md`  
- Sequence diagram showing publish/consume flow  
- Example JSON schema for each topic

### 👥 Assignees
@phat041102 (lead – topic schema)  
@HoangSon0810, @xuandat2001 (flow diagrams)  
Reviewer: @ElwizScott

### ✅ Definition of Done
- [ ] Topics documented with schema examples  
- [ ] Roles (producer/consumer) clarified  
- [ ] Ready for Milestone 2 implementation

---

CT-05 – Integration Boundary Diagram (Level 3 View)
## CT-05 – Integration Boundary Diagram (Level 3 View)
🏷️ Labels: type:integration, milestone:1, sprint:2, priority:high

### 🎯 Goal
Create a detailed Level-3 C4 diagram showing microservice boundaries and integration touchpoints between JA and JM.  
Highlight service dependencies, API gateway routes, and messaging paths.

### 📄 Deliverables
- `/integration/diagrams/C4_Level3_Integration.vpd`  
- Exported PNG for report inclusion  
- `/integration/diagrams/readme.md` describing interactions

### 👥 Assignees
@HoangSon0810 (architecture lead)  
@xuandat2001 (frontend ↔ backend integration flow)  
@phat041102 (Kafka ↔ network routes)  
Reviewer: @ElwizScott, @ctungnguyen

### ✅ Definition of Done
- [ ] Diagram approved by both subteams  
- [ ] Added to final report diagrams folder  
- [ ] Matches system architecture justification

---

CT-06 – Integration Consistency Checklist and Review
## CT-06 – Integration Consistency Checklist and Review
🏷️ Labels: type:integration, documentation, milestone:1, sprint:2, priority:medium

### 🎯 Goal
Review all integration design documents for consistency and completeness.  
Ensure all shared contracts, topics, and endpoints are aligned with JM’s definitions.

### 📄 Deliverables
- `/integration/review-checklist.md`  
- Review log documenting feedback from JM team  
- Updated overview or contract files after corrections

### 👥 Assignees
@All members  
Lead reviewer: @ElwizScott  
Cross-team liaison: @HoangSon0810  

### ✅ Definition of Done
- [ ] Checklist reviewed jointly with JM team  
- [ ] Feedback implemented  
- [ ] Approval recorded in `/integration/review-checklist.md`

 
