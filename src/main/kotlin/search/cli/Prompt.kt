package search.cli

import com.github.ajalt.clikt.output.TermUi

/**
 * The Prompt class is used to model the applications state machine.
 *
 * This is done through a set of functions, that take the output of the previous function,
 * prompt the user for input, then pass that information on to the next function, determined by
 * the user input.
 *
 * This class contains the entity maps and the entity index maps to pass on to the search and
 * display functions when the user has completed their input.
 *
 * @param organizations     The organization map containing all organizations loaded into the application.
 *                          The org's id is the key with user Organization object as the value.
 * @param users             The user map containing all organizations loaded into the application.
 *                          The user's id is the key with the User object as the value.
 * @param tickets           The ticket map containing all tickets loaded into the application.
 *                          The ticket's id is the key with the Ticket object as the value.
 * @param organizationIndex The index map of organizations. The outer map contains the Organization
 *                          properties as the key with an inner map containing all values for that property
 *                          as keys and the list of organization ids for the value.
 * @param userIndex         The index map of users. The outer map contains the User
 *                          properties as the key with an inner map containing all values for that property
 *                          as keys and the list of user ids for the value.
 * @param ticketIndex       The index map of tickets. The outer map contains the Ticket
 *                          properties as the key with an inner map containing all values for that property
 *                          as keys and the list of ticket ids for the value.
 */
class Prompt(
        val organizations: Map<String, Organization>,
        val users: Map<String, User>,
        val tickets: Map<String, Ticket>,
        val organizationIndex: Map<String, MutableMap<String, MutableList<String>>>,
        val userIndex: Map<String, MutableMap<String, MutableList<String>>>,
        val ticketIndex: Map<String, MutableMap<String, MutableList<String>>>
) {

    /**
     * init is the entrypoint for the prompt state machine.
     *
     * This function starts the user at the welcome display.
     */
    fun init() {
        val displaySearchableFields = getDisplaySearchableFields()
        val entitySelection = getEntitySelection(displaySearchableFields)
        handleSearchOptions(entitySelection)
    }

    /**
     * handleSearchOptions is the midpoint of the prompt state machine, after the
     * user has selected the entity type to search.
     *
     * Once the users have finished their search, they can choose to restart from this point
     * or back at the initial display.
     *
     * @param entity    An EntityEnum representing the entity type the user has selected to search.
     *                  For example, ORGANIZATION.
     */
    fun handleSearchOptions(entity: EntityEnum) {
        val searchTerm = getSearchTerm(entity.className)
        val searchValue = getSearchValue(entity.className, searchTerm)

        // After the user has selected an entity, search term and search value, trigger the search function
        val matchingEntities: List<Entity> = when(entity) {
            EntityEnum.ORGANIZATION -> search(searchTerm, searchValue, organizationIndex, organizations)
            EntityEnum.USER -> search(searchTerm, searchValue, userIndex, users)
            EntityEnum.TICKET -> search(searchTerm, searchValue, ticketIndex, tickets)
        }

        // Find the entities that relate to the matched entities and display the results in the prompt.
        when(entity) {
            EntityEnum.ORGANIZATION -> {
                val relatedEntities = findEntitiesRelatedToOrgs(
                        matchingEntities as List<Organization>,
                        userIndex,
                        ticketIndex,
                        users,
                        tickets)
                prettyPrintResults(matchingEntities, relatedEntities, EntityEnum.ORGANIZATION, searchTerm, searchValue)
            }
            EntityEnum.USER -> {
                val relatedEntities = findEntitiesRelatedToUsers(
                        matchingEntities as List<User>,
                        ticketIndex,
                        organizations,
                        tickets)
                prettyPrintResults(matchingEntities, relatedEntities, EntityEnum.USER, searchTerm, searchValue)
            }
            EntityEnum.TICKET -> {
                val relatedEntities = findEntitiesRelatedToTickets(
                        matchingEntities as List<Ticket>,
                        organizations,
                        users)
                prettyPrintResults(matchingEntities, relatedEntities, EntityEnum.TICKET, searchTerm, searchValue)
            }
        }

        // Guide the user into restarting back at the initial display or at the search entity display.
        val restartFromStart = getRestartFromStart(entity.className)
        if (restartFromStart) init() else handleSearchOptions(entity)
    }
}

/**
 * getDisplaySearchableFields is used to get the user's selection for whether or not they want to
 * see the full list of entities and properties to search, or just the list of entities.
 *
 * @return      Returns true if the user has opted to see all searchable fields or false
 *              if the user has opted to move on to the select entity screen.
 */
fun getDisplaySearchableFields(): Boolean {
    val introText = "\nWelcome to Zendesk Search!\n" +
            "Type 'ctrl+c' at any time to exit\n\n" +
            "       Select search option:\n" +
            "       * Press 1 to search Zendesk\n" +
            "       * Press any other key to view searchable fields\n\n"

    val initialSelection = TermUi.prompt(introText, default = "", showDefault = false)

    // Returns true if the user has entered anything other than "1"
    return (initialSelection == null || initialSelection.toIntOrNull() == null || initialSelection.toInt() != 1)
}

/**
 * getEntitySelection allows the user to select an entity type they want to search.
 *
 * This function uses the entity types defined in the EnityEnum. Therefore, no changes are required to this
 * function if new entity types are added to the application.
 *
 * @param displayFields     A Boolean that represents whether the user has opted for all searchable fields of
 *                          all the entity types to be displayed before showing the entity options.
 * @return      Returns an EntityEnum representing the entity type the user wishes to search.
 */
fun getEntitySelection(displayFields: Boolean): EntityEnum {
    // Get the list of entity types from the EntityEnum class and reduce to a single string as a numbered list
    val entitySelectionText = "\nSelect entity to search \n${EntityEnum.values.mapIndexed {i, it -> "${i+1}) $it\n"}
            .reduce { acc, it -> acc + it }}"

    // Get the searchable fields for each entity if the user has chosen to see the field options
    val entityFields = if (displayFields) {
        EntityEnum.values.map { getSearchableFields(it) }
                .reduce {acc, it -> acc + it} + "\n"
    } else ""

    // Prompt the user to select an entity type
    val entitySelection = TermUi.prompt(entityFields + entitySelectionText)

    // If the user entered anything other than a number that corresponds to an entity, trigger a warning message
    // and recursively call the function to display the options again. Otherwise, return the entity selected.
    return if (entitySelection == null
            || entitySelection.toIntOrNull() == null
            || entitySelection.toInt() > EntityEnum.values().size
            || EntityEnum.getByInt(entitySelection.toInt()) == null
    ) {
        println("\n${PromptDisplay.ansiRed}$entitySelection is an invalid choice${PromptDisplay.ansiReset}")
        getEntitySelection(false)
    } else {
        EntityEnum.getByInt(entitySelection.toInt())!!
    }
}

/**
 * getSearchTerm prompts the user to select one of the entity's properties to search over.
 *
 * @param entity            A string representing the selected entity (e.g. "Organization")
 * @param displayFields     A boolean representing whether or not the searchable fields for that entity
 *                          should be displayed.
 * @return      Returns a string representing the property of the entity the user wishes to search (e.g. "url").
 */
fun getSearchTerm(entity: String, displayFields: Boolean = false): String {
    val entityFields = if (displayFields) getSearchableFields(entity) else ""
    val searchTerm = TermUi.prompt("${entityFields}\nEnter search term for $entity or press enter to view searchable fields","")

    // If entered text is not a valid search term, display a warning and recursively call the function to display the
    // valid search term options and prompt the user for input.
    return if (searchTerm == null || (searchTerm.isNotEmpty() && !isValidSearchTerm(searchTerm, entity))) {
        println("\n${PromptDisplay.ansiRed}$searchTerm is an invalid choice${PromptDisplay.ansiReset}")
        getSearchTerm(entity, true)

    // If the user has pressed enter, don't display the warning but recursively call the function to display search
    // terms and prompt.
    } else if (searchTerm.isEmpty()) {
        getSearchTerm(entity, displayFields = true)

    // If the search term is valid, return it
    } else {
        searchTerm
    }
}

/**
 * getSearchValue prompts the user to enter the value to search for.
 *
 * The user can search for an empty field by pressing enter without entering any other text. In this case, the
 * default "NULL_OR_EMPTY" search value is returned.
 *
 * @param entity         A string representing the entity type the user has selected to search (e.g. "Organization")
 * @param searchTerm     A string representing a property of that entity the user wishes to search (e.g. "url")
 *
 * @return      Returns the value the user wishes to search that entity's property for.
 */
fun getSearchValue(entity: String, searchTerm: String): String {
    val searchValue = TermUi.prompt("\nEnter search value for $entity $searchTerm", default = "NULL_OR_EMPTY", showDefault = false)
    return if (searchValue.isNullOrEmpty()) getSearchValue(entity, searchTerm) else searchValue
}

/**
 * getRestartFromStart allows the user to choose whether to restart the prompt back at the initial welcome display or
 * search the entity again.
 *
 * @param entity    A string representing the previously selected entity type (e.g. "Organization")
 * @return          Returns true if the user has opted to restart from the start, or false if they
 *                  have chosen to search the entity again.
 */
fun getRestartFromStart(entity: String): Boolean {
    val resetSelection = TermUi.prompt(
            "Enter 1 to search $entity again, or any other key to restart from the start",
            default = "",
            showDefault = false
    )
    return (resetSelection == null || resetSelection.toIntOrNull() == null || resetSelection.toInt() != 1)
}
