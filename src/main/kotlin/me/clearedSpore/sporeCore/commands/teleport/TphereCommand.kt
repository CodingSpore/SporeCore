package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.logs.LogsService
import me.clearedSpore.sporeCore.features.logs.`object`.LogType
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class TphereCommand : SporeCommand() {

    @Default
    @Command("tphere <targets>")
    @Command("teleporthere <targets>")
    @Command("bring <targets>")
    @Permission(Perm.TELEPORT_OTHERS)
    fun onTphere(sender: Player, @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {
        var suffix = chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: ""
        var targetSuffix = ""

        if (targets == null) {
            sender.gameMode = GameMode.SPECTATOR
            return
        }

        if (!sender.hasPermission(Perm.GAMEMODE_OTHERS)) {
            sender.sendErrorMessage("You don't have permission to set the gamemodes of others")
            return
        }

        val players = targets.values()

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }

        if (players.size == 1) {
            if (SporeCore.instance.coreConfig.logs.teleports) {
                targetSuffix = chatService?.getPlayerSuffix(players.first().player)?.legacyToMiniMessage() ?: ""
                LogsService.addLog(sender.name, "Teleported $targetSuffix${players.first().name}&r to them", LogType.TELEPORT)
            }
            players.first().teleportAsync(sender.location)
            Logger.log(suffix, sender, Perm.LOG, "teleported $targetSuffix${players.first().name}&r to them", false)
            sender.sendSuccessMessage("Teleported ${players.first().name} to you.")
        } else {
            if (SporeCore.instance.coreConfig.logs.teleports) {
                LogsService.addLog(sender.name, "Teleported ${players.size} players to them", LogType.TELEPORT)
            }
            players.forEach { it.teleportAsync(sender.location) }
            Logger.log(suffix, sender, Perm.LOG, "teleported ${players.size} players to them", false)
            sender.sendSuccessMessage("Teleported ${players.size} players to you.")
        }

    }
}
