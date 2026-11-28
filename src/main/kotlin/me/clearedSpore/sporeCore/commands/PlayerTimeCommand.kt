package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.StringUtil.capitalizeFirstLetter
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@CommandAlias("ptime")
@CommandPermission(Perm.PTIME)
@RegisterCommand
class PlayerTimeCommand : SporeCommand() {

    @Command("ptime <time>")
    @Permission(Perm.PTIME)
    fun onPTime(sender: CommandSender, @Argument("time", suggestions = "times") type: Time) {
        check(sender is Player) { Logger.error("This command can only be used by a player!") }

        sender.setPlayerTime(type.tick, false)
        sender.sendMessage("Your personal time has been set to ${type.toString().capitalizeFirstLetter()}".blue())
    }

    enum class Time(val tick: Long) {

        DAY(1000),
        NOON(6000),
        NIGHT(13000),
        MIDNIGHT(18000)
        ;

    }

}
