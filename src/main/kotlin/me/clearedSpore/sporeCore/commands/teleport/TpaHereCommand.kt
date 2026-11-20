package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import org.bukkit.entity.Player

@CommandAlias("tpahere")
class TpaHereCommand : SporeCommand() {

    @Default
    @CommandCompletion("@players")
    fun onTpaHere(sender: Player, target: OnlinePlayer) {
        TeleportRequestService.sendRequest(sender, target.player, TeleportRequestService.RequestType.TPAHERE)
    }
}
