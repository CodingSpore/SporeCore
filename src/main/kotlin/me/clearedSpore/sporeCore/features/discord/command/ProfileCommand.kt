package me.clearedSpore.sporeCore.features.discord.command

import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.features.discord.`object`.DiscordCommand
import me.clearedSpore.sporeCore.features.discord.`object`.DiscordLinks
import me.clearedSpore.sporeCore.features.discord.`object`.DiscordOption
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.awt.Color

class ProfileCommand : DiscordCommand(
    "profile",
    "Checks a users profile"
) {

    override val options = listOf(
        DiscordOption(
            "user",
            "The user to check",
            required = true,
            permission = Permission.MODERATE_MEMBERS
        ),
    )
    override fun execute(event: SlashCommandInteractionEvent) {
        if (event.user.isBot) return
        if (!event.isFromGuild) return

        val user: Long

        try {
            user = event.getOption("user")!!.asLong
        } catch (_: NumberFormatException) {
            event.reply("Please provide a valid user ID.").setEphemeral(true).queue()
            return
        }

        if (event.guild!!.getMemberById(user) == null) {
            event.reply("Please provide a valid user ID.").setEphemeral(true).queue()
            return
        }

        if (!DiscordLinks.isLinked(user.toString())) {
            event.reply("That user is not linked in-game.").setEphemeral(true).queue()
            return
        }

        val playerName = DiscordLinks.get(user.toString())?.name
        val playerUUID = DiscordLinks.get(user.toString())?.uuid
        val player = DiscordLinks.get(user.toString())


        val embed = EmbedBuilder().apply {
            setColor(Color.lightGray)
            setTitle(playerName)
            setDescription(
                        "**Player**: $playerName\n" +
                        "**UUID**: $playerUUID\n" +
                        "**Linked User**: <@$user>"
                        )

        }

        event.reply(MessageCreateData.fromEmbeds(embed.build())).setEphemeral(true).queue()
    }
}