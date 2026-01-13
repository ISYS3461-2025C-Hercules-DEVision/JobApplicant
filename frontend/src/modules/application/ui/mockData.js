export const mockApplications = [
    {
        applicationId: "app_001",
        jobPostId: "job_1001",
        status: "PENDING",
        createdAt: "2026-01-05T09:20:00.000Z",
        submissionDate: "2026-01-05T09:20:00.000Z",
        appliedDateText: "Jan 5, 2026",
        feedback: "",
        job: {
            title: "Frontend Developer Intern",
            companyName: "PixelCraft Studio",
            location: "Ho Chi Minh City, VN",
        },
        documents: [
            {
                fileId: "cv_001_abcdef12",
                fileType: "CV",
                createdAt: "2026-01-05T09:18:00.000Z",
                fileUrl: "https://example.com/mock/cv_frontend_intern.pdf",
            },
            {
                fileId: "cl_001_12345678",
                fileType: "Cover Letter",
                createdAt: "2026-01-05T09:19:00.000Z",
                fileUrl: "https://example.com/mock/cover_letter_pixelcraft.pdf",
            },
        ],
    },

    {
        applicationId: "app_002",
        jobPostId: "job_1002",
        status: "VIEWED",
        createdAt: "2026-01-02T14:10:00.000Z",
        submissionDate: "2026-01-02T14:10:00.000Z",
        appliedDateText: "Jan 2, 2026",
        feedback: "",
        job: {
            title: "Junior UI Engineer",
            companyName: "NeonWorks",
            location: "Da Nang, VN",
        },
        documents: [
            {
                fileId: "cv_002_zzzz1111",
                fileType: "CV",
                createdAt: "2026-01-02T14:05:00.000Z",
                fileUrl: "https://example.com/mock/cv_ui_engineer.pdf",
            },
        ],
    },

    {
        applicationId: "app_003",
        jobPostId: "job_1003",
        status: "ACCEPTED",
        createdAt: "2025-12-20T08:00:00.000Z",
        submissionDate: "2025-12-20T08:00:00.000Z",
        appliedDateText: "Dec 20, 2025",
        feedback:
            "Great work on the technical assessment! We’d love to move forward with an offer.",
        job: {
            title: "React Developer",
            companyName: "CloudNova",
            location: "Remote",
        },
        documents: [
            {
                fileId: "cv_003_aa11bb22",
                fileType: "CV",
                createdAt: "2025-12-20T07:58:00.000Z",
                fileUrl: "https://example.com/mock/cv_react_dev.pdf",
            },
            {
                fileId: "portfolio_003_cc33dd44",
                fileType: "Portfolio",
                createdAt: "2025-12-20T07:59:00.000Z",
                fileUrl: "https://example.com/mock/portfolio_cloudnova.pdf",
            },
        ],
    },

    {
        applicationId: "app_004",
        jobPostId: "job_1004",
        status: "REJECTED",
        createdAt: "2025-12-15T11:30:00.000Z",
        submissionDate: "2025-12-15T11:30:00.000Z",
        appliedDateText: "Dec 15, 2025",
        feedback:
            "Thank you for applying. We decided to proceed with other candidates at this time.",
        job: {
            title: "Backend Engineer",
            companyName: "DataForge Inc.",
            location: "Ha Noi, VN",
        },
        documents: [],
    },

    {
        applicationId: "app_005",
        jobPostId: "job_1005",
        status: "VIEWED",
        createdAt: "2026-01-08T16:00:00.000Z",
        submissionDate: "2026-01-08T16:00:00.000Z",
        appliedDateText: "Jan 8, 2026",
        feedback: "",
        job: {
            title: "Fullstack Developer",
            companyName: "GreenByte",
            location: "Ho Chi Minh City, VN",
        },
        documents: [
            {
                fileId: "cv_005_eeee9999",
                fileType: "CV",
                createdAt: "2026-01-08T15:50:00.000Z",
                fileUrl: "https://example.com/mock/cv_fullstack.pdf",
            },
            {
                fileId: "github_005_ffff8888",
                fileType: "GitHub",
                createdAt: "2026-01-08T15:55:00.000Z",
                fileUrl: "https://github.com/example/mock-profile",
            },
        ],
    },
];
