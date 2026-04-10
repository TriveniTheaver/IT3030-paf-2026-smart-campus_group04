# Smart Campus Operations Hub (IT3030 PAF)

Spring Boot REST API + React (Vite) client for facility bookings, incident ticketing, notifications, and OAuth-style authentication.

## Prerequisites

- **JDK 23** (see `backend/pom.xml`; CI uses the same)
- **Maven 3.9+**
- **Node.js 20+** and npm

## Backend

From the `backend` directory:

```bash
mvn clean verify
```

Run the API (default port **8081**):

```bash
cd web-app
mvn spring-boot:run
```

Configuration: `backend/web-app/src/main/resources/application.yml` (H2 in-memory by default). Replace Google OAuth placeholders for real Google sign-in.

### Seeded accounts (development)

| Email            | Password    | Role        |
|------------------|------------|-------------|
| admin@sliit.lk   | password123 | ADMIN      |
| tech@sliit.lk    | password123 | TECHNICIAN |
| it12345678@my.sliit.lk | password123 | USER |

## Frontend

From the `frontend` directory:

```bash
npm install
npm run dev
```

Vite dev server proxies `/api` to `http://localhost:8081` (see `frontend/vite.config.js`).

## Member 1 — Facilities catalogue & resource management

**REST API (base path `/api/facilities`)**

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| GET | `/api/facilities` | List resources; optional query params `type`, `minCapacity`, `location` | Public |
| GET | `/api/facilities/{id}` | Resource by id | Public |
| POST | `/api/facilities` | Create resource | ADMIN |
| PUT | `/api/facilities/{id}` | Update resource | ADMIN |
| DELETE | `/api/facilities/{id}` | Delete resource | ADMIN |

Create/update bodies use `ResourceRequest` with server-side validation (name, type, capacity, location, status, optional availability window and image URL). Validation errors return HTTP **400** with JSON `{ "message": "Validation failed", "errors": { ... } }`.

**React UI**

- `frontend/src/modules/facilities/components/FacilitiesDashboard.jsx` — browse, filter, admin CRUD, booking entry for eligible users.
- `frontend/src/modules/facilities/components/ResourceModal.jsx` — create/edit form with client-side checks and display of API validation messages.

**Automated tests**

- `backend/web-app/src/test/java/com/smartcampus/FacilitiesResourceApiIT.java` — public GET, invalid POST (400), authenticated admin POST (201).

## Member 3 — Incident / maintenance ticketing (tickets, attachments, comments, technician workflow)

Assignment alignment: **Module C** (incident tickets, evidence attachments, workflow, technician assignment, comments with ownership). This section lists **Member 3** API and UI ownership for individual marking.

### REST API (base path `/api/tickets`)

| Method | Path | What it does | Access / roles |
|--------|------|----------------|----------------|
| POST | `/api/tickets` | Create ticket (JSON body; optional attachment URL strings). | **USER** (`application/json`) |
| POST | `/api/tickets` | Create ticket with up to **3** image uploads (`multipart/form-data`: `resourceId`, optional `category`, `priority`, `description`, `preferredContact`, repeated `files`). | **USER** (`multipart/form-data`) |
| GET | `/api/tickets/files/{filename}` | Download stored evidence file (opaque UUID filename). | **Authenticated** (JWT) |
| GET | `/api/tickets` | List **all** tickets (triage queue). | **ADMIN** |
| GET | `/api/tickets/assigned-to-me` | List tickets assigned to the current technician. | **TECHNICIAN** |
| GET | `/api/tickets/mine` | List tickets reported by the current user. | **USER** |
| GET | `/api/tickets/user/{reporterId}` | List tickets by reporter id. | **ADMIN**, or **same user** as `reporterId` |
| GET | `/api/tickets/{id}` | Get one ticket by id. | **Authenticated** if user may access that ticket (reporter, assignee, or **ADMIN**; otherwise **403**) |
| PUT | `/api/tickets/{id}/status` | Update status / optional `resolutionNotes` (workflow rules enforced in service). | **ADMIN** or **TECHNICIAN** (technician only on **assigned** tickets) |
| PUT | `/api/tickets/{id}/assign` | Assign a technician (`technicianId`). | **ADMIN** |
| GET | `/api/tickets/{ticketId}/comments` | List comments on a ticket (oldest first). | **Authenticated** if user may access the ticket |
| POST | `/api/tickets/{ticketId}/comments` | Add comment (`{ "content": "..." }`). | **Authenticated** if user may access the ticket |
| PUT | `/api/tickets/{ticketId}/comments/{commentId}` | Edit comment (author only). | **Authenticated**; **403** if not author |
| DELETE | `/api/tickets/{ticketId}/comments/{commentId}` | Delete comment (author only). | **Authenticated**; **204**; **403** if not author |

**Related (not Member 3, but used by the UI):** `GET /api/facilities` (resource picker), `GET /api/users/technicians` (admin assign dropdown) — implemented in other modules.

**Backend packages (Member 3):** `backend/incidents-module/` — `controller`, `service`, `model`, `repository`, `storage` (`TicketAttachmentStorageService`: image validation, size limits, safe filenames, disk storage under `app.ticket-upload.dir`).

**Configuration:** `spring.servlet.multipart` and `app.ticket-upload.dir` in `backend/web-app/src/main/resources/application.yml` (and `application-mysql.yml`).

### React UI (Member 3)

| File | Role |
|------|------|
| `frontend/src/modules/incidents/components/IncidentsDashboard.jsx` | Full incidents experience: **USER** — report form (facility, category, priority, description, contact, **file upload** max 3 images with merge/replace UX); **ADMIN** — full queue, assign technician, triage statuses; **TECHNICIAN** — assigned queue, field status updates; ticket detail modal with description, **evidence images** (including authenticated blob load for uploaded files), **comment thread**, **inline edit/delete** for own comments. Includes helper **`AuthenticatedTicketImage`** (same file) for JWT-protected image URLs. |

**Routing:** the incidents screen is mounted in `frontend/src/App.jsx` at route **`/incidents`** (`<IncidentsDashboard />`).

## GitHub Actions

Workflow should run `mvn clean verify` on push/PR so the backend compiles and tests pass.

## Repository naming (assignment)

Use the course naming pattern, e.g. `it3030-paf-2026-smart-campus-groupXX`, and exclude `node_modules`, `target`, and other build artifacts from submission zips.
