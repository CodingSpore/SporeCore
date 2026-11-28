package me.clearedSpore.sporeCore.commands.gamemode

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.StringUtil.capitalizeFirstLetter
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class GamemodeCommand : SporeCommand() {

    @Command("gamemode <gamemode> [targets]")
    @Command("gm <gamemode> [targets]")
    @Permission(Perm.GAMEMODE)
    fun onGamemode(
        sender: CommandSender,
        @Argument("gamemode", suggestions = "gamemodes") gamemode: String,
        @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?
    ) {
        val suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.translate() ?: "" else ""

        val mode = try {
            GameMode.valueOf(gamemode.uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("Invalid gamemode! Use creative, survival, spectator, or adventure.".red())
            return
        }

        val permission = when (mode) {
            GameMode.CREATIVE -> Perm.CREATIVE
            GameMode.SURVIVAL -> Perm.SURVIVAL
            GameMode.SPECTATOR -> Perm.SPECTATOR
            GameMode.ADVENTURE -> Perm.ADVENTURE
        }

        if (!sender.hasPermission(permission)) {
            sender.sendMessage("You do not have permission to switch to ${mode.name.lowercase().capitalizeFirstLetter()}!".red())
            return
        }

        if (targets == null) {
            check(sender is Player) { "You must specify a target." }
            sender.gameMode = mode
            sender.sendMessage("Your gamemode has been updated to ${mode.name.lowercase().capitalizeFirstLetter()}".blue())
            Logger.log(
                suffix,
                sender,
                Perm.LOG,
                "changed their gamemode to ${mode.name.capitalizeFirstLetter()}",
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
            target.gameMode = mode
            if (players.size == 1) {
                val targetSuffix = if (Bukkit.getOnlinePlayers().contains(target.player)) chatService?.getPlayerSuffix(target.player)?.translate() ?: "" else ""
                Logger.log(
                    suffix,
                    sender,
                    Perm.LOG,
                    "changed $targetSuffix${target.name}&r’s gamemode to ${mode.name.capitalizeFirstLetter()}.",
                    false
                )
                sender.sendMessage("You updated ".blue() + targetSuffix + target.name + "’s gamemode to ${mode.name.capitalizeFirstLetter()}.".blue())
                target.player?.sendMessage("Your gamemode has been updated to ${mode.name.capitalizeFirstLetter()}.".blue())
                return
            } else {
                target.player?.sendMessage("Your gamemode has been updated to ${mode.name.capitalizeFirstLetter()}.".blue())
            }
        }
        Logger.log(suffix, sender, Perm.LOG, "changed everyone’s gamemode to ${mode.name.capitalizeFirstLetter()}.", false)
        sender.sendMessage("You updated everyone’s gamemode to ${mode.name.capitalizeFirstLetter()}.".blue())
    }
}
