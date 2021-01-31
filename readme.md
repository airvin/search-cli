# Search CLI

This application is a simple command line application to search data and return
results in a readable format. The data relates to a ticket-tracking system,
where tickets correspond to an organization and user.

## Building the application

To build the application, use:

```
make build
```

This will build an executable called `search-cli` in the
`build/install/search-cli/bin` directory.

## Running the application

The `search-cli` executable can be run with:

```
make run
```

## Overview of application

This application is written in Kotlin and uses gradle as a build tool. The
entrypoint to the application (the `main` function) is in the
`src/main/kotlin/search/cli/App.kt` file.

## Requirements

- Search response times should not increase linearly as the number of documents
  grows
- The user should be able to search on any field
- The user should be able to search for empty values, e.g. where description is
  empty.
- Values from any related entities should be included in the results, i.e.
  searching organization by id should return its tickets and users.
- Only full value matching is allowed (e.g. "mar" won't return "mary").

## Assumptions

- The dataset can be feasibly stored in memory on a single machine
- The `_id` field of each entity is unique
- Entities are related as follows:
  - Each ticket must be associated with one organization
  - Organizations can have zero or more tickets
  - Each user must be associated with one organization
  - Organizations can have zero or more users
  - Tickets must be associated with one user through the `submitter_id`
  - Tickets may be associated with one user through the `assignee_id`
  - Tickets may be associated with one user through the `requester_id`

## Design Decisions

### Data structures

The requirement for sub-linear search time means that some data processing will
be required on application startup. Simply sorting the list of organizations,
users and tickets to allow for `log(n)` time is not a solution given that it is
a requirement to be able to search on any field. While it requires `O(n)`
processing on startup, indexing on each field for each data structure allows for
constant time searching.

### Application state machine

As an interactive CLI, there are various stages of the application with each
stage requiring different prompt text and permitting different user inputs. For
this reason, the application was modelled as a state machine. Each state
describes the prompt display and the permitted state transitions. The state
machine can be visualised as follows: ![SMD](state_machine_diagram.jpg)

### Extensibility

The data structures of Organizations, Users and Tickets are ones that may be
subject to change in the future. For example, new fields may be required or
fields may become obsolete. It is also possible that entirely new entity types
may need to be defined in future. Therefore, reflection has been used
extensively throughout this application to examine the properties of classes at
runtime without having to hardcode them. Examples of this can be seen in the
`loadFile()` function in `FileIo`, `createIndex()` function in `Indexes` and
`printSearchableFields()` function in `Search`.

While it was necessary in places to hardcode logic relating to the
Organizations, Users and Tickets, all efforts were made to keep this isolated to
specific areas of the code (for example, in describing the relationships between
the entities in the `Search` file). In all other places, the `EntityEnum` class
was used in order to be sure that all classes of entity were handled
appropriately.

### Error Handling

This codebase uses functional programming principles as much as possible. A
functional library for Kotlin, called [Arrow](https://arrow-kt.io/docs/core/) is
used for error handling with the `Either` type.

Conventions:

- Use immutable state.
- Catch exceptions as close as possible to their source and convert to [Arrow's
  `Either`
  type](https://arrow-kt.io/docs/apidocs/arrow-core-data/arrow.core/-either/).
- Implement functions as expressions. Functions that produce errors can be composed
  using `map`, `flatMap` and `fold`. Avoid statements with side effects in functions.
- Use recursion over loops.

Example Gists:

- [How to catch exceptions and convert to and Either type](https://gist.github.com/airvin/79f1fb2a3821a9e5d227db3ee9561f42).
- [Using flatMap to compose functions that return Eithers](https://gist.github.com/airvin/3bfae1f3e622e466ba9072b53684555a).

## Testing

To run the unit tests, use:

```
make test
```

## TODO

- Include `assignee_id` and `referrer_id` in the user-ticket relationship search
- Implement integration tests for the prompt
