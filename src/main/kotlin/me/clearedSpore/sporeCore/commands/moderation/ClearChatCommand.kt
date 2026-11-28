package me.clearedSpore.sporeCore.commands.moderation

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.punishment.PunishmentService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

class ClearChatCommand : SporeCommand() {


    @Command("clearchat")
    @Permission(Perm.CLEAR_CHAT)
    fun onClear(sender: CommandSender) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""
        val lines = PunishmentService.config.settings.clearLines

        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(Perm.CLEAR_CHAT_BYPASS)) {
                repeat(lines) {
                    player.sendMessage(" ")
                }
            }
        }

        Logger.log(suffix, sender, Perm.LOG, "cleared the chat", true)
    }
}