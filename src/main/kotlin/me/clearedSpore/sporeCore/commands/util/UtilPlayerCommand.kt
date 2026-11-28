package me.clearedSpore.sporeCore.commands.util

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.green
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.StringUtil.joinWithSpaces
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.vanish.VanishService
import me.clearedSpore.sporeCore.util.Perm
import me.clearedSpore.sporeCore.util.button.TextButton
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@RegisterCommand
class UtilPlayerCommand : SporeCommand() {

    @Command("util player uuid <player>")
    @Permission(Perm.UTIL_PLAYER)
    fun getUUID(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        val target = Bukkit.getOfflinePlayer(targetName)

        if (!target.hasPlayedBefore()) {
            sender.userJoinFail()
            return
        }

        sender.sendSuccessMessage("$targetName UUID is: &f${target.uniqueId}")
        val button = TextButton("[Click to copy]".blue())
            .copyToClipboard(target.uniqueId.toString())
            .hoverEvent("Click to copy!")
            .onClick {
                sender.sendSuccessMessage("Successfully copied!")
            }
            .build(sender)
        sender.sendMessage(button)
    }

/*    @Command("util player skull <player>")
    @Permission(Perm.UTIL_PLAYER)
    fun skull(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        val target: OfflinePlayer = Bukkit.getOfflinePlayer(targetName)
        if (!target.hasPlayedBefore() && !target.isOnline) {
            sender.userJoinFail()
            return
        }
        var suffix = chatService?.getPlayerSuffix(target.player)?.legacyToMiniMessage() ?: ""

        val head = ItemStack(org.bukkit.Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.owningPlayer = target
        head.itemMeta = meta

        sender.sendSuccessMessage("Player skull for $suffix${target.name}" + " created!".blue())
        if (sender is Player) {
            sender.inventory.addItem(head)
        }
    }*/

    @Command("util player sendmessage <player> <message>")
    @Permission(Perm.UTIL_PLAYER)
    fun onSendMessage(
        sender: CommandSender,
        @Argument("player", suggestions = "players") targetOnline: Player,
        @Argument("message") messageParts: String
    ) {
        val target = targetOnline.player as Player
        val message = messageParts.joinWithSpaces()
        var suffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""

        if (!target.isOnline) {
            sender.sendErrorMessage("That player is not online.")
            return
        }

        target.sendMessage(message.translate())
        sender.sendSuccessMessage("Successfully send the message to $suffix${target.name}")
        sender.sendMessage("Message: $message".blue())
    }

/*    @Command("util player skull id <id>")
    @Permission(Perm.UTIL_PLAYER)
    fun skullId(sender: CommandSender, @Argument("id") skullId: String) {
        val head = ItemStack(org.bukkit.Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.ownerProfile = Bukkit.getOfflinePlayer(skullId).playerProfile
        head.itemMeta = meta


        sender.sendSuccessMessage("Custom skull with ID $skullId created!")
        if (sender is Player) {
            sender.inventory.addItem(head)
        }
    }*/


    @Command("util player vanished <player> ")
    @Permission(Perm.UTIL_PLAYER)
    fun vanished(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        val target = Bukkit.getPlayer(targetName)
        if (target == null) {
            sender.sendErrorMessage("&e$targetName".translate() + " is offline, cannot retrieve their vanish status.".red())
            return
        }
        var suffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""

        val isVanished = VanishService.isVanished(target.uniqueId)
        if (isVanished) {
            sender.sendSuccessMessage("$suffix${target.name}".translate() + " is &fvanished!".blue())
        } else {
            sender.sendSuccessMessage("$suffix${target.name}".translate() + " is &fvisible!".blue())
        }
    }

    @Command("util player location <player>")
    @Permission(Perm.UTIL_PLAYER)
    fun location(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        val target = Bukkit.getPlayer(targetName)
        if (target == null) {
            sender.sendErrorMessage("$targetName is offline, cannot retrieve location!")
            return
        }

        val loc = target.location
        val copyString = "${loc.blockX}, ${loc.blockY}, ${loc.blockZ}, ${loc.world?.name ?: "world"}"
        var suffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""
        sender.sendSuccessMessage("$suffix${target.name}" + "'s location: ${loc.world?.name} x:${loc.blockX} y:${loc.blockY} z:${loc.blockZ}".white())

        if (sender is Player) {
            val copyButton = TextButton("[Copy]".blue())
                .copyToClipboard(copyString)
                .hoverEvent("Copy coordinates")
                .build(sender)

            val components = mutableListOf(copyButton)

            if (sender.hasPermission(Perm.TELEPORT_CORDS)) {
                val teleportButton = TextButton(" [Teleport]".blue())
                    .runCommand("/tppos ${loc.blockX} ${loc.blockY} ${loc.blockZ}")
                    .hoverEvent("Click to teleport!")
                    .build(sender)
                components.add(teleportButton)
            }

            val finalComponent = Component.join(JoinConfiguration.separator(Component.text(" ")), components)
            sender.sendMessage(finalComponent)
        }
    }
}