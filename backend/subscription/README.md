# Subscription Service Kafka: Applicant Profile Updates

Publishes an event when a user's job search salary range (min/max) changes.

## Topic

- Name: `applicant-profile-updates`
- Configurable via `kafka.topics.applicant-profile-updates` in `src/main/resources/application.yml`.

## Payload (JSON)

```
{
  "applicantId": "<string>",
  "minSalary": <number|null>,
  "maxSalary": <number|null>,
  "updatedAt": "<ISO-8601 timestamp>",
  "source": "subscription",
  "eventType": "PROFILE_SALARY_UPDATED"
}
```

## Publish Condition

- Emitted by `SearchProfileServiceImpl.upsert()` only when either `minSalary` or `maxSalary` differs from the previously stored values.

## Consumer Notes (Applicant Service)

- Suggested group id: `applicant-service`
- On receive, update `Resume.minSalary` / `Resume.maxSalary` for `applicantId`.
- Accept `null` for `maxSalary` to indicate no upper bound.

## Local Setup

- Ensure Kafka is reachable via `KAFKA_BOOTSTRAP_SERVERS`.
- Default topic name used if the env var `KAFKA_APPLICANT_PROFILE_UPDATES_TOPIC` is not set.
