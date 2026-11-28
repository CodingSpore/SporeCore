package me.clearedSpore.sporeCore.commands.privatemessages

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.features.setting.impl.CommandSpySetting
import me.clearedSpore.sporeCore.features.setting.impl.SocialSpySetting
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

class SocialSpyCommand : SporeCommand() {

    @Command("socialspy command")
    @Permission(Perm.COMMAND_SPY)
    fun onCommandSpy(sender: CommandSender) {
        if (sender !is Player) sender.sendErrorMessage("Only players can use this command.")
        val user = UserManager.get(sender.name) ?: return

        if (!user.getSettingOrDefault(CommandSpySetting())) {
            user.setSetting(CommandSpySetting(), true)
            sender.sendSuccessMessage("You have enabled command spy.")
            user.save()
        } else {
            user.setSetting(CommandSpySetting(), false)
            sender.sendSuccessMessage("You have disabled command spy.")
            user.save()
        }

    }

    @Command("socialspy")
    @Permission(Perm.MSG_SPY)
    fun onSocialSpy(sender: CommandSender) {
        if (sender !is Player) sender.sendErrorMessage("Only players can use this command.")
        val user = UserManager.get(sender.name) ?: return

        if (!user.getSettingOrDefault(SocialSpySetting())) {
            user.setSetting(SocialSpySetting(), true)
            sender.sendSuccessMessage("You have enabled socialspy.")
            user.save()
        } else {
            user.setSetting(SocialSpySetting(), false)
            sender.sendSuccessMessage("You have disabled socialspy.")
            user.save()
        }

    }
}