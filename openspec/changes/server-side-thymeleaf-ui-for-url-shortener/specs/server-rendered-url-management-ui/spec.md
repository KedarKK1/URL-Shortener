## ADDED Requirements

### Requirement: Users can manage URLs from a web interface
The system SHALL provide a server-rendered web UI for creating, viewing, updating, and deleting URLs through Thymeleaf templates backed by the existing URL services.

#### Scenario: Create a URL from the form
- **WHEN** a user submits a valid create-url form
- **THEN** the system SHALL persist the URL and redirect the user to a confirmation or dashboard view with the new record shown

#### Scenario: Show validation errors
- **WHEN** a user submits an invalid form
- **THEN** the system SHALL return the form with validation messages and preserve the submitted values

#### Scenario: View dashboard and analytics
- **WHEN** a user opens the dashboard or analytics page
- **THEN** the system SHALL render the relevant URL data using server-rendered Thymeleaf templates and reusable fragments
