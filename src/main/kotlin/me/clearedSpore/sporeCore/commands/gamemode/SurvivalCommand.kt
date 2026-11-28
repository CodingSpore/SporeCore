package me.clearedSpore.sporeCore.commands.gamemode

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class SurvivalCommand : SporeCommand() {

    @Command("survival [targets]")
    @Command("gms [targets]")
    @Permission(Perm.SURVIVAL)
    fun onSurvival(sender: CommandSender,  @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""

        if (targets == null) {
            check(sender is Player) { "You must specify a target." }
            sender.gameMode = GameMode.SURVIVAL
            sender.sendMessage("Your gamemode has been updated to Survival".blue())
            Logger.log(
                suffix,
                sender,
                Perm.LOG,
                "&rchanged their gamemode to Survival",
                false
            )
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

        players.forEach { target ->
            target.gameMode = GameMode.SURVIVAL
            val targetSuffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""
            if (targets.values().size == 1) {
                Logger.log(suffix, sender, Perm.LOG, "changed $targetSuffix${target.name}&r&f’s gamemode to Survival", false)
                sender.sendMessage("You updated ".blue() + targetSuffix + target.name + "’s gamemode to Survival.".blue())
                target.player?.sendMessage("Your gamemode has been updated to Survival".blue())
                return
            } else {
                target.player?.sendMessage("Your gamemode has been updated to Survival".blue())
            }
        }
        Logger.log(suffix, sender, Perm.LOG, "changed everyone’s gamemode to Survival", false)
        sender.sendMessage("You updated everyone’s gamemode to Survival.".blue())
    }
}
