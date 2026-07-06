## ADDED Requirements

### Requirement: Create Short URL
The system SHALL allow users to create a short URL by providing a target URL, an optional custom alias, and an optional expiration time.

#### Scenario: Successful URL creation
- **WHEN** a user submits a valid absolute target URL with no custom alias
- **THEN** the system SHALL generate a unique random alias, persist the URL metadata, and return the created URL details

#### Scenario: Validation error for invalid target URL
- **WHEN** a user submits an invalid URL format
- **THEN** the system SHALL reject the request with a validation error

### Requirement: Retrieve URL Details
The system SHALL support retrieving the metadata of a shortened URL using its unique ID.

#### Scenario: Successful metadata retrieval
- **WHEN** a user requests the metadata using a valid, existing URL ID
- **THEN** the system SHALL return the URL alias, target URL, click count, status, and creation timestamps

### Requirement: Update Short URL
The system SHALL allow updating the target URL, alias, or expiration time of an existing shortened URL.

#### Scenario: Successful update
- **WHEN** a user submits valid update parameters for an existing URL ID
- **THEN** the system SHALL update the metadata in the database and clear any stale caches

### Requirement: Delete Short URL
The system SHALL allow deleting a shortened URL.

#### Scenario: Successful deletion
- **WHEN** a user deletes an existing URL by ID
- **THEN** the system SHALL remove the URL from the database and evict any associated caches
