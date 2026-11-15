# Backend-Application

Kinemo REST API built with Spring Boot, Java 21 and Spring Data JPA.

## BC05 · Observation and Crisis History

Base path: `/api/v1/children/{childId}/episodes`.

- `GET /` lists the child's episodes, newest first. Use `page` (default 0) and `size` (default 25, up to 100).
- `GET /{id}` returns an episode and its professional comments.
- `POST /` records an observation or crisis. Send a description and an ISO-8601 occurrence time. For a crisis, include `crisisSeverity` (`mild`, `moderate`, or `severe`), with optional `durationMinutes`, `trigger`, and `helped`. An optional `alertId` must refer to a closed alert for that child.
- `POST /{id}/comments` adds a professional comment from a JSON body containing `text`.
- `PUT /{id}/review` records the first review. Repeating the request preserves its original reviewer and date.

The API takes its account ID from the authenticated request principal. Integrate IAM and provide a `HistoryAccess` bean backed by family ownership, active caregiver authorization and assigned psychologists. Furnish a `ClosedAlertLookup` bean backed by BC03 to record closed alerts. Without those integrations, permission checks and linked-alert validation fail closed. Before using the production profile, provision the JPA schema for `history_episodes` and `history_professional_comments`, since that profile validates the schema.

Run `mvn test` to verify the API, authorization rules and persistence with an isolated H2 database. The tests provide substitutes for the access and alert lookups.
