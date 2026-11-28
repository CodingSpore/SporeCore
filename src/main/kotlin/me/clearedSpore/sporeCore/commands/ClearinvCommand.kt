package me.clearedSpore.sporeCore.commands

import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.command.SporeCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class ClearinvCommand : SporeCommand() {

    @Command("clearinv [players]")
    @Command("clearinventory [players]")
    @Command("clear [players]")
    @Command("ci [players]")
    fun onClear(sender: CommandSender, @Argument("players", suggestions = "players") targets: MultiplePlayerSelector?) {

        if (targets == null) {
            check(sender is Player) { Logger.error("You must specify a target.") }
            sender.sendSuccessMessage("Your inventory has been cleared.")
            sender.inventory.clear()
            return
        }
        var suffix = chatService?.getPlayerSuffix(sender as Player)?.legacyToMiniMessage() ?: ""

        val players = targets.values()

        if (!sender.hasPermission(Perm.CLEAR_OTHERS)) {
            sender.sendErrorMessage("You don't have permission to clear other players")
            return
        }

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }

        players.forEach {
            it.inventory.clear()
            it.sendMessage("Your inventory has been cleared.".blue())
        }

        val targetSuffix = chatService?.getPlayerSuffix(players.first().player)?.legacyToMiniMessage() ?: ""

        if (players.size == 1) {
            Logger.log(suffix, sender, Perm.LOG, "cleared $targetSuffix$name's inventory", false)
            sender.sendSuccessMessage("You cleared $targetSuffix${players.first().name}".blue() + "'s inventory.".blue())
        } else {
            Logger.log(suffix, sender, Perm.LOG, "cleared the inventories of all players online", false)
            sender.sendSuccessMessage("You cleared ${players.size} players' inventories.".blue())
        }
    }
}


