package me.clearedSpore.sporeCore.commands.spawn

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.DatabaseManager
import me.clearedSpore.sporeCore.util.TeleportService.awaitTeleport
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command

class SpawnCommand : SporeCommand() {

    @Command("spawn")
    fun onSpawn(player: Player) {
        val spawn = DatabaseManager.getServerData().spawn

        if (spawn == null) {
            player.sendMessage("Spawn has not been set yet!".red())
            return
        }

        player.awaitTeleport(spawn)
    }
}