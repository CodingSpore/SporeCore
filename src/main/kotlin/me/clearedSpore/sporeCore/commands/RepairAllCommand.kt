package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@RegisterCommand
class RepairAllCommand : SporeCommand() {

    @Command("repairall [targets]")
    @Command("fixall [targets]")
    @Permission(Perm.REPAIRALL)
    fun onRepair(sender: CommandSender, @Argument("targets", suggestions = "players") targets: MultiplePlayerSelector?) {
        if (targets == null) {
            check(sender is Player) { Logger.error("You must specify a target.") }
            (sender.inventory.contents + sender.inventory.extraContents + arrayOf(sender.inventory.itemInOffHand)).forEach { item ->
                if (item != null && item.type != Material.AIR) {
                    item.durability = 0
                }
                sender.sendMessage("All your items have been repaired.".blue())
            }

            return
        }

        val players = targets.values()

        var success = 0
        var failed = 0

        players.forEach { player ->
            var repaired = 0

            (player.inventory.contents + player.inventory.extraContents + arrayOf(player.inventory.itemInOffHand)).forEach { item ->
                if (item != null && item.type != Material.AIR) {
                    item.durability = 0
                    repaired++
                }
            }

            player.inventory.armorContents.forEach { armor ->
                if (armor != null && armor.type != Material.AIR) {
                    armor.durability = 0
                    repaired++
                }
            }

            if (repaired > 0) {
                success++
                player.sendMessage("All your items have been repaired.".blue())
            } else {
                failed++
            }
        }

        when {
            success > 0 && failed > 0 ->
                sender.sendMessage(
                    "Successfully repaired items for $success players and failed for $failed players (no items).".blue()
                )

            success > 0 ->
                sender.sendMessage(
                    "Successfully repaired items for $success players.".blue()
                )

            else ->
                sender.sendMessage(
                    "Failed to repair items for all players (no items).".red()
                )
        }
    }
}
