package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("tpaccept")
class TpaAcceptCommand() : SporeCommand() {

    @Default
    fun onAccept(player: Player) {
        TeleportRequestService.accept(player)
    }
}

@CommandAlias("tpdeny")
class TpaDenyCommand : SporeCommand() {

    @Default
    fun onDeny(player: Player) {
        TeleportRequestService.deny(player)
    }
}
