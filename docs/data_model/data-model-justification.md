## **DM-09: Data Model Justification**

The Data Model is a core component of the Job Applicant Subsystem and has been designed to support the modular and distributed nature of our microservices architecture. Below, we provide justifications for how the data model supports the maintainability, extensibility, resilience, scalability, security, and performance of the system.

### 6.1.1. Maintainability

The data model follows a modular design, with each microservice managing its own domain and associated data. For example, the Applicant Service owns the Applicant data, while the Application Service handles job applications. This clear ownership allows each service to be independently maintained without risk of breaking other parts of the system. Each service has its own database schema (MongoDB (with GridFS for media storage)) and interacts with others through well-defined APIs and Kafka events.

**Advantages**

- Clear service ownership of data, meaning that only relevant microservices need to know about and update certain data entities.
- Isolated changes: New features or bug fixes in one service’s data model don’t require changes in other services. For instance, adding a new field to the Application entity doesn’t affect the Payment Service or Notification Service.
- Improved testing: Each service can be tested independently, as its own data and database, making unit tests more straightforward.

**Disadvantages:**

- Cross-service debugging and tracking changes across multiple services can become more challenging as the number of services grows.

### 6.1.2. Extensibility

The data model has been designed with future-proofing in mind. Each service is decoupled from the others, and new services or features can be added with minimal disruption to existing ones. For instance, adding a new Search Profile for applicants or a Job Posting feature can be done by adding new services, with minimal changes to existing services.

**Advantages**

- Independent scaling: As the system grows, new features such as advanced search for applicants or job matching can be added by simply introducing new microservices and expanding the data model to support them.
- Modular architecture: New fields or relationships can be added to existing data models without affecting the functionality of other services. For example, a new Rating system for applicants can be added in the Applicant Service without requiring changes to other services.
- Loose coupling: Each service communicates via APIs and events, which reduces the complexity of adding new features.

**Disadvantages**

- Introducing new services requires careful coordination to ensure API contracts and data consistency are maintained across all services.
- API versioning can become complex if backward compatibility is not carefully handled.

### 6.1.3. Resilience

The data model supports resilience by utilizing event-driven communication and asynchronous processing via Kafka. This allows services to be fault-tolerant and handle failures gracefully. If a service like Authentication Service fails, the Applicant Service can continue operating, as it will receive Kafka events indicating state changes in a fault-tolerant way. Similarly, data consistency is ensured by ensuring eventual consistency with Kafka and Redis caches for high-priority data.

**Advantages**

- Eventual consistency: Kafka allows services to be resilient to temporary failures by replaying missed events. This ensures that eventual consistency is maintained between services.
- Fault isolation: Each service manages its own data, meaning that failures in one service (the Profile Service) do not affect others (the Notification Service).
- Redundancy: The system can retry operations on failure and leverage eventual consistency through Kafka, ensuring that updates are processed even if one service temporarily fails.

**Disadvantages**

Eventual consistency might introduce slight delays in the propagation of updates between services (a profile update might not immediately appear across all services). This can be managed with event retry mechanisms and circuit breakers to ensure better reliability.

### 6.1.4. Scalability

The data model is optimized for scalability by employing sharding and independent scaling of each microservice. For example, the Applicant Service can be scaled independently to handle high loads of resume uploads, while other services such as the Payment Service can remain unaffected. Sharding by country ensures that data is distributed effectively across different database clusters, reducing load and latency.

**Advantages**

- Horizontal scaling of services means that individual microservices can scale based on demand. For example, the Notification Service can handle more notifications without affecting the Application Service.
- Sharding improves performance by limiting the amount of data each service needs to interact with. For example, applicants from different regions (countries) are placed in separate database shards, ensuring fast and scalable access.
- Event-driven scaling: Kafka allows you to scale the event consumers dynamically, adding more event listeners as the system grows.

**Disadvantages**

- Sharding introduces complexity in managing and coordinating data across different database clusters.
- Cross-shard operations can be complex to handle, especially in global queries (querying all applicants across countries).

### 6.1.5. Security

The data model is secured through role-based access control (RBAC) implemented in the Authorization Service. Sensitive fields such as passwordHash or ssoId are never exposed to external systems or the frontend, ensuring that data breaches are minimized. Additionally, API Gateway and JWT tokens are used for secure communication and authorization across services.

**Advantages**

- Fine-grained access control ensures that only authorized users or services can access specific data. For example, an Admin can access all applicant data, while an Applicant can only view their own profile.
- Sensitive data protection is ensured by not exposing sensitive attributes like passwordHash or ssoId in API responses or external DTOs.
- JWT tokens are used to securely authenticate and authorize users across services, ensuring that only trusted users can access protected resources.

**Disadvantages**

- Managing JWT expiration and revocation can become complex as the system scales.
- Distributed security management across microservices requires additional infrastructure and careful handling of tokens and keys.

### 6.1.6. Performance

Caching and event-driven architecture significantly improve performance by reducing redundant operations. Redis is used to cache frequently accessed data, such as applicant profiles, and Kafka ensures low-latency messaging between services. The system is designed to minimize blocking operations, and service-specific databases ensure that each service is optimized for its data needs.

**Advantages**

- Caching with Redis significantly reduces latency, especially for frequent read operations such as profile lookups.
- Kafka allows asynchronous processing, meaning services can work in parallel without waiting on each other, improving system throughput.
- Service isolation enables independent optimization of each service, so slow services like Search Profiles do not impact core services like Applicant Service.

**Disadvantages**

- Cold starts in services or cache misses in Redis can result in a temporary performance lag.
- Event-driven systems can introduce latency if not properly optimized (especially with large event queues).