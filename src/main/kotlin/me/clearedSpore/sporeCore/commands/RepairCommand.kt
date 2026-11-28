package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.acf.targets.`object`.TargetPlayers
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@CommandAlias("repair|fix")
@CommandPermission(Perm.REPAIR)
@RegisterCommand
class RepairCommand : SporeCommand() {

    @Command("repair")
    @Command("fix")
    @Permission(Perm.REPAIR)
    fun onRepair(sender: CommandSender) {
        check(sender is Player) { Logger.error("Only players can use this command!") }
        val item = sender.inventory.itemInMainHand

        if (item.type.isAir) {
            sender.sendErrorMessage("You cannot repair air.")
            return
        } else {
            item.durability = 0
            sender.sendMessage("Your item has been repaired.".blue())
        }
    }
}
