package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@RegisterCommand
class TPSBarCommand : SporeCommand() {

    @Command("tpsbar")
    @Permission(Perm.TPSBAR)
    fun onTpsBar(player: Player) {
        val user = UserManager.get(player)
        if (user == null) {
            player.userFail()
            return
        }

        val current = user.tpsBar
        user.tpsBar = !current
        player.sendSuccessMessage("Toggled TPS bar!")

    }
}