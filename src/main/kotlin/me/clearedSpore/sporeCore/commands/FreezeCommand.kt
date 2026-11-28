package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.extension.PlayerExtension.uuidStr
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.logs.LogsService
import me.clearedSpore.sporeCore.features.logs.`object`.LogType
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@RegisterCommand
class FreezeCommand : SporeCommand() {

    @Command("freeze <target>")
    @Permission(Perm.FREEZE)
    fun onFreeze(
        sender: CommandSender,
        @Argument("target", suggestions = "players") targetRaw: Player
    ) {
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else "&4"
        val target = targetRaw.player ?: return
        val targetSuffix = chatService?.getPlayerSuffix(target) ?: ""

        val shouldLog = SporeCore.instance.coreConfig.logs.freeze

        if (target.hasMetadata("frozen")) {
            target.removeMetadata("frozen", SporeCore.instance)

            Logger.log(suffix, sender, Perm.LOG, "unfrozen $targetSuffix${target.name}", true)
            if (shouldLog) {
                LogsService.addLog(sender.uuidStr(), "Unfroze ${target.name}", LogType.FREEZE)
                LogsService.addLog(target.uuidStr(), "Unfrozen by ${sender.name}", LogType.FREEZE)
            }

            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You are no longer frozen!!".blue())
            target.sendMessage("You may leave the server again.".blue())
            target.sendMessage("")
            target.sendMessage("")
        } else {
            target.setMetadata("frozen", FixedMetadataValue(SporeCore.instance, true))

            Logger.log(suffix, sender, Perm.LOG, "froze $targetSuffix${target.name}", true)
            if (shouldLog) {
                LogsService.addLog(sender.uuidStr(), "Froze ${target.name}", LogType.FREEZE)
                LogsService.addLog(target.uuidStr(), "Frozen by ${sender.name}", LogType.FREEZE)
            }

            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You have been &bfrozen".red())
            target.sendMessage("Do not leave the server!".red())
            target.sendMessage("Wait for further instructions!".red())
            target.sendMessage("")
            target.sendMessage("")
        }
    }



    @Command("freeze set <target> <state>")
    @Permission(Perm.FREEZE)
    fun onSet(
        sender: CommandSender,
        @Argument("target", suggestions = "players") targetRaw: Player,
        @Argument("state") state: Boolean
    ) {
        val target = targetRaw.player ?: return
        var suffix = if (sender is Player) chatService?.getPlayerSuffix(sender)?.legacyToMiniMessage() ?: "" else ""

        val shouldLog = SporeCore.instance.coreConfig.logs.freeze

        if (!state) {
            target.removeMetadata("frozen", SporeCore.instance)
            Logger.log(suffix, sender, Perm.LOG, "unfrozen ${target.name}", true)
            if (shouldLog) {
                LogsService.addLog(sender.uuidStr(), "Unfroze ${target.name}", LogType.FREEZE)
                LogsService.addLog(target.uuidStr(), "Unfrozen by ${sender.name}", LogType.FREEZE)
            }

            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You are no longer frozen!!".blue())
            target.sendMessage("You may leave the server again.".blue())
            target.sendMessage("")
            target.sendMessage("")
        } else {
            target.setMetadata("frozen", FixedMetadataValue(SporeCore.instance, true))
            Logger.log(suffix, sender, Perm.LOG, "froze ${target.name}", true)

            if (shouldLog) {
                LogsService.addLog(sender.uuidStr(), "Froze ${target.name}", LogType.FREEZE)
                LogsService.addLog(target.uuidStr(), "Frozen by ${sender.name}", LogType.FREEZE)
            }

            target.sendMessage("")
            target.sendMessage("")
            target.sendMessage("You have been &bFrozen".red())
            target.sendMessage("Do not leave the server!".red())
            target.sendMessage("Wait for further instructions!".red())
            target.sendMessage("")
            target.sendMessage("")
        }
    }
}