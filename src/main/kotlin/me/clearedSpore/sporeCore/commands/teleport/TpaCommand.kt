package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command

class TpaCommand : SporeCommand() {

    @Command("tpa <player>")
    fun onTpa(sender: Player, @Argument("player", suggestions = "players") target: OnlinePlayer) {
        TeleportRequestService.sendRequest(sender, target.player, TeleportRequestService.RequestType.TPA)
    }
}
