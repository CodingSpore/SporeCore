package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Damageable
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class HealCommand : SporeCommand() {


    @Command("heal [targets]")
    @Permission(Perm.HEAL)
    fun onHeal(
        sender: CommandSender,
        @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?
    ) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""

        if (targets == null) {
            check(sender is Player) { Logger.error("You must specify a target.") }
            sender.sendSuccessMessage("You have been fed!")
            sender.foodLevel = 20 ; sender.saturation = 20f
            return
        }

        if (!sender.hasPermission(Perm.HEAL_OTHERS)) {
            sender.sendErrorMessage("You don't have permission to heal other players")
            return
        }

        val players = targets.values()

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }

        players.forEach {
            it.health = it.maxHealth
            it.foodLevel = 20 ; it.saturation = 20f
        }

        val count = players.size

        if (count == 1) {
            val name = players.first()?.name ?: players.first().type.name.lowercase()
            val targetSuffix = chatService?.getPlayerSuffix(players.first())?.translate()
            sender.sendMessage("You healed $targetSuffix$name&r&#1D91FF.".blue())
            Logger.log(suffix, sender, Perm.LOG, "healed $targetSuffix$name", false)
        } else {
            sender.sendMessage("You healed $count entities.".blue())
            Logger.log(suffix, sender, Perm.LOG, "healed $count entities", false)
        }


    }
}
