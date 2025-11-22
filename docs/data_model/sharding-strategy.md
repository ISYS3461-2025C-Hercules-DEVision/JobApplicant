# **DM-06 – Sharding Strategy & Indexing Plan**

**Sharding Strategy & Indexing Plan for Job Applicant Subsystem**

---

## **Sharding Strategy:**

- **Shard Key**:
    
    As per your data model, the **`country`** field is selected as the shard key, which ensures all data related to a specific country is stored together. This optimizes read/write performance for region-specific queries, particularly those related to applicants, their profiles, and their job search activities.
    
    The **country** field is mandatory in each relevant entity (Applicant, Education, WorkExperience, Resume, etc.) and is indexed for efficient lookup across shards.
    
- **Implications of Sharding**:
    - **Cross-shard queries** will require special attention since MongoDB does not support joins across shards. To mitigate this, design queries that minimize the need for cross-shard access.
    - **Sharded entities** include `Applicant`, `Education`, `WorkExperience`, `ApplicantSkill`, `Resume`, and `SearchProfile`. These entities must be stored in the same shard to maintain consistency, ensuring that related data can be retrieved together.
    - **Non-sharded entities** like **Authentication Service** and **Authorization Service** will not be sharded. These services rely on different sharded systems and may need to access data across shards (via API or service-to-service calls).
- **Shard Migration**:
    - If a user's **country** changes, the entire user's profile, along with related entities (Education, WorkExperience, ApplicantSkill), must be migrated to the new shard.
    - This migration will be handled by an event-driven architecture, using Kafka to notify systems when migration occurs (`ApplicantShardMoved`).

---

## **Index Planning Strategy:**

- **Indexes on Core Entities**:
    - **Applicant**:
        
        Indexing on `country` (shard key), `email` (unique), and `createdAt` for sorting and search optimization.
        
    - **Education and WorkExperience**:
        
        Index on `applicantId` to optimize lookups for an applicant's education and job history.
        
    - **SkillTag**:
        
        Unique index on `name` (case-insensitive) to avoid duplicate skills across applicants.
        
    - **ApplicantSkill**:
        
        Composite indexes on both `applicantId` and `skillId` to optimize search operations related to skill proficiencies.
        
    - **SearchProfile**:
        
        Index on `applicantId` and `desiredCountry` to optimize queries based on user preferences and job search settings.
        
- **Full-Text Search (FTS)**:
    - Implement full-text indexes on the **Resume**, **WorkExperience**, and **MediaPortfolio** fields (`objective`, `jobTitle`, `description`) to support free-text searches.
- **Search Queries**:
    - **Job Search**: A typical search query might look for jobs filtered by `location`, `salary range`, and `skill tags`. The query will target a single shard based on the applicant's `country`.
    - Example query (pseudo-SQL):
        
        ```sql
        SELECT jobTitle, location, salary, companyName FROM jobPosts
        WHERE location = 'VN' AND salary >= 50000 AND skillTags IN ('React', 'JavaScript');
        ```
        
    - **Cross-shard Queries**: Ensure minimal reliance on cross-shard queries. For instance, when querying applicants by skills, restrict searches to applicants within the same shard using their `country`.

---

## **Key Considerations:**

- **Performance Trade-offs**:
    - While sharding by `country` provides efficiency for localized searches (job postings in specific countries), it may require handling **complex migrations** if users move between regions. Performance optimizations for sharded queries, especially during migration, must be factored into system design.
    - Cross-shard aggregation or analysis (querying all applicants across countries) could incur additional latency as these queries would have to aggregate results from multiple shards.
- **Monitoring & Maintenance**:
    - Continuous monitoring of shard distribution and indexing performance is critical. Tools like MongoDB’s Atlas can provide insights into shard usage, query performance, and migration bottlenecks.

---

## **Deliverables**:

1. **Sharding Document**:
    - Commit the `sharding-and-indexing.md` document, including the shard key strategy, index recommendations, and migration procedures.
2. **Index List**:
    - List all indexes for frequent queries (full-text search, tag lookups) and their corresponding entity attributes.
3. **Example Queries**:
    - Provide example queries showing planned routing and indexing strategies using pseudo-SQL or NoSQL notation to demonstrate the efficiency of the sharding and indexing approach.