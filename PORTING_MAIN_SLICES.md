# Robustness ports onto `main` (slice manifest)

Baseline: **`origin/main`** at start of port. Source snapshots: **`am`** merged tip (TrackHire hardening).

Verification each slice: **`mvn clean verify`**.

Commit messages stay short and do not mention other branch names.

| Slice | Commit theme | Paths (from `am` unless noted) | Status |
|------|----------------|--------------------------------|--------|
| 1 | CSRF / POST + servlet validation | Utilities `CsrfUtil`, `HtmlUtil`, `ResetTokenUtil` + tests; servlets listed in plan (all POST paths); DAO/model support: `ApplicationDAO`, `InterviewDAO`, `UserDAO`, `TagDAO`, `Application`, `User`; related JSPs; **`pom.xml`** test deps needed for **`mvn verify`**. (`login.js` unchanged vs baseline.) | done |
| 2 | JDBC statistics + dashboard metrics | `DashboardServlet.java`, `StatisticsServlet.java`, `statistics.jsp`, `TechnicalDAO.java` (HTML escaping for user fields already in slice 1 JSPs). | done |
| 3 | Deploy descriptor / session cookies | `WEB-INF/web.xml`, root `web.xml`. | done |
| 4 | Admin users (verification) | Already delivered in slice 1 (`UserServlet` + admin `users.jsp`). | bundled |
| 5 | Landing / servlet entry | `index.jsp` (sidebar servlet links already in ported JSPs from slice 1). | done |
| 6 | Build hygiene | `.gitignore` from reference; `git rm -r --cached target` removes tracked WAR/classes. | done |
| 7 | Tooling / docs | `README.md`, `scripts/*`, `.env.local.example`, migration SQL; `seed.sql`, `DBConnection.java`; servlet/DAO IT & controller tests, H2 SQL fixtures (`src/test/resources`). Three commits landed. | done |
