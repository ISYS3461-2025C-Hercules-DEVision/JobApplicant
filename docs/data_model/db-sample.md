## **DM-08: Sample DB Seed Script & Example Data**

### **1. Overview of the Goal**

- You need a **seed script** that will insert **sample data** into your MongoDB.
- The **data** should include:
    1. **1 premium applicant**
    2. **Multiple skills** for that applicant
    3. **Sample applications** submitted by the applicant

### **2. Sample Data (Overview)**

Here’s a **quick rundown** of what you need:

1. **Applicant Data**:
    - Name, email, country, etc.
2. **Skills Data**:
    - Multiple skills like “React”, “Java”, etc.
3. **Application Data**:
    - The applicant applies to different job posts.

---

### **3. Seed Script in JSON Format**

Below is an example of the **`sample-seed.json`** that you can use to seed your local MongoDB.

```json
{
  "applicant": {
    "applicantId": "12345",
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "country": "Vietnam",
    "city": "Hanoi",
    "streetAddress": "123 Main St.",
    "phoneNumber": "+84912345678",
    "profileImageUrl": "https://someurl.com/profile.jpg",
    "isActivated": true,
    "createdAt": "2025-11-20T12:00:00Z",
    "updatedAt": "2025-11-21T08:12:00Z",
    "subscription": {
      "planType": "Premium",
      "isActive": true,
      "startDate": "2025-11-01T00:00:00Z",
      "expiryDate": "2025-12-01T00:00:00Z"
    }
  },
  "skills": [
    {
      "skillId": "1",
      "skillName": "React"
    },
    {
      "skillId": "2",
      "skillName": "Node.js"
    },
    {
      "skillId": "3",
      "skillName": "JavaScript"
    }
  ],
  "applications": [
    {
      "applicationId": "app-123",
      "jobPostId": "job-001",
      "companyId": "company-001",
      "status": "Pending",
      "coverLetter": "This is a cover letter for the job.",
      "resumeFileId": "resume-123",
      "submittedAt": "2025-11-21T09:00:00Z",
      "updatedAt": "2025-11-21T09:05:00Z"
    },
    {
      "applicationId": "app-124",
      "jobPostId": "job-002",
      "companyId": "company-002",
      "status": "Accepted",
      "coverLetter": "This is another cover letter for a different job.",
      "resumeFileId": "resume-124",
      "submittedAt": "2025-11-22T10:00:00Z",
      "updatedAt": "2025-11-22T10:05:00Z"
    }
  ]
}

```

### **Explanation**:

- **Applicant**:
    - Represents a **premium applicant** with personal data (e.g., name, email, address).
    - **Subscription**: This data has a `Premium` subscription, and fields include subscription start/end dates.
- **Skills**:
    - Multiple skills assigned to the applicant.
    - Each skill is stored as an array with unique `skillId`.
- **Applications**:
    - Sample applications to two job posts.
    - Each application contains: application ID, job post ID, company ID, cover letter, resume file, and timestamps.

---

### **4. Example Queries**

Here are a few example queries you can use to test the imported data in MongoDB:

1. **Find all applicants**:
    
    ```jsx
    db.applicants.find().pretty();
    
    ```
    
2. **Find applicant by email**:
    
    ```jsx
    db.applicants.find({ "email": "john.doe@example.com" }).pretty();
    
    ```
    
3. **Get all skills of an applicant**:
    
    ```jsx
    db.skills.find({ "applicantId": "12345" }).pretty();
    
    ```
    
4. **Find all applications for a specific job**:
    ```jsx
    db.applications.find({ "jobPostId": "job-001" }).pretty();

    ```