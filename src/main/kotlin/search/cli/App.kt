package search.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.output.TermUi

class App : CliktCommand() {

    val initialOption = TermUi.prompt(PromptOptions.INTRODUCTION) {
        it.toIntOrNull() ?: throw UsageError(PromptOptions.INTRO_ERROR)
    }

    override fun run() {
        val entitySelection = if (initialOption == 1) {
            TermUi.prompt(PromptOptions.ENTITY_SELECTION) {
                it.toIntOrNull() ?: throw UsageError(PromptOptions.ENTITY_SELECTION)
            }
        } else {
            TermUi.prompt(PromptOptions.ENTITY_TYPES.map { printSearchableFields(it) }
                    .reduce {acc, it -> acc + it} + "\n" + PromptOptions.ENTITY_SELECTION)
        }

        TermUi.prompt(PromptOptions.ENTER_OR_VIEW_FIELDS + entitySelection)

        println("Searching for $entitySelection...")
    }
}

fun main(args: Array<String>) = App().main(args)

