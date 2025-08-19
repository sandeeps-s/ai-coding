# 0006 — Security and access control

Status: Accepted
Date: 2025-08-19

Context
- The service exposes HTTP APIs and actuator endpoints, requiring authentication and role-based access control.

Decision
- Use Spring Security for authentication and authorization.
- In-memory user store for local development/testing, with roles USER and ADMIN.
- HTTP Basic authentication for API and actuator endpoints.
- Permit unauthenticated access to health, info, and Prometheus metrics endpoints; require authentication for all other API endpoints.
- Method-level security enabled for future extensibility.

Evidence
- Security config: boot/src/main/kotlin/.../security/SecurityConfig.kt
- Actuator endpoint rules: SecurityConfig.kt

Consequences
- Pros: Secure by default, easy to extend for external identity providers.
- Cons: In-memory user store not suitable for production; must be replaced for real deployments.

Related
- 0005 — Observability and health

