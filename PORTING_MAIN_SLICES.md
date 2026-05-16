# Robustness ports onto `main` (slice manifest)

Baseline: **`origin/main`** at start of port. Source snapshots: **`am`** merged tip (TrackHire hardening).

Verification each slice: **`mvn clean verify`**.

Commit messages stay short and do not mention other branch names.

| Slice | Commit theme | Paths (from `am` unless noted) | Status |
|------|----------------|--------------------------------|--------|
| 1 | CSRF / POST + servlet validation | Utilities `CsrfUtil`, `HtmlUtil`, `ResetTokenUtil` + tests; servlets listed in plan (all POST paths); DAO/model support: `ApplicationDAO`, `InterviewDAO`, `UserDAO`, `TagDAO`, `Application`, `User`; related JSPs; **`pom.xml`** test deps needed for **`mvn verify`**. (`login.js` unchanged vs baseline.) | done |
| 2 | JDBC statistics page | `DashboardServlet.java`, `StatisticsServlet.java`, `statistics.jsp` | pending |
| 3 | Deploy descriptor / session cookies | `src/main/webapp/WEB-INF/web.xml`, `src/main/webapp/web.xml` | pending |
| 4 | Admin users (verification) | *No separate path delta if slice 1 included `users.jsp` + `UserServlet`* | N/A expected |
| 5 | Landing / servlet entry | `index.jsp` servlet-friendly links (from `am`) | pending |
| 6 | Build hygiene | `.gitignore`; remove tracked under `target/` if present | pending |
| 7 | Tooling / docs | `README.md`, `scripts/*`, `.env.local.example`, `docs/mysql-migration-*.sql`, `pom.xml`, `seed.sql`; models/DAOs/servlet/tests as needed for parity | pending |
