package me.clearedSpore.sporeCore.commands.spawn

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.DatabaseManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

class SetSpawnCommand : SporeCommand() {

    @Command("setspawn")
    @Permission(Perm.SETSPAWN)
    fun onSetSpawn(player: Player) {
        val serverData = DatabaseManager.getServerData()
        serverData.spawn = player.location.clone()
        player.sendSuccessMessage("You have successfully set the spawn location!")
        DatabaseManager.saveServerData()
    }


}