package me.clearedSpore.sporeCore.commands.utilitymenus

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@CommandAlias("smithingtable|smith")
@CommandPermission(Perm.SMITHING)
class SmithingTableCommand() : SporeCommand() {

    @Default
    @CommandCompletion("@players")
    fun onSmithing(player: Player, @Optional targetName: String?) {
        if (targetName == null) {
            player.openSmithingTable(player.location, true)
            return
        }

        if (!player.hasPermission(Perm.UTILITY_OTHERS)) {
            player.sendMessage("You don't have permission to do this!".red())
            return
        }

        val target = Bukkit.getPlayer(targetName)
        target!!.openSmithingTable(player.location, true)
        player.sendMessage("You have opened a Smithing table for ${target.name}".blue())

    }
}