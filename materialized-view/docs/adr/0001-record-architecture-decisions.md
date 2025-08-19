# 0001 — Record architecture decisions

Status: Accepted
Date: 2025-08-19

Context
- We need a lightweight, versioned way to capture and communicate key technical decisions.

Decision
- Use Architecture Decision Records (ADRs) stored under docs/adr.
- One ADR per decision, sequentially numbered, Markdown format.
- Each ADR includes: Status, Context, Decision, Consequences, and References to code where applicable.

Consequences
- Decisions are transparent and reviewable in PRs.
- New contributors can understand why the system is shaped as it is.

Implementation Notes
- This ADR establishes the practice and index location: docs/adr.

