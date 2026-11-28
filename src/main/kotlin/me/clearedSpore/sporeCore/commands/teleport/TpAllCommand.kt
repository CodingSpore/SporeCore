package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.extension.PlayerExtension.uuidStr
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.logs.LogsService
import me.clearedSpore.sporeCore.features.logs.`object`.LogType
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@RegisterCommand
class TpAllCommand : SporeCommand() {

    @Command("tpall")
    @Command("teleportall")
    @Permission(Perm.TELEPORT_ALL)
    fun onTpAll(sender: Player) {
        var suffix = chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: ""
        Bukkit.getOnlinePlayers().forEach {
            if (it != sender) it.teleportAsync(sender.location)
        }

        val amount = Bukkit.getOnlinePlayers().size - 1

        for (player in Bukkit.getOnlinePlayers()) {
            player.teleportAsync(sender.location)
        }

        Logger.log(suffix, sender, Perm.LOG, "teleported $amount players themself", true)
        if (SporeCore.instance.coreConfig.logs.teleports) {
            LogsService.addLog(sender.uuidStr(), "Teleported $amount players to them", LogType.TELEPORT)
        }
        sender.sendSuccessMessage("Teleported everyone to you.")
    }
}
