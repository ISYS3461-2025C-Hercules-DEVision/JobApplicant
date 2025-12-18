import React, { useMemo, useState } from "react";
import {
    BrowserRouter as Router,
    Routes,
    Route,
    NavLink,
    Navigate,
    useLocation,
} from "react-router-dom";
import {
    LayoutDashboard,
    User,
    Building2,
    FileText,
    Briefcase,
    Search,
    Menu,
    LogOut,
    ChevronRight,
} from "lucide-react";

/**
 * Single-file Admin Dashboard + Left Sidebar
 * - 4 pages: Applicant Account, Company Account, Application (CV), Job Post
 * - Each page shows its own table
 * - Uses Tailwind + lucide-react; router via react-router-dom
 */

// ---------- UI Primitives (lightweight, no shadcn dependency required) ----------
function cn(...parts) {
    return parts.filter(Boolean).join(" ");
}

function Card({ className, children }) {
    return (
        <div
            className={cn(
                "rounded-2xl border border-slate-200/70 bg-white shadow-sm",
                className
            )}
        >
            {children}
        </div>
    );
}

function Button({ className, variant = "default", ...props }) {
    const base =
        "inline-flex items-center justify-center gap-2 rounded-xl px-3 py-2 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-slate-200 disabled:opacity-50";
    const styles = {
        default: "bg-slate-900 text-white hover:bg-slate-800",
        ghost: "bg-transparent hover:bg-slate-100 text-slate-700",
        outline:
            "bg-white border border-slate-200 text-slate-800 hover:bg-slate-50",
    };
    return <button className={cn(base, styles[variant], className)} {...props} />;
}

function Badge({ children, className }) {
    return (
        <span
            className={cn(
                "inline-flex items-center rounded-full border border-slate-200 bg-slate-50 px-2 py-0.5 text-xs font-medium text-slate-700",
                className
            )}
        >
      {children}
    </span>
    );
}

function Input({ className, ...props }) {
    return (
        <input
            className={cn(
                "h-10 w-full rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-200",
                className
            )}
            {...props}
        />
    );
}

function Table({ columns, rows, rowKey }) {
    return (
        <div className="overflow-hidden rounded-2xl border border-slate-200">
            <div className="overflow-x-auto">
                <table className="min-w-full text-left text-sm">
                    <thead className="bg-slate-50 text-slate-600">
                    <tr>
                        {columns.map((c) => (
                            <th
                                key={c.key}
                                className="whitespace-nowrap px-4 py-3 font-semibold"
                            >
                                {c.header}
                            </th>
                        ))}
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 bg-white">
                    {rows.map((r) => (
                        <tr key={rowKey(r)} className="hover:bg-slate-50/60">
                            {columns.map((c) => (
                                <td
                                    key={c.key}
                                    className="whitespace-nowrap px-4 py-3 text-slate-800"
                                >
                                    {typeof c.cell === "function" ? c.cell(r) : r[c.key]}
                                </td>
                            ))}
                        </tr>
                    ))}
                    {rows.length === 0 && (
                        <tr>
                            <td
                                colSpan={columns.length}
                                className="px-4 py-10 text-center text-slate-500"
                            >
                                No results.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

// ---------- Demo data (replace with API calls) ----------
const seed = {
    applicants: [
        {
            id: "A-1001",
            name: "Nguyen Minh Anh",
            email: "anh.nguyen@example.com",
            status: "Active",
            createdAt: "2025-12-01",
        },
        {
            id: "A-1002",
            name: "Tran Quoc Huy",
            email: "huy.tran@example.com",
            status: "Suspended",
            createdAt: "2025-11-21",
        },
        {
            id: "A-1003",
            name: "Le Thi Mai",
            email: "mai.le@example.com",
            status: "Active",
            createdAt: "2025-10-10",
        },
    ],
    companies: [
        {
            id: "C-2001",
            company: "Blue Horizon LLC",
            email: "hr@bluehorizon.com",
            plan: "Pro",
            verified: true,
        },
        {
            id: "C-2002",
            company: "Saigon Tech",
            email: "talent@saigontech.vn",
            plan: "Free",
            verified: false,
        },
        {
            id: "C-2003",
            company: "Lotus Finance",
            email: "careers@lotusfin.co",
            plan: "Business",
            verified: true,
        },
    ],
    applications: [
        {
            id: "CV-3001",
            applicant: "Nguyen Minh Anh",
            jobTitle: "Frontend Developer",
            company: "Saigon Tech",
            stage: "Screening",
            submittedAt: "2025-12-12",
        },
        {
            id: "CV-3002",
            applicant: "Le Thi Mai",
            jobTitle: "QA Engineer",
            company: "Blue Horizon LLC",
            stage: "Interview",
            submittedAt: "2025-12-08",
        },
        {
            id: "CV-3003",
            applicant: "Tran Quoc Huy",
            jobTitle: "Data Analyst",
            company: "Lotus Finance",
            stage: "Rejected",
            submittedAt: "2025-11-25",
        },
    ],
    jobs: [
        {
            id: "J-4001",
            title: "Frontend Developer",
            company: "Saigon Tech",
            location: "HCMC (Hybrid)",
            status: "Open",
            postedAt: "2025-12-05",
        },
        {
            id: "J-4002",
            title: "QA Engineer",
            company: "Blue Horizon LLC",
            location: "Da Nang",
            status: "Open",
            postedAt: "2025-12-01",
        },
        {
            id: "J-4003",
            title: "Data Analyst",
            company: "Lotus Finance",
            location: "Remote",
            status: "Closed",
            postedAt: "2025-11-18",
        },
    ],
};

// ---------- Layout ----------
const NAV = [
    {
        to: "/dashboard",
        label: "Dashboard",
        icon: LayoutDashboard,
        badge: "NEW",
    },
    { to: "/applicants", label: "Applicant Account", icon: User },
    { to: "/companies", label: "Company Account", icon: Building2 },
    { to: "/applications", label: "Application (CV)", icon: FileText },
    { to: "/jobs", label: "Job Post", icon: Briefcase },
];

function Shell({ children }) {
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const location = useLocation();

    React.useEffect(() => {
        // close sidebar on route change (mobile)
        setSidebarOpen(false);
    }, [location.pathname]);

    return (
        <div className="min-h-screen bg-slate-50">
            {/* Mobile top bar */}
            <div className="sticky top-0 z-40 border-b border-slate-200 bg-white/80 backdrop-blur md:hidden">
                <div className="flex items-center gap-2 px-4 py-3">
                    <Button
                        variant="ghost"
                        onClick={() => setSidebarOpen((v) => !v)}
                        aria-label="Toggle sidebar"
                        className="-ml-2"
                    >
                        <Menu className="h-5 w-5" />
                    </Button>
                    <div className="flex items-center gap-2">
                        <div className="h-8 w-8 rounded-xl bg-slate-900" />
                        <div className="leading-tight">
                            <div className="text-sm font-semibold text-slate-900">
                                Admin
                            </div>
                            <div className="text-xs text-slate-500">Dashboard</div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="mx-auto flex max-w-7xl">
                {/* Sidebar */}
                <aside
                    className={cn(
                        "fixed inset-y-0 left-0 z-50 w-72 border-r border-slate-200 bg-white md:sticky md:top-0 md:z-0",
                        sidebarOpen ? "translate-x-0" : "-translate-x-full md:translate-x-0",
                        "transition-transform duration-200"
                    )}
                >
                    <div className="flex h-full flex-col">
                        <div className="flex items-center gap-3 px-5 py-5">
                            <div className="h-10 w-10 rounded-2xl bg-slate-900" />
                            <div>
                                <div className="text-sm font-semibold text-slate-900">
                                    Admin Dashboard
                                </div>
                                <div className="text-xs text-slate-500">Management</div>
                            </div>
                        </div>

                        <div className="px-4 pb-3">
                            <div className="relative">
                                <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                                <Input placeholder="Search..." className="pl-9" />
                            </div>
                        </div>

                        <nav className="flex-1 px-3 pb-4">
                            <div className="px-2 pb-2 text-xs font-semibold uppercase tracking-wide text-slate-400">
                                Theme
                            </div>
                            <div className="space-y-1 px-1 pb-4">
                                <SidebarItem to="/dashboard" label="Dashboard" icon={LayoutDashboard} badge="NEW" />
                                <SidebarDivider />
                            </div>

                            <div className="px-2 pb-2 text-xs font-semibold uppercase tracking-wide text-slate-400">
                                Components
                            </div>
                            <div className="space-y-1 px-1">
                                <SidebarItem to="/applicants" label="Applicant Account" icon={User} />
                                <SidebarItem to="/companies" label="Company Account" icon={Building2} />
                                <SidebarItem to="/applications" label="Application (CV)" icon={FileText} />
                                <SidebarItem to="/jobs" label="Job Post" icon={Briefcase} />
                            </div>
                        </nav>

                        <div className="border-t border-slate-200 p-4">
                            <Button variant="outline" className="w-full justify-between">
                <span className="flex items-center gap-2">
                  <LogOut className="h-4 w-4" /> Logout
                </span>
                                <ChevronRight className="h-4 w-4 text-slate-400" />
                            </Button>
                        </div>
                    </div>
                </aside>

                {/* Overlay for mobile */}
                {sidebarOpen && (
                    <div
                        className="fixed inset-0 z-40 bg-black/30 md:hidden"
                        onClick={() => setSidebarOpen(false)}
                    />
                )}

                {/* Main */}
                <main className="flex-1 px-4 py-6 md:px-8 md:py-8">
                    {children}
                </main>
            </div>
        </div>
    );
}

function SidebarDivider() {
    return <div className="my-2 h-px bg-slate-100" />;
}

function SidebarItem({ to, label, icon: Icon, badge }) {
    return (
        <NavLink
            to={to}
            className={({ isActive }) =>
                cn(
                    "group flex items-center justify-between rounded-xl px-3 py-2 text-sm font-medium",
                    isActive
                        ? "bg-slate-900 text-white"
                        : "text-slate-700 hover:bg-slate-100"
                )
            }
        >
            {({ isActive }) => (
                <>
          <span className="flex items-center gap-2">
            <Icon
                className={cn(
                    "h-4 w-4",
                    isActive ? "text-white" : "text-slate-500"
                )}
            />
              {label}
          </span>
                    {badge ? (
                        <span
                            className={cn(
                                "rounded-full px-2 py-0.5 text-[10px] font-semibold",
                                isActive
                                    ? "bg-white/15 text-white"
                                    : "bg-slate-100 text-slate-700"
                            )}
                        >
              {badge}
            </span>
                    ) : null}
                </>
            )}
        </NavLink>
    );
}

// ---------- Pages ----------
function PageHeader({ title, subtitle, right }) {
    return (
        <div className="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
            <div>
                <h1 className="text-2xl font-semibold tracking-tight text-slate-900">
                    {title}
                </h1>
                {subtitle ? (
                    <p className="mt-1 text-sm text-slate-500">{subtitle}</p>
                ) : null}
            </div>
            {right ? <div className="flex items-center gap-2">{right}</div> : null}
        </div>
    );
}

function Dashboard() {
    return (
        <div className="space-y-6">
            <PageHeader
                title="Dashboard"
                subtitle="Quick overview of your admin system"
                right={
                    <>
                        <Button variant="outline">Export</Button>
                        <Button>Create</Button>
                    </>
                }
            />

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatCard label="Applicants" value={seed.applicants.length} />
                <StatCard label="Companies" value={seed.companies.length} />
                <StatCard label="Applications" value={seed.applications.length} />
                <StatCard label="Job Posts" value={seed.jobs.length} />
            </div>

            <Card className="p-5">
                <div className="flex items-center justify-between">
                    <div>
                        <div className="text-sm font-semibold text-slate-900">
                            Recent Applications
                        </div>
                        <div className="text-xs text-slate-500">
                            Latest CV submissions across the platform
                        </div>
                    </div>
                    <NavLink
                        to="/applications"
                        className="text-sm font-medium text-slate-700 hover:text-slate-900"
                    >
                        View all
                    </NavLink>
                </div>
                <div className="mt-4">
                    <Table
                        rowKey={(r) => r.id}
                        columns={[
                            { key: "id", header: "ID" },
                            { key: "applicant", header: "Applicant" },
                            { key: "jobTitle", header: "Job" },
                            { key: "company", header: "Company" },
                            {
                                key: "stage",
                                header: "Stage",
                                cell: (r) => <StatusPill value={r.stage} />,
                            },
                            { key: "submittedAt", header: "Submitted" },
                        ]}
                        rows={seed.applications.slice(0, 5)}
                    />
                </div>
            </Card>
        </div>
    );
}

function StatCard({ label, value }) {
    return (
        <Card className="p-5">
            <div className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                {label}
            </div>
            <div className="mt-2 text-3xl font-semibold text-slate-900">{value}</div>
            <div className="mt-2 text-xs text-slate-500">Updated just now</div>
        </Card>
    );
}

function StatusPill({ value }) {
    const map = {
        Active: "bg-emerald-50 text-emerald-700 border-emerald-200",
        Suspended: "bg-amber-50 text-amber-700 border-amber-200",
        Open: "bg-emerald-50 text-emerald-700 border-emerald-200",
        Closed: "bg-slate-100 text-slate-700 border-slate-200",
        Screening: "bg-blue-50 text-blue-700 border-blue-200",
        Interview: "bg-purple-50 text-purple-700 border-purple-200",
        Rejected: "bg-rose-50 text-rose-700 border-rose-200",
        Free: "bg-slate-100 text-slate-700 border-slate-200",
        Pro: "bg-indigo-50 text-indigo-700 border-indigo-200",
        Business: "bg-slate-900/5 text-slate-800 border-slate-200",
    };
    return (
        <span
            className={cn(
                "inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium",
                map[value] || "bg-slate-50 text-slate-700 border-slate-200"
            )}
        >
      {value}
    </span>
    );
}

function ApplicantsPage() {
    const [q, setQ] = useState("");
    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return seed.applicants;
        return seed.applicants.filter(
            (r) =>
                r.name.toLowerCase().includes(s) ||
                r.email.toLowerCase().includes(s) ||
                r.id.toLowerCase().includes(s)
        );
    }, [q]);

    return (
        <div className="space-y-6">
            <PageHeader
                title="Applicant Account"
                subtitle="Manage applicant profiles and access"
                right={
                    <>
                        <div className="w-64">
                            <Input
                                value={q}
                                onChange={(e) => setQ(e.target.value)}
                                placeholder="Search applicants..."
                            />
                        </div>
                        <Button>Add applicant</Button>
                    </>
                }
            />

            <Card className="p-5">
                <Table
                    rowKey={(r) => r.id}
                    columns={[
                        { key: "id", header: "Applicant ID" },
                        { key: "name", header: "Name" },
                        { key: "email", header: "Email" },
                        {
                            key: "status",
                            header: "Status",
                            cell: (r) => <StatusPill value={r.status} />,
                        },
                        { key: "createdAt", header: "Created" },
                        {
                            key: "actions",
                            header: "Actions",
                            cell: () => (
                                <div className="flex gap-2">
                                    <Button variant="outline" className="h-9 px-3">
                                        View
                                    </Button>
                                    <Button variant="ghost" className="h-9 px-3">
                                        Edit
                                    </Button>
                                </div>
                            ),
                        },
                    ]}
                    rows={rows}
                />
            </Card>
        </div>
    );
}

function CompaniesPage() {
    const [q, setQ] = useState("");
    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return seed.companies;
        return seed.companies.filter(
            (r) =>
                r.company.toLowerCase().includes(s) ||
                r.email.toLowerCase().includes(s) ||
                r.id.toLowerCase().includes(s)
        );
    }, [q]);

    return (
        <div className="space-y-6">
            <PageHeader
                title="Company Account"
                subtitle="Manage employer accounts and verification"
                right={
                    <>
                        <div className="w-64">
                            <Input
                                value={q}
                                onChange={(e) => setQ(e.target.value)}
                                placeholder="Search companies..."
                            />
                        </div>
                        <Button>Add company</Button>
                    </>
                }
            />

            <Card className="p-5">
                <Table
                    rowKey={(r) => r.id}
                    columns={[
                        { key: "id", header: "Company ID" },
                        { key: "company", header: "Company" },
                        { key: "email", header: "Email" },
                        {
                            key: "plan",
                            header: "Plan",
                            cell: (r) => <StatusPill value={r.plan} />,
                        },
                        {
                            key: "verified",
                            header: "Verified",
                            cell: (r) =>
                                r.verified ? (
                                    <Badge className="bg-emerald-50 text-emerald-700 border-emerald-200">
                                        Yes
                                    </Badge>
                                ) : (
                                    <Badge>No</Badge>
                                ),
                        },
                        {
                            key: "actions",
                            header: "Actions",
                            cell: () => (
                                <div className="flex gap-2">
                                    <Button variant="outline" className="h-9 px-3">
                                        View
                                    </Button>
                                    <Button variant="ghost" className="h-9 px-3">
                                        Verify
                                    </Button>
                                </div>
                            ),
                        },
                    ]}
                    rows={rows}
                />
            </Card>
        </div>
    );
}

function ApplicationsPage() {
    const [q, setQ] = useState("");
    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return seed.applications;
        return seed.applications.filter(
            (r) =>
                r.applicant.toLowerCase().includes(s) ||
                r.jobTitle.toLowerCase().includes(s) ||
                r.company.toLowerCase().includes(s) ||
                r.id.toLowerCase().includes(s)
        );
    }, [q]);

    return (
        <div className="space-y-6">
            <PageHeader
                title="Application (CV)"
                subtitle="Track CV submissions and hiring stages"
                right={
                    <>
                        <div className="w-64">
                            <Input
                                value={q}
                                onChange={(e) => setQ(e.target.value)}
                                placeholder="Search applications..."
                            />
                        </div>
                        <Button>Download CSV</Button>
                    </>
                }
            />

            <Card className="p-5">
                <Table
                    rowKey={(r) => r.id}
                    columns={[
                        { key: "id", header: "Application ID" },
                        { key: "applicant", header: "Applicant" },
                        { key: "jobTitle", header: "Job" },
                        { key: "company", header: "Company" },
                        {
                            key: "stage",
                            header: "Stage",
                            cell: (r) => <StatusPill value={r.stage} />,
                        },
                        { key: "submittedAt", header: "Submitted" },
                        {
                            key: "actions",
                            header: "Actions",
                            cell: () => (
                                <div className="flex gap-2">
                                    <Button variant="outline" className="h-9 px-3">
                                        View CV
                                    </Button>
                                    <Button variant="ghost" className="h-9 px-3">
                                        Update Stage
                                    </Button>
                                </div>
                            ),
                        },
                    ]}
                    rows={rows}
                />
            </Card>
        </div>
    );
}

function JobsPage() {
    const [q, setQ] = useState("");
    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return seed.jobs;
        return seed.jobs.filter(
            (r) =>
                r.title.toLowerCase().includes(s) ||
                r.company.toLowerCase().includes(s) ||
                r.location.toLowerCase().includes(s) ||
                r.id.toLowerCase().includes(s)
        );
    }, [q]);

    return (
        <div className="space-y-6">
            <PageHeader
                title="Job Post"
                subtitle="Manage job listings and publishing status"
                right={
                    <>
                        <div className="w-64">
                            <Input
                                value={q}
                                onChange={(e) => setQ(e.target.value)}
                                placeholder="Search jobs..."
                            />
                        </div>
                        <Button>Create job</Button>
                    </>
                }
            />

            <Card className="p-5">
                <Table
                    rowKey={(r) => r.id}
                    columns={[
                        { key: "id", header: "Job ID" },
                        { key: "title", header: "Title" },
                        { key: "company", header: "Company" },
                        { key: "location", header: "Location" },
                        {
                            key: "status",
                            header: "Status",
                            cell: (r) => <StatusPill value={r.status} />,
                        },
                        { key: "postedAt", header: "Posted" },
                        {
                            key: "actions",
                            header: "Actions",
                            cell: () => (
                                <div className="flex gap-2">
                                    <Button variant="outline" className="h-9 px-3">
                                        Edit
                                    </Button>
                                    <Button variant="ghost" className="h-9 px-3">
                                        Toggle
                                    </Button>
                                </div>
                            ),
                        },
                    ]}
                    rows={rows}
                />
            </Card>
        </div>
    );
}

// ---------- App ----------
export default function App() {
    return (
        <Router>
            <Shell>
                <Routes>
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/applicants" element={<ApplicantsPage />} />
                    <Route path="/companies" element={<CompaniesPage />} />
                    <Route path="/applications" element={<ApplicationsPage />} />
                    <Route path="/jobs" element={<JobsPage />} />
                    <Route
                        path="*"
                        element={
                            <div className="p-6">
                                <div className="text-lg font-semibold text-slate-900">
                                    Not found
                                </div>
                                <div className="mt-1 text-sm text-slate-500">
                                    The page you are looking for doesn’t exist.
                                </div>
                            </div>
                        }
                    />
                </Routes>
            </Shell>
        </Router>
    );
}
