# Job Applicant
A subsystem of DEVision focused on empowering Computer Science job seekers. The Job Applicant module enables registration, secure login, job search, and applications, with premium real-time alerts for new jobs matching users’ skills, salary range, and career goals.


### **MongoDB Import Instructions**

Create a **README.md** with instructions on how to load this seed data into MongoDB locally:

---

```markdown
# How to Load Sample Seed Data into MongoDB

## Prerequisites:
1. MongoDB installed locally. [Download MongoDB](https://www.mongodb.com/try/download/community)
2. A MongoDB database running locally (`mongodb://localhost:27017`).

## Steps to Import Seed Data:
1. Clone this repository (if not done already).
2. Ensure MongoDB is running on your machine:
   - Open a terminal and run:
     ```bash
     mongod
     ```

3. Open a new terminal window and navigate to your project folder where `sample-seed.json` is located.

4. Import the JSON data into MongoDB using the following command:
   ```bash
   mongoimport --db devvision --collection applicants --file ./sample-seed.json --jsonArray

```

- **`devvision`** is the database name.
- **`applicants`** is the collection you are inserting data into.
- The `-jsonArray` flag ensures MongoDB reads the file as an array of documents.

## Verifying Data:

To verify that the data has been successfully imported, run the following command in your terminal:

```bash
mongo
use devvision
db.applicants.find().pretty()

```

This will display all the records from the `applicants` collection.

## Payment Consumer (Kafka)

- The subscription service includes a Kafka consumer for payment events from the Job Manager Payment API.
- It is disabled by default and can be toggled via environment variables.

### Topics (dev)
- Success: `payment-success`
- Failed: `payment-failed`
- Initiated: `payment-initiated` (not consumed by default)

### Bootstrap servers (dev)
- Outside Docker: `localhost:29092`
- Inside Docker: `kafka:9092`

### Enable the consumer in Docker Compose
Add the following environment variables to the `subscription` service (for example in an `.env.local` or directly under the service):

```yaml
services:
   subscription:
      environment:
         - PAYMENT_CONSUMER_ENABLED=true
         - KAFKA_PAYMENT_SUCCESS_TOPIC=payment-success
         - KAFKA_PAYMENT_FAILED_TOPIC=payment-failed
```

Then rebuild only the subscription service:

```bash
docker compose up -d --build subscription
```

### Behavior
- On `payment-success` for `subsystem=JOB_APPLICANT` and `paymentType=SUBSCRIPTION`:
   - Records the payment transaction with `status=SUCCESS` and UTC timestamp
   - Activates a 30-day PREMIUM subscription (deactivates any previous active one)
- On `payment-failed` for `subsystem=JOB_APPLICANT` and `paymentType=SUBSCRIPTION`:
   - Records the payment transaction with `status=FAILED` and UTC timestamp
   - Does not modify subscriptions

Note: We create the transaction with email at initiation via the REST `POST /payments/initiate`, so email is captured even though the event does not include it.