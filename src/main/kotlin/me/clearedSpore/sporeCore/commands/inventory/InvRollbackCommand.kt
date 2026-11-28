package me.clearedSpore.sporeCore.commands.inventory

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.inventory.InventoryManager
import me.clearedSpore.sporeCore.menu.invrollback.InvRollBackMenu
import me.clearedSpore.sporeCore.menu.invrollback.claim.ClaimMenu
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command

class InvRollbackCommand : SporeCommand() {

    @Command("invrestore [player]")
    fun onRollback(player: Player, @Argument("player", suggestions = "players") targetName: String?) {

        if (targetName.isNullOrEmpty()) {
            val user = UserManager.get(player)
            if (user == null) {
                player.userFail()
                return
            }

            user.pendingInventories.removeIf { InventoryManager.getInventory(it) == null }
            UserManager.save(user)

            if (user.pendingInventories.isEmpty()) {
                player.sendErrorMessage("You don't have any inventories you can claim!")
                return
            }

            ClaimMenu(player).open(player)
        } else if (targetName.isNotEmpty() && player.hasPermission(Perm.INV_ROLLBACK)) {
            val target = Bukkit.getOfflinePlayer(targetName)

            if (!target.hasPlayedBefore()) {
                player.userJoinFail()
                return
            }

            InvRollBackMenu(player, target).open(player)
        } else {
            player.sendErrorMessage("You don't have permission to perform this command!")
        }
    }
}