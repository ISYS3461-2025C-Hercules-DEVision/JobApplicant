# Job Applicant Functional Requirements.md

## 1/ Job Applicant Registration 

### 1.1.1 
The system shall provide a registration form requiring Email, Password, and
Country as mandatory fields. Phone number, Street name/number, and City are
optional fields.

### 1.1.2 
The system shall enforce a unique email constraint, ensuring no two Job
Applicants share the same email address in the database.

### 1.1.3 
The system shall send an email to the new user with information on how to
activate the account. A User can only login after they activate their account.

### 1.2.4 
The Country input field must be a selectable option (e.g., a dropdown list)
instead of a direct text input field.

### 1.2.1
The system shall validate password strength against the following criteria: 
a) At least 8 characters. 
b) At least 1 number. 
c) At least 1 special character (e.g.,$#@!). 
d) At least 1 capitalized letter.

The system shall validate the email syntax to ensure it meets standard
formatting requirements, including:  
a) Contains exactly one '@' symbol.  
b) Contains at least one '.' (dot) after the '@' symbol.  
c) The total length of the email address must be less than 255 characters.  
d) No spaces or prohibited characters (e.g., ( ) [ ] ; :).

### 1.2.3
The system shall validate the Phone number (if provided) to ensure it:  
a) Contains only digits and must start with a valid international dial code (e.g., +84, +49).  
b) The length of the digits following the dial code must be less than 13.

### 1.2.4
All input validation specified above must be performed on both the frontend for
quick user feedback and the backend for security. Any validation error must be
clearly displayed to the end-user.

### 1.3.1 
The system shall support registration via SSO (Single Sign-On) from ONE
selected external platform: Google, Microsoft, Facebook, or GitHub.

### 1.3.2 
Upon successful SSO registration, the system shall persist the core user profile
(name, email, country) in the server. The system must be configured so the user
that logs in using the SSO interface cannot use a password for direct Job
Applicant system access.

### 1.3.3
The system shall select a User’s attribute as the sharding key to partition and
store the user data in the designated database shard. The chosen shard key must
enhance the search algorithm of the Job Applicant and/or Job Manager systems


## 2/ Job Applicant Login

### 2.1.1
The system shall authenticate the user using the submitted email and
password. If the system does not use HTTPS, the frontend shall send
credentials using the Basic Authentication format. If HTTPS is applied, the
email and password can be sent in plaintext within the request body.

### 2.1.2
Upon successful login, the backend shall generate a JSON Web Signature
(JWS) token. This token must contain signed user identity data (e.g., user ID,
Role) to verify its integrity.

### 2.2.1
Upon successful login, the backend shall generate a JSON Web Encryption
(JWE) token instead of a JWS. This token must contain user identity
information (e.g., user ID, Role) and ensure that the payload is encrypted and
cannot be read by unauthorized parties.

### 2.2.2
The system shall implement a mechanism to prevent brute-force attacks on the
login endpoint, blocking the authentication for that account after 5 failed login
attempts within 60 seconds.

### 2.2.3 
The system shall invalidate and revoke the user's JWE token when the user
explicitly logs out or after the defined expiration time.

### 2.3.1
The system shall support logging in using an account from the single selected
external platform defined in requirement 1.3.1 (Google, Microsoft, Facebook,
or GitHub).

### 2.3.2
The system shall use a dedicated Redis cache instance to store and quickly
check the revocation status of the JWE tokens for non-SSO accounts,
improving security performance.

### 2.3.3
The backend shall implement a token refreshing mechanism for non-SSO
accounts. The system issues a short-lived Access Token and a longer-lived
Refresh Token to maintain user session without requiring frequent reauthentication.


## 3/ Profile Management

### 3.1.1 
Job Applicants shall be able to edit their core contact information: Email,
Password, Phone Number, Address, City, and Country.

### 3.1.2
The system must allow the Job Applicant to create and save a basic text-only
profile, including Education (see 3.1.3), Work Experience (See 3.1.4), and
a brief Objective Summary.

### 3.1.3
The system shall allow the user to manage their Education history as a list of
Degree, Academic Institution (e.g., 'Bachelor of Software Engineering
(Hons) at RMIT'), Duration (from year to year, or from year to current) and
optionally GPA.  
The Degree and Institution can be inputted in plaintext. GPA is from 0 to 100.

### 3.1.4
The system shall allow applicants to declare their List of Work Experience.
For each previous job, the applicant shall enter the Job Title, Duration (from
mm-yyyy to mm-yyyy, or mm-yyyy to now), and Job Description

### 3.2.1
The Job Applicant shall be able to upload an Avatar (profile picture). The
system must automatically resize the image to a defined standard size for
optimal display performance.

### 3.2.2 
The system shall allow the user to tag and manage a list of Technical Skills
and Competencies (e.g., adding tags like "Python", "Kafka", "SQL").

### 3.2.3 
The Job Applicant shall be able to upload images and videos to showcase
their skills or portfolio activities.

### 3.2.4
The system shall display the current and past job applications of the
Applicant. The persistence of those applications must be done by the Job
Applicant team.

### 3.3.1
Profile updates must be propagated to a Kafka topic when the user changes
skills or country. This enables a subscribed Job Manager system to be
notified instantly if the applicant's technical and education background match
their headhunt criteria.

### 3.3.2
Any profile modification must be persisted immediately to the appropriate
database shard. Specifically, if the Job Applicant changes the Country field,
the application logic must perform a data migration of the entire user record
to the new, corresponding database shard.


## 4/ Job Search and Application

### 4.1.1
The Job Applicant shall be able to search for job posts using four criteria: Job
Title, Employment Type (Full-time, Part-time, Internship, Contract), Location
(City or Country), and Salary (minimum or range). The system shall allow the
user to select multiple Employment Types in a single search request

### 4.1.2 
The Job Title search must be case-insensitive (e.g., "Software Engineer" and
"software engineer" yield the same results).

### 4.1.3 
In any single search request, the user can only provide one value for the
Location field (either one City or one Country).

### 4.1.4
The search result shall display the Job Title, Employment Type, Location,
Salary (if applicable), Company Name, Posted Date, and Expiry Date (if
applicable) of each Job Post.  
Clicking on the Job Post shows the above information along with the job
description.

### 4.2.1
The system shall implement a Full-Text Search (FTS) capability on the Job
Post data, allowing searches across the Title, Description, and Required Skills fields.

### 4.2.2 
The system shall allow Job Applicants to filter jobs based on a Fresher status
(a boolean flag indicating if the job is suitable for fresh graduates).

### 4.2.3 
The system shall allow applicants to apply for a job post. An applicant can
optionally include a Cover Letter when applying for a job.

### 4.2.4 
The frontend shall lazy load job posts matching the search result criteria

### 4.2.5 
The page that provides job search and result display shall be responsive to
desktop and mobile devices.

### 4.2.6 
The search results shall display the Skill Tags (e.g., React, Kafka) of each job post item

### 4.3.1
The search query must be optimized to perform retrieval using a database
sharding key (Country), ensuring searches are routed only to the relevant data
shard.  
The system sets the default location of a job search to Vietnam.

### 4.3.2
The system shall enable the Job Applicant to upload files for their
Curriculum Vitae (CV) and Cover Letter when applying for each
application.


## 5/ Premium Applicant Subscription

### 5.1.1
The system shall provide a monthly subscription feature where applicants
can pay a monthly fee of 10 USD using a third-party credit-card
payment system (e.g., Stripe, Paypal). The system must record and
display the premium status on the applicant's Profile page.

### 5.1.2 
The system must record the payment transaction in the system with the
user’s email and transaction time.

### 5.2.1
The system shall allow applicants to define and save a Search Profile
including desired technical background (5.2.2), employment status
(5.2.3), country, salary range (5.2.4), and semicolon-separated job titles
(e.g., "Software Engineer; Backend Developer;").

### 5.2.2 
The system shall record desired technical background as tags (e.g.,
Kafka, React, Spring Boot) for efficient matching.

### 5.2.3
The system shall record desired employment status as multiple selections
from Full-time, Part-time, Fresher, Internship, and Contract. If neither
Full-time nor Part-time is specified, the matching logic must include both
Full-time and Part-time jobs.

### 5.2.4
The system shall record desired salary range from a minimum to a
maximum amount. If the minimum is not set, the minimum is 0. If
maximum salary is not set, there is no upper limit. The search must also
include jobs with undeclared salary.

### 5.3.1
The system shall implement a real-time notification service using the Kafka
messaging platform. A dedicated Kafka consumer service must instantly
evaluate all incoming new job posts against the criteria of all active
subscribers. For every found match, the system must deliver an immediate
real-time notification to the applicants.


## 6/ Admin Panel

### 6.1.1 
The system shall provide a separate, secure Login interface for administrators.

### 6.1.2 
Administrators shall be able to view and deactivate any Job Applicant account
or Company account based on their ID or email.

### 6.2.1 
The Administration Portal must include a search function that allows
administrators to find Job Applicant, Company, and Job Post by name.

### 6.2.2 
Administrators shall be able to view and delete any Job Post from the system.
