package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.StringUtil.capitalizeFirstLetter
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.WeatherType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command

@CommandAlias("pweather")
@CommandPermission(Perm.PWEATHER)
@RegisterCommand
class PlayerWeatherCommand : SporeCommand() {

    @Command("pweather <type>")
    fun onPweather(sender: CommandSender, @Argument("type", suggestions = "weatherTypes") type: WeatherType) {
        check(sender is Player) { Logger.error("You must be a player to use this command.") }

        Bukkit.getPlayer(sender.uniqueId)?.setPlayerWeather(type)
        sender.sendMessage(
            "Your personal weather has been set to ${type.toString().capitalizeFirstLetter()}".blue())
    }
}
