# Search CLI

This application is a simple command line application to search data and return results in a readable format. The data relates to a ticket-tracking system, where tickets correspond to an organisation and user. 


## Overview of application

This application is written in Kotlin and uses gradle as a build tool. 
The entrypoint to the application (the `main` function) is in the `src/main/kotlin/search/cli/App.kt` file.


## Design Decisions


## Assumptions

- The dataset can be feasibly stored in memory on a single machine
- The user should be able to search on any field
- The user should be able to search for empty values, e.g. where description is empty.
- Only full value matching is allowed (e.g. "mar" won't return "mary").
- Values from any related entities should be included in the results, i.e. searching organization by id should return its tickets and users.
- Entities are related as follows:
    * Each ticket must be associated with one organisation
    * Organizations can have zero or more tickets
    * Each user must be associated with one organization
    * Organizations can have zero or more users
    * Tickets must be associated with one user through the `submitter_id`
    * Tickets may be associate with one user through the `assignee_id` 
