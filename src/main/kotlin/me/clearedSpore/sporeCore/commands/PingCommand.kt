package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.contexts.OnlinePlayer
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.orange
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command

@RegisterCommand
class PingCommand : SporeCommand() {


    @Command("ping [player]")
    fun onPing(sender: Player, @Argument("player", suggestions = "players") onlineTarget: Player?) {
        val target = onlineTarget?.player ?: sender
        val targetSuffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""
        val ping = target.ping

        val formattedPing = when {
            ping < 90 -> "$ping".green()
            ping < 120 -> "&e$ping"
            ping < 150 -> "$ping".orange()
            else -> "$ping".red()
        }

        if (target == sender) {
            sender.sendMessage("Your ping is $formattedPing".blue())
        } else {
            sender.sendMessage("$targetSuffix${target.name}" + "'s ping is $formattedPing".blue())
        }
    }
}