package me.clearedSpore.sporeCore.features.discord.`object`

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

abstract class DiscordCommand(val name: String, val description: String) {

    abstract fun execute(event: SlashCommandInteractionEvent)

    open val options: List<DiscordOption> = emptyList()
}

data class DiscordOption(
    val name: String,
    val description: String,
    val permission: Permission,
    val required: Boolean = false
)
