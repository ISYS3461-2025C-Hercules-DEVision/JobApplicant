// src/mocks/mockJobs.js

export const mockJobsPage = {
    content: [
        {
            id: "job-001",
            jobPostId: "job-001", // ✅ optional (backend may use this)
            companyId: "company-001", // ✅ REQUIRED for applying
            title: "Backend Engineer (Node.js)",
            company: "TechNova",
            companyName: "TechNova",
            location: "Ho Chi Minh City, VN",
            workMode: "HYBRID",
            employmentType: "FULL_TIME",
            salaryRange: "$1,200 - $2,500 / month",
            experienceLevel: "1-3 years",
            applicantsCount: 15,
            postedAt: "2026-01-05",
            expiresIn: "12 days",
            skills: ["Node.js", "MongoDB", "Kafka", "Microservices"],

            about:
                "TechNova is building scalable microservices powering next-generation analytics. You will work with a global engineering team to improve performance, reliability, and developer experience.",

            responsibilities: [
                "Develop backend services using Node.js and TypeScript",
                "Design scalable REST APIs and event-driven workflows",
                "Optimize MongoDB queries and improve caching strategy",
                "Integrate Kafka streaming and monitor message pipelines",
                "Collaborate with frontend & product teams to define features",
            ],

            requirements: [
                "1+ year experience with Node.js",
                "Strong understanding of REST / JSON / HTTP fundamentals",
                "Familiar with MongoDB or PostgreSQL",
                "Experience with Express / NestJS is a plus",
                "Basic understanding of CI/CD pipelines",
            ],

            benefits: [
                "13th month salary bonus",
                "Hybrid work model with flexible hours",
                "Full insurance + annual health check",
                "Training budget and mentorship program",
                "Company trips & monthly team events",
            ],

            applyUrl: "https://example.com/apply/backend-node",
        },

        {
            id: "job-002",
            jobPostId: "job-002",
            companyId: "company-002",
            title: "Frontend Engineer (React)",
            company: "PixelForge",
            companyName: "PixelForge",
            location: "Da Nang, VN",
            workMode: "ONSITE",
            employmentType: "FULL_TIME",
            salaryRange: "$900 - $1,800 / month",
            experienceLevel: "0-2 years",
            applicantsCount: 21,
            postedAt: "2026-01-02",
            expiresIn: "7 days",
            skills: ["React", "TailwindCSS", "TypeScript", "REST API"],

            about:
                "PixelForge is hiring a frontend engineer to build delightful user experiences for SaaS products. You’ll work closely with designers and backend engineers to ship high-quality UI.",

            responsibilities: [
                "Build UI components with React and TailwindCSS",
                "Implement responsive layouts and design system patterns",
                "Work with REST APIs and manage state effectively",
                "Write clean, reusable and testable components",
                "Improve performance and accessibility across the UI",
            ],

            requirements: [
                "Solid understanding of React fundamentals",
                "Familiar with TypeScript or willing to learn",
                "Basic CSS/HTML skills with responsive design",
                "Nice to have: React Query / Redux Toolkit",
                "Nice to have: unit tests with Jest / Testing Library",
            ],

            benefits: [
                "Competitive salary + performance bonus",
                "Modern office + free snacks",
                "Clear career path & training support",
                "Paid annual leave + team trips",
                "MacBook provided",
            ],

            applyUrl: "https://example.com/apply/react-frontend",
        },

        {
            id: "job-003",
            jobPostId: "job-003",
            companyId: "company-003",
            title: "UI/UX Designer",
            company: "StudioHUB",
            companyName: "StudioHUB",
            location: "Hanoi, VN",
            workMode: "REMOTE",
            employmentType: "FULL_TIME",
            salaryRange: "$800 - $1,600 / month",
            experienceLevel: "1-3 years",
            applicantsCount: 9,
            postedAt: "2025-12-28",
            expiresIn: "5 days",
            skills: ["Figma", "Design Systems", "Wireframes", "Prototyping"],

            about:
                "StudioHUB creates modern digital products for global clients. We need a UI/UX Designer who can craft clean interfaces, prototype workflows, and validate designs with users.",

            responsibilities: [
                "Create wireframes and interactive prototypes in Figma",
                "Design UI screens following brand identity and accessibility",
                "Work closely with developers to ensure design quality",
                "Conduct simple user testing and iterate designs",
                "Build and maintain design system components",
            ],

            requirements: [
                "Experience with Figma and prototyping",
                "Understanding of UX flows and information architecture",
                "Good eye for typography and layout",
                "Ability to explain design decisions clearly",
                "Portfolio required",
            ],

            benefits: [
                "Remote-first team",
                "Flexible schedule",
                "Professional design courses funded",
                "International client exposure",
                "Annual salary review",
            ],

            applyUrl: "https://example.com/apply/uiux",
        },
    ],

    totalPages: 1,
    totalElements: 3,
    number: 0,
    size: 10,
};
