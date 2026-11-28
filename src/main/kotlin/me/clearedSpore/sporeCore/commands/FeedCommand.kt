package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector
import javax.annotation.Nullable

@RegisterCommand
class FeedCommand : SporeCommand() {

    @Command("feed [targets]")
    @Permission(Perm.FEED)
    fun onFeed(sender: CommandSender, @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""

        if (targets == null) {
            check(sender is Player) { Logger.error("You must specify a target.") }
            sender.sendSuccessMessage("You have been fed!")
            sender.foodLevel = 20 ; sender.saturation = 20f
            return
        }

        if (!sender.hasPermission(Perm.FEED_OTHERS)) {
            sender.sendErrorMessage("You don't have permission to feed other players")
            return
        }

        val players = targets.values()

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }

        players.forEach {
            it.foodLevel = 20 ; it.saturation = 20f
        }

        sender.sendMessage(
            if (players.size == 1)
                "You fed ${players.first().name}.".blue()
            else
                "You fed ${players.size} players.".blue()
        )

        Logger.log(
            suffix,
            sender,
            Perm.LOG,
            "fed ${players.size} player(s)",
            false
        )
    }
}
