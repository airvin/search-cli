# Search CLI

This application is a simple command line application to search data and return results in a readable format.
The data relates to a ticket-tracking system, where tickets correspond to an organization and user.

## Building the application

To build the application, use:

```
make build
```

This will build an executable called `search-cli` in the `build/install/search-cli/bin` directory.

## Running the application

The `search-cli` executable can be run with:

```
./build/install/search-cli/bin/search-cli <query>
```

## Overview of application

This application is written in Kotlin and uses gradle as a build tool.
The entrypoint to the application (the `main` function) is in the `src/main/kotlin/search/cli/App.kt` file.

## Requirements

- Search response times should not increase linearly as the number of documents grows
- The user should be able to search on any field
- The user should be able to search for empty values, e.g. where description is empty.
- Values from any related entities should be included in the results, i.e. searching organization by id should return its tickets and users.
- Only full value matching is allowed (e.g. "mar" won't return "mary").

## Assumptions

- The dataset can be feasibly stored in memory on a single machine
- Entities are related as follows:
  - Each ticket must be associated with one organization
  - Organizations can have zero or more tickets
  - Each user must be associated with one organization
  - Organizations can have zero or more users
  - Tickets must be associated with one user through the `submitter_id`
  - Tickets may be associated with one user through the `assignee_id`
  - Tickets may be associated with one user through the `requester_id`

## Design Decisions

The requirement for sub-linear search time means that some data processing will be required on application startup.
Simply sorting the list of organizations, users and tickets to allow for `log(n)` time is not a solution given that
it is a requirement to be able to search on any field. While it requires `O(n)` processing on startup, indexing on each
field for each data structure allows for constant time searching.

### Extensibility

The data structures of Organizations, Users and Tickets are ones that may be subject to change in the future.
For example, new fields may be required or fields may become obsolete.
It is also possible that entirely new entity types may need to be defined in future.
Therefore, care has been taken to avoid hardcoding properties of these data structures wherever possible.

## Testing

To run the unit tests, use:

```
make test
```

## TODO

- State machine for prompt options
- Load the list of organizations, users and tickets from file
- Create the maps for each field
- Implement search
