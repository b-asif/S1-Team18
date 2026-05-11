# TrackHire

**TrackHire** is a three-tier **Java Servlet + JSP** web application for job seekers to **register**, **sign in**, and **manage job applications**, **interviews**, and **technical assessments** in one session-backed workspace. Built for **CS 157A (Team 18)** using **Apache Tomcat 9**, **JDBC**, and **MySQL 8+**, with **BCrypt** passwords and standard web hardening (CSRF on mutating requests, session refresh on login, prepared statements, and output escaping where applied).

| | |
| :--- | :--- |
| **Stack** | Java 11, Maven (WAR), Servlet API 4.0 (`javax.servlet`), JSP, JDBC, MySQL 8+ |
| **Deploy** | `target/S1-TEAM18.war` → Tomcat `webapps/` |
| **Tests** | JUnit 5, Mockito; H2-backed integration tests (`mvn verify`) |

[`seed.sql`](seed.sql) defines the **MySQL schema** and **demo data** for the public repository. Optional team write-ups (`HISTORY.md`, `PROJECT_OVERVIEW.md`, course design report) are **gitignored** so they are not pushed with public commits; keep copies locally if you use them.

---

## Overview

The app addresses scattered tracking of application **stage**, **upcoming interviews**, and **technical screens** by persisting everything per-user in **MySQL**, with an **admin** role for user-directory maintenance only. Database credentials are never hard-coded: [`DBConnection.java`](src/main/java/com/myapp/util/DBConnection.java) requires **`DB_PASSWORD`** via environment variables or JVM system properties.

**Main areas (after login):** Dashboard (JDBC metrics and **48-hour interview alerts**), Applications (CRUD, **status filter**, **search**, **tags**, URL/location/notes), Interviews (CRUD, search), Technicals (assessments), Statistics (JDBC summaries), Profile (update, password change, gated account deletion), Forgot password (token flow). Admins see **Users** (`/users`).

---

## Capabilities

| Area | Description |
|------|-------------|
| **Auth** | Registration, login (BCrypt), POST logout with CSRF; session carries `userId` and `isAdmin`. |
| **Dashboard** | `GET /dashboard` — use servlet URL so attributes load; server-rendered KPIs and alerts. |
| **Applications** | List/add/**update**/delete with **user-scoped** DAO updates; `GET /applications?q=&status=`; per-user **tags** (CSRF on changes). |
| **Interviews** | List/add/delete; `GET /interviews?q=` keyword search. |
| **Technicals** | List/add/delete at `/technicals` with CSRF. |
| **Statistics** | `GET /statistics` — JDBC status and activity-style counts. |
| **Profile** | Field updates, password change; deletion requires **current password** and phrase **`I understand. Delete this account.`** |
| **Password reset** | `ForgotPasswordServlet` — hashed tokens, expiry, one-time use; see [Password reset (development)](#password-reset-development). |
| **Users (admin)** | `UserServlet` `/users` — non-admins redirected; admins edit directory fields (first/last name, email, username) with CSRF; **UI matches** the main app sidebar shell (`dashboard` / `statistics` style). |

Schema limitations: **company** is stored on the application row; interviews and technicals link to **userId** only (no **applicationId** FK). Statistics are summaries, not a full analytics stack. Keyword search uses **`LIKE`**. Re-running **`seed.sql`** upserts **Users** only; other **INSERT**s append unless the database is cleared or truncated first.

---

## Demo accounts

[`seed.sql`](seed.sql) creates **`trackhire`** and sample rows for immediate UI exploration.

| Username | Email | Password | Role | Notes |
|----------|--------|----------|------|--------|
| `alicej` | `alice@example.com` | `password` | Admin | Sample application, interview, technical; **`/users`**. |
| `brianl` | `brian@example.com` | `password` | User | Nine applications (all major statuses), tags, interviews, technicals. |

Existing databases missing newer columns should run [`docs/mysql-migration-20260510-applications-and-roles.sql`](docs/mysql-migration-20260510-applications-and-roles.sql) first, then apply data from **`seed.sql`** as needed. Skip statements that have already been applied.

**Restoring demo passwords after local DB edits:** If you ever overwrite a seeded user’s BCrypt hash (for example while debugging login), `seed.sql` includes an explicit `UPDATE` on **`alicej`** and **`brianl`** so their plaintext password is again **`password`**, matching the table above. Re-run that statement (or the full `seed.sql` flow) on schema **`trackhire`** as appropriate.

---

## Run locally

**Prerequisites:** JDK 11, Maven 3.8+, Tomcat 9, MySQL 8+, Git. **Windows:** PowerShell for [`scripts/Start-TrackHire.ps1`](scripts/Start-TrackHire.ps1).

**Database configuration** ([`DBConnection.java`](src/main/java/com/myapp/util/DBConnection.java)):

| Variable / property | Required | Default |
|---------------------|----------|---------|
| `DB_PASSWORD` / `db.password` | Yes | — |
| `DB_USER` / `db.user` | No | `root` |
| `DB_URL` / `db.url` | No | `jdbc:mysql://localhost:3306/trackhire?useSSL=true&serverTimezone=UTC` |

```powershell
$env:DB_PASSWORD = "your_mysql_password"
$env:DB_USER    = "root"
```

**Schema:** Execute **`seed.sql`** in MySQL Workbench or via the start script (without **`-SkipSeed`**).

**Build:**

```powershell
mvn clean package    # or: mvn clean verify
```

Deploy **`target/S1-TEAM18.war`** to **`<CATALINA_HOME>/webapps/`**, ensure the JVM sees **`DB_PASSWORD`** (or **`-Ddb.password`**), start Tomcat, open **`http://localhost:8080/S1-TEAM18/`** (adjust host/port/context as needed).

**Tomcat installed as a Windows service:** `setenv.bat` is often **not** read by the service. Use **Tomcat Monitor** / **`Tomcat9w.exe`** → **Java** → **Java options** and add **one option per line** (use your own MySQL password and URL; do not commit this file or screenshots):

```text
-Ddb.user=root
-Ddb.password=REPLACE_WITH_YOUR_MYSQL_PASSWORD
-Ddb.url=jdbc:mysql://localhost:3306/trackhire?useSSL=false&serverTimezone=UTC
```

Restart the service after changing options. **`catalina.*.log`** may print the full JVM command line—**treat logs as confidential** if they include `-Ddb.password`, and do not upload them to public issues.

**Clean redeploy (fixes stale JSP/servlet mix on Windows):**

```powershell
net stop "Apache Tomcat 9.0 Tomcat9_Server"   # use your service display name if different
Remove-Item -LiteralPath "$env:CATALINA_HOME\webapps\S1-TEAM18" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -LiteralPath "$env:CATALINA_HOME\webapps\S1-TEAM18.war" -Force -ErrorAction SilentlyContinue
# If CATALINA_HOME is unset, substitute the full path, e.g. C:\Program Files\Apache Software Foundation\Tomcat 9.0_Tomcat9_Server
mvn clean package
Copy-Item -Path "target\S1-TEAM18.war" -Destination "$env:CATALINA_HOME\webapps\S1-TEAM18.war" -Force
net start "Apache Tomcat 9.0 Tomcat9_Server"
```

**Scripted workflow:** Copy [`.env.local.example`](.env.local.example) to **`.env.local`**, set **`DB_PASSWORD`** and **`CATALINA_HOME`** when using automatic Tomcat start.

```powershell
.\scripts\Start-TrackHire.ps1              # seed + package + deploy/start (default)
.\scripts\Start-TrackHire.ps1 -Verify      # mvn clean verify + seed + deploy when applicable
.\scripts\Start-TrackHire.ps1 -SkipSeed    # build/deploy without re-seeding
.\scripts\Start-TrackHire.ps1 -SkipTomcat # Maven (+ seed) only
.\scripts\Stop-TrackHire.ps1
```

IDE task runners can invoke the same script. If **`mysql`** is not on `PATH`, set **`MYSQL_EXECUTABLE`** in **`.env.local`**. Passwords containing **`#`** may break the CLI defaults file the script generates; seed via Workbench in that case.

---

## Testing

**Automated**

```powershell
mvn test      # Surefire
mvn verify    # Failsafe / H2 integration paths (see `pom.xml`)
```

H2 validates DAOs and controller branches; it does not replace MySQL + Tomcat acceptance testing.

**Manual (servlet URLs, not raw JSPs for data pages)**

| Check | Expected |
|-------|----------|
| Login `brianl` / `password` | Dashboard; no admin Users entry. |
| `/applications` | Seeded rows; search and status filter work; tags where seeded. |
| `/interviews?q=` | Keyword filter. |
| `/technicals`, `/statistics`, `/dashboard` | Data matches DB; dashboard shows 48h alerts when dates qualify. |
| Login `alicej` / `password` | `/users` works; same URL as `brianl` does not grant directory admin. |
| Profile | Updates and password change; deletion only with password + confirmation phrase. |
| `POST /logout` | CSRF present; session ends. |

**Servlet paths** (prefix **`/S1-TEAM18`** for default context): `GET /login.jsp`, `POST /login`; `GET|POST /register`; `GET /dashboard`; `GET|POST /applications` (`q`, `status`); `GET|POST /interviews` (`q`); `GET|POST /technicals`; `GET /statistics`; `GET|POST /profile`; `GET|POST /forgot-password`; `POST /logout`; `GET|POST /users` (admin).

Use **`/dashboard`**, **`/statistics`**, and **`/technicals`** for those features—not bookmarked `dashboard.jsp`, `statistics.jsp`, or `assessments.jsp` at the webapp root (those may 404 or bypass servlet setup depending on deploy).

---

## Password reset (development)

The reset token is written to **Tomcat logs / stderr** in development for convenience. In production, replace this with email or another secure delivery channel. Flow: submit step 1 on **`/forgot-password`**, read the token from logs, open **`forgot-password?token=...`**, set a new password.

---

## Security controls

| Control | Implementation |
|---------|----------------|
| Passwords | BCrypt (`jbcrypt`) |
| SQL | Prepared statements |
| CSRF | `CsrfUtil` on mutating POSTs |
| Sessions | New session on successful login |
| Logout | POST + CSRF |
| XSS | `HtmlUtil` where used in JSPs |
| IDOR | Updates/deletes scoped by `userId` except admin directory rules |
| Secrets | `DB_PASSWORD` via env or JVM properties only |

---

## Encoding (JSP / login branding)

Auth pages declare **UTF-8** (`contentType`, `pageEncoding`, `<meta charset>`). The login/forgot-password marketing line uses an HTML numeric entity for the star rating so it does not render as mojibake (e.g. `â˜…`) if a tool mis-reads the source file.

---

## Before a public commit

- **Never commit** **`.env.local`** (gitignored); keep only **`.env.local.example`** with placeholders.
- **Do not commit** real MySQL passwords, Tomcat Java-option screenshots, or **`catalina.*.log` / `localhost.*.log`** lines that contain **`-Ddb.password=`**.
- Ensure **`target/`** and internal docs remain ignored—see [`.gitignore`](.gitignore) (**`HISTORY.md`**, **`PROJECT_OVERVIEW.md`**, course design report, **`.env.local`**).
- If those files were **already tracked**, remove them from the index once (they stay on disk): `git rm --cached HISTORY.md PROJECT_OVERVIEW.md "CS157A Team 18 - Project Data Model & DB Design Report.md" 2>$null; git rm -r --cached target 2>$null` (PowerShell; adjust if some paths were never tracked).
- Prefer **`mvn clean package`** then deploy the new **`S1-TEAM18.war`**; avoid checking in **`target/`** or old WARs.

---

## Troubleshooting

| Issue | Action |
|-------|--------|
| Missing `DB_PASSWORD` | Export variables or use `.env.local` with `Start-TrackHire.ps1` |
| Seed fails from script | Run `seed.sql` in Workbench; verify `mysql` path and credentials |
| Empty dashboard/lists | Navigate via **`/dashboard`**, **`/applications`**, etc. |
| Port 8080 in use | `Stop-TrackHire.ps1` or stop redundant Tomcat instances |
| JDBC SSL locally | Use `useSSL=false` in `DB_URL` for local-only dev |
| 404 on `statistics.jsp` / `assessments.jsp` | Use **`/statistics`** and **`/technicals`** (servlets). |
| Login works for new users but not seeded users | Re-run the demo **`UPDATE`** in [`seed.sql`](seed.sql) for **`alicej`** / **`brianl`**, or restore the exact BCrypt string from the file; demo plaintext is **`password`**. |

---

## Repository layout

| Path | Contents |
|------|----------|
| [`src/main/java/com/myapp/controller/`](src/main/java/com/myapp/controller) | Servlets |
| [`src/main/java/com/myapp/dao/`](src/main/java/com/myapp/dao) | JDBC |
| [`src/main/java/com/myapp/model/`](src/main/java/com/myapp/model) | Models |
| [`src/main/java/com/myapp/util/`](src/main/java/com/myapp/util) | `DBConnection`, `CsrfUtil`, `HtmlUtil`, `ResetTokenUtil` |
| [`src/main/webapp/`](src/main/webapp) | JSP, CSS, `WEB-INF/web.xml` |
| [`src/test/`](src/test) | Tests, `schema-h2.sql`, `data-h2.sql` |
| [`scripts/`](scripts) | Start/stop PowerShell helpers |
| [`docs/`](docs) | MySQL migration SQL |
| [`seed.sql`](seed.sql) | MySQL DDL + demo inserts |
