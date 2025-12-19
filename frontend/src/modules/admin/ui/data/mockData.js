// src/data/mockData.js

export const applicants = [
    { id: 1, name: "Adam Trantow", email: "adam@mail.com", phone: "0123 456 789", verified: true, status: "Active" },
    { id: 2, name: "Angel Rolfson-Kulas", email: "angel@mail.com", phone: "0987 654 321", verified: true, status: "Active" },
    { id: 3, name: "Betty Hammes", email: "betty@mail.com", phone: "0111 222 333", verified: true, status: "Active" },
    { id: 4, name: "Billy Braun", email: "billy@mail.com", phone: "0444 555 666", verified: false, status: "Banned" },
    { id: 5, name: "Billy Stoltenberg", email: "stoltenberg@mail.com", phone: "0777 888 999", verified: true, status: "Banned" },
];

export const companies = [
    { id: 1, companyName: "Mohr, Langworth and Hills", email: "contact@mohr.com", industry: "Software", status: "Active" },
    { id: 2, companyName: "Koch and Sons", email: "hr@koch.com", industry: "Manufacturing", status: "Active" },
    { id: 3, companyName: "Waelchi - VonRueden", email: "jobs@waelchi.com", industry: "Finance", status: "Inactive" },
    { id: 4, companyName: "White, Cassin and Goldner", email: "hello@whitecassin.com", industry: "Retail", status: "Active" },
];

export const applications = [
    { id: 101, applicantName: "Adam Trantow", jobTitle: "UI Designer", submittedAt: "2025-01-05", status: "Pending" },
    { id: 102, applicantName: "Angel Rolfson-Kulas", jobTitle: "Frontend Dev", submittedAt: "2025-01-07", status: "Reviewed" },
    { id: 103, applicantName: "Betty Hammes", jobTitle: "QA Engineer", submittedAt: "2025-01-08", status: "Rejected" },
];

export const jobPosts = [
    { id: 201, title: "UI Designer", company: "Mohr, Langworth and Hills", location: "Remote", status: "Open" },
    { id: 202, title: "Frontend Developer", company: "Koch and Sons", location: "Berlin", status: "Open" },
    { id: 203, title: "Data Analyst", company: "Waelchi - VonRueden", location: "London", status: "Closed" },
];
