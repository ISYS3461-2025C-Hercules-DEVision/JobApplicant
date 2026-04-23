# Deployment

This branch is prepared for:

- Render: Spring Boot backend services, Eureka, Kafka, and Redis-compatible token revocation.
- Vercel: Vite React frontend.

## Render

Use the root `render.yaml` Blueprint from the Render dashboard. The Blueprint intentionally leaves account-specific secrets as `sync: false`; enter them in Render when prompted.

Required Render values:

| Service | Variable | Notes |
| --- | --- | --- |
| `jobapp-authentication` | `MONGODB_URI` | MongoDB connection string for auth users and refresh tokens. |
| `jobapp-authentication` | `GOOGLE_CLIENT_ID` | Google OAuth client ID. |
| `jobapp-authentication` | `GOOGLE_CLIENT_SECRET` | Google OAuth client secret. |
| `jobapp-authentication` | `FRONTEND_REDIRECT_URL` | Vercel frontend URL after login, for example `https://<vercel-project>.vercel.app/auth/callback`. |
| `jobapp-authentication` | `FRONTEND_BANNED_URL` | Vercel frontend banned-account URL. |
| `jobapp-authentication` | `APP_SUPER_ADMIN_EMAIL` | Initial super admin email. |
| `jobapp-authentication` | `APP_SUPER_ADMIN_PASSWORD` | Initial super admin password. |
| `jobapp-application` | `MONGODB_URI` | MongoDB connection string for applications. |
| `jobapp-application` | `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name. |
| `jobapp-application` | `CLOUDINARY_API_KEY` | Cloudinary API key. |
| `jobapp-application` | `CLOUDINARY_API_SECRET` | Cloudinary API secret. |
| `jobapp-applicant` | `MONGODB_URI` | MongoDB connection string for applicants. |
| `jobapp-applicant` | `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name. |
| `jobapp-applicant` | `CLOUDINARY_API_KEY` | Cloudinary API key. |
| `jobapp-applicant` | `CLOUDINARY_API_SECRET` | Cloudinary API secret. |
| `jobapp-subscription` | `MONGODB_URI` | MongoDB connection string for subscriptions. |
| `jobapp-subscription` | `STRIPE_API_KEY` | Stripe secret key. |
| `jobapp-subscription` | `STRIPE_SUCCESS_URL` | Vercel URL for successful checkout, for example `https://<vercel-project>.vercel.app/subscription/return`. |
| `jobapp-subscription` | `STRIPE_CANCEL_URL` | Vercel URL for cancelled checkout, for example `https://<vercel-project>.vercel.app/subscription`. |
| `jobapp-admin` | `MONGODB_URI` | MongoDB connection string for admin records. |
| `jobapp-api-gateway` | `JOB_MANAGER_URI` | External Job Manager API origin. |

After Render creates `jobapp-api-gateway`, use its `https://jobapp-api-gateway.onrender.com` URL as the Vercel `VITE_API_BASE` unless Render assigns a different slug.

## Vercel

Import the repository into Vercel with:

- Root directory: `frontend`
- Framework preset: Vite
- Install command: `npm ci`
- Build command: `npm run build`
- Output directory: `dist`

Set these Vercel environment variables:

```env
VITE_API_BASE=https://jobapp-api-gateway.onrender.com
VITE_API_BASE_JOB_MANAGER=https://jobapp-api-gateway.onrender.com/job-manager
```

If the Render gateway slug differs, replace `jobapp-api-gateway.onrender.com` with the actual Render URL.

## Post-deploy checks

1. Confirm the Render gateway health endpoint returns 200:

   ```bash
   curl https://jobapp-api-gateway.onrender.com/actuator/health
   ```

2. Confirm Vercel can reach the backend by opening the deployed frontend and using login/register flows.
3. Add the final Vercel production domain to `CORS_ALLOWED_ORIGINS` on `jobapp-api-gateway` if it is not covered by `https://*.vercel.app`.
