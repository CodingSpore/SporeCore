package me.clearedSpore.sporeCore.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.command.SporeCommand
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@CommandAlias("invulnerable|god")
@CommandPermission(Perm.GOD)
@RegisterCommand
class GodCommand : SporeCommand() {

    @Command("god [target]")
    @Command("invulnerable [target]")
    @Permission(Perm.GOD)
    fun onGodMode(sender: CommandSender, @Argument("target", suggestions = "players") target: Player?) {
        var suffix = chatService?.getPlayerSuffix(sender as Player)?.legacyToMiniMessage() ?: ""
        var player = sender as Player

        if (target == null) {
            player.isInvulnerable = !player.isInvulnerable
            player.sendMessage(
                "Your godmode has been ${if (player.isInvulnerable) "enabled" else "disabled"}.".blue()
            )
            Logger.log(
                suffix,
                sender,
                Perm.MODE_ALLOW,
                "has ${if (player.isInvulnerable) "enabled" else "disabled"} godmode",
                false
            )
            return
        }
        var targetSuffix = chatService?.getPlayerSuffix(target as Player)?.legacyToMiniMessage() ?: ""

        sender.sendMessage("GodMode ${if (target.isInvulnerable) "enabled" else "disabled"} for $targetSuffix${target.player}".blue() + ".".blue())
        Logger.log(
            suffix,
            sender,
            Perm.MODE_ALLOW,
            "has ${if (target.isInvulnerable) "enabled" else "disabled"} godmode for $targetSuffix${target.name}",
            false
        )
    }
}
