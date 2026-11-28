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
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class CreativeCommand : SporeCommand() {

    @Command("creative [targets]")
    @Command("gmc [targets]")
    @Permission(Perm.CREATIVE)
    fun onCreative(sender: CommandSender, @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""

        if (targets == null) {
            check(sender is Player) { "You must specify a target." }
            sender.gameMode = GameMode.CREATIVE
            sender.sendMessage("Your gamemode has been updated to Creative".blue())
            Logger.log(
                suffix,
                sender,
                Perm.LOG,
                "&rchanged their gamemode to Creative",
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
            target.gameMode = GameMode.CREATIVE
            val targetSuffix = chatService?.getPlayerSuffix(target)?.translate() ?: ""
            if (targets.values().size == 1) {
                Logger.log(suffix, sender, Perm.LOG, "changed $targetSuffix${target.name}&r&f’s gamemode to Creative", false)
                sender.sendMessage("You updated ".blue() + targetSuffix + target.name + "’s gamemode to Creative.".blue())
                target.player?.sendMessage("Your gamemode has been updated to Creative".blue())
                return
            } else {
                target.player?.sendMessage("Your gamemode has been updated to Creative".blue())
            }
        }
        Logger.log(suffix, sender, Perm.LOG, "changed everyone’s gamemode to Creative", false)
        sender.sendMessage("You updated everyone’s gamemode to Creative.".blue())
    }
}
