package me.clearedSpore.sporeCore.commands.util

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeCore.util.Perm
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@RegisterCommand
class UtilCommand : SporeCommand() {

    @Command("util")
    @Permission(Perm.UTIL_COMMAND)
    fun onHelp(help: CommandHelp) {
        help.showHelp()
    }
}