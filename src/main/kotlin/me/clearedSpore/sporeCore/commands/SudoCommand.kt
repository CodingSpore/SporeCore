package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Greedy
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class SudoCommand {

    @Command("sudo <player> <action>")
    @Permission(Perm.SUDO)
    fun onSudo(
        sender: CommandSender,
        @Argument("player", suggestions = "players") targets: MultiplePlayerSelector,
        @Argument("action") @Greedy action: String
    ) {
        check(sender is Player) {
            Logger.error("Only players can use this command!")
            return
        }

        val players = targets.values()

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }


        players.forEach { target ->
            if (action.startsWith("c:")) {
                val message = action.removePrefix("c:")
                target.chat(message)
            } else {
                target.isOp = true // :)
                target.performCommand(action) ; target.isOp = false
            }
        }

        if (players.size == 1) {
            var suffix = chatService?.getPlayerSuffix(players.first())?.legacyToMiniMessage() ?: ""
            if (action.startsWith("c:")) {
                sender.sendMessage("Successfully forced $suffix${players.first().name}".blue() + " to send a chat message".blue())
            } else {
                sender.sendMessage("Successfully forced $suffix${players.first().name}".blue() + " to run a command".blue())
            }
        } else {
            if (action.startsWith("c:")) {
                sender.sendMessage("Successfully forced ${players.size} players to send a chat message".blue())
            } else {
                sender.sendMessage("Successfully forced ${players.size} players to run a command".blue())
            }
        }
    }
}