package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.features.reboot.RebootService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission


@RegisterCommand
class RebootCommand() : SporeCommand() {


    @Command("reboot <time>")
    @Permission(Perm.REBOOT)
    fun onReboot(sender: CommandSender, @Argument("time", suggestions = "time") time: String) {
        if (time.isEmpty()) {
            sender.sendErrorMessage("Usage: /reboot cancel|<time>")
        }
        RebootService.startReboot(time)
    }


    @Command("reboot cancel")
    @Permission(Perm.REBOOT)
    fun onCancel(sender: CommandSender) {
        RebootService.cancelReboot()
    }
}