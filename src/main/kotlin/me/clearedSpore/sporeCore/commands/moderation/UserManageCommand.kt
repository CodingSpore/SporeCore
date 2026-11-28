package me.clearedSpore.sporeCore.commands.moderation

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.punishment.PunishmentService
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Permission
import kotlin.collections.contains

class UserManageCommand : SporeCommand() {


    @Subcommand("usermanage bypassipbans <player>")
    @Permission("*")
    fun onSetIPBypass(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""
        var target = Bukkit.getOfflinePlayer(targetName)
        var targetSuffix = if (Bukkit.getOnlinePlayers().contains(target.player)) chatService?.getPlayerSuffix(target.player)?.legacyToMiniMessage() ?: "" else ""
        var user = UserManager.getOrCreate(target.uniqueId, targetName)

        if (user.isIPBanExempt) {
            user.isIPBanExempt = false
            sender.sendMessage("You have un-exempted ${target.name} from IP Bans!".blue())
            user.save()
            Logger.log(suffix, sender, Perm.LOG, "un-exempted ${target.name} from IP Bans", false)
        } else {
            user.isIPBanExempt = true
            sender.sendMessage("You have exempted ${target.name} from IP Bans!".blue())
            user.save()
            Logger.log(suffix, sender, Perm.LOG, "exempted ${target.name} from IP Bans", false)
        }
    }
}