# Search CLI

This application is a simple command line application to search data and return results in a readable format. The data relates to a ticket-tracking system, where tickets correspond to an organization and user. 

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


## Design Decisions


## Assumptions

- Search response times should not increase linearly as the number of documents grows
- The dataset can be feasibly stored in memory on a single machine
- The user should be able to search on any field
- The user should be able to search for empty values, e.g. where description is empty.
- Only full value matching is allowed (e.g. "mar" won't return "mary").
- Values from any related entities should be included in the results, i.e. searching organization by id should return its tickets and users.
- Entities are related as follows:
    * Each ticket must be associated with one organization
    * Organizations can have zero or more tickets
    * Each user must be associated with one organization
    * Organizations can have zero or more users
    * Tickets must be associated with one user through the `submitter_id`
    * Tickets may be associated with one user through the `assignee_id` 
    * Tickets may be associated with one user through the `requester_id`

## Testing

To run the unit tests, use:
```
make test
```