package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class FlyCommand : SporeCommand() {

    @Command("fly [targets]")
    @Command("togglefly [targets]")
    @Permission(Perm.FLIGHT)
    fun onFly(sender: CommandSender, @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {

        if (targets == null) {
            check(sender is Player) { "You must specify a target." }
            sender.allowFlight = !sender.allowFlight
            sender.sendMessage("Your flight has been ${if (sender.allowFlight) "enabled" else "disabled"}.".blue())
            return
        }

        val players = targets.values()

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }

        players.forEach {
            it.allowFlight = !it.allowFlight
            it.sendMessage(
                "Your flight has been ${if (it.allowFlight) "enabled" else "disabled"}.".blue()
            )
        }

        val suffix = chatService?.getPlayerSuffix(players.first().player)?.legacyToMiniMessage() ?: ""

        sender.sendMessage(
            if (players.size == 1 && players.first().name != sender.name)
                "Flight toggled for $suffix${players.first().name}".translate() + ".".blue()
            else
                "Flight toggled for $suffix${players.size}".translate() + " players.".blue()
        )
    }
}
