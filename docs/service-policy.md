# Service Policy

## Confirmed Policies

- New spaces are created with adminStatus=PENDING and isActive=true.
- Space deletion is soft deletion by setting deletedAt and isActive=false, then deleting related image and schedule rows.
- Only the host owner may update or delete a space or mutate schedules.
- Public space lookup filters by deletedAt only in visible repository calls.
- Space images are stored as image URL rows by spaceId.
- Schedule creation defaults isBookable to true.
- Schedule startTime and endTime are required and startTime must be before endTime.
- Schedule update preserves omitted values and validates the resulting time range.

## Validation Rules

Visible validation rules are documented from DTO annotations, entity methods, and service methods only. Any validation behavior not present in code is Needs confirmation.

## Authorization Rules

JWT stateless security is configured. GET /spaces, GET /spaces/*, and GET /spaces/*/schedule are permitAll; create/update/delete and owner schedule mutations are authenticated. The matcher order may make GET /spaces/me match the public /spaces/* rule; this requires confirmation/testing.

## Creation Policy

Creation behavior is documented only where visible in service or entity factory methods.

## Update Policy

Update behavior is documented only where visible in service or entity update methods.

## Deletion Policy

Deletion behavior is documented only where visible in service or entity delete methods.

## State Transition Rules

State transitions are documented only where visible in entity or service methods. Missing transitions are Needs confirmation.

## Exception Cases

The service throws IllegalArgumentException and SecurityException directly. No @ControllerAdvice or global exception response mapper is visible. HTTP status mapping for these exceptions is Needs confirmation unless explicitly handled in code.

## API Behavior Policy

- API behavior must be documented in `API_SPEC.yaml`.
- If API behavior changes, `API_SPEC.yaml` must be updated in the same PR.

## Needs Confirmation

- Admin approval workflow is not exposed by visible APIs.
- Whether public lists should filter only APPROVED spaces is Needs confirmation.
- Schedule overlap and booking conflict policy are not visible.
- Image upload/storage integration is not visible; only image URLs are persisted.
- Security matcher behavior for GET /spaces/me needs confirmation.
