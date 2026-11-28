package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.menu.stat.StatsMenu
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command

class StatsCommand : SporeCommand() {

    @Command("stats [player]")
    fun onStats(player: Player, @Argument("player", suggestions = "players") targetName: String?) {
        if (targetName == null) {
            StatsMenu(player).open(player)
            return
        }

        val target = Bukkit.getOfflinePlayer(targetName)

        if (!target.hasPlayedBefore()) {
            player.userJoinFail()
            return
        }

        StatsMenu(target).open(player)

    }


}