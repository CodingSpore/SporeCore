package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class SpeedCommand : SporeCommand() {

    @Command("speed <speed> [targets]")
    @Permission(Perm.SPEED)
    fun onSpeed(
        sender: CommandSender,
        @Argument("speed") @Range(min = "1", max = "10") speed: Float,
        @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?
    ) {
        if (targets == null) {
            check(sender is Player) { Logger.error("You must specify a target.") }
            var senderSuffix = chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: ""
            sender.sendSuccessMessage("Your speed has been set to $speed.")
            Logger.log(
                senderSuffix,
                sender,
                Perm.MODE_ALLOW,
                "set speed of $senderSuffix${sender.name} to $speed",
                false
            )
            val bukkitSpeed = speed / 10f
            sender.flySpeed = bukkitSpeed
            sender.walkSpeed = bukkitSpeed
            return
        }

        if (speed !in 1.0..10.0) {
            sender.sendMessage("Amount must be between 1 - 10!".red())
            return
        }

        if (!sender.hasPermission(Perm.SPEED_OTHERS)) {
            sender.sendErrorMessage("You do not have permission to set the speed of other players!")
            return
        }

        val players = targets.values()

        players.forEach { target ->
            val bukkitSpeed = speed / 10f
            target.flySpeed = bukkitSpeed
            target.walkSpeed = bukkitSpeed
        }

        var suffix = chatService?.getPlayerSuffix(players.first().player)?.legacyToMiniMessage() ?: ""

        when (players.size) {
            1 -> {
                sender.sendMessage("Speed set to $speed for $suffix${players.first().name}".blue() + ".".blue())
                Logger.log(
                    suffix,
                    sender,
                    Perm.MODE_ALLOW,
                    "set speed of $suffix${players.first().name} to $speed",
                    false
                )
            }
            else -> {
                sender.sendMessage("Speed set to $speed for ${players.size} players.".blue())
                Logger.log(
                    suffix,
                    sender,
                    Perm.MODE_ALLOW,
                    "set speed of ${players.size} players to $speed",
                    false
                )
            }
        }
    }

    @Command("speed reset [targets]")
    @Permission(Perm.SPEED)
    fun onReset(sender: CommandSender, @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {
        if (targets == null) {
            check(sender is Player) { "You must specify a target." }
            var senderSuffix = chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: ""
            sender.sendSuccessMessage("Your speed has been reset back to default.")
            Logger.log(
                senderSuffix,
                sender,
                Perm.MODE_ALLOW,
                "reset their speed",
                false
            )
            sender.flySpeed = 0.1f
            sender.walkSpeed = 0.2f
            return
        }

        if (!sender.hasPermission(Perm.SPEED_OTHERS)) {
            sender.sendErrorMessage("You do not have permission to set the speed of other players!")
            return
        }

        val players = targets.values()

        players.forEach { player ->
            player.flySpeed = 0.1f
            player.walkSpeed = 0.2f
            if (player != sender) {
                player.sendMessage("Your speed has been reset to default.".blue())
            }
        }
        var suffix = chatService?.getPlayerSuffix(players.first().player)?.legacyToMiniMessage() ?: ""

        when (players.size) {
            1 -> {
                sender.sendMessage("Speed reset for $suffix${players.first().name}".blue() + ".".blue())
                Logger.log(
                    suffix,
                    sender,
                    Perm.MODE_ALLOW,
                    "reset speed of $suffix${players.first().name}",
                    false
                )
            }
            else -> {
                sender.sendMessage("Speed reset for ${players.size} players.".blue())
                Logger.log(
                    suffix,
                    sender,
                    Perm.MODE_ALLOW,
                    "reset the speed of ${players.size} players",
                    false
                )
            }
        }
    }
}