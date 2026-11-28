package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command

@Command("tpaccept")
class TpaAcceptCommand : SporeCommand() {

    fun onAccept(player: Player) {
        TeleportRequestService.accept(player)
    }
}

@Command("tpdeny")
class TpaDenyCommand : SporeCommand() {

    fun onDeny(player: Player) {
        TeleportRequestService.deny(player)
    }
}
