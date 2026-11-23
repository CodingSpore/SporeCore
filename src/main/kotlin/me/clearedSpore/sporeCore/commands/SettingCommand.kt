package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.menu.settings.SettingsMenu
import me.clearedSpore.sporeCore.user.UserManager
import org.bukkit.entity.Player


@CommandAlias("settings")
class SettingCommand() : SporeCommand() {

    @Default()
    fun onMenu(player: Player) {
        val user = UserManager.get(player)
        if (user == null) {
            return player.userJoinFail()
        }

        SettingsMenu(player).open(player)
    }
}