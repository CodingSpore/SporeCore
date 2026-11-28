package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeCore.extension.PlayerExtension.userFail
import me.clearedSpore.sporeCore.extension.PlayerExtension.userJoinFail
import me.clearedSpore.sporeCore.extension.PlayerExtension.uuid
import me.clearedSpore.sporeCore.features.message.Message
import me.clearedSpore.sporeCore.features.message.MessageType
import me.clearedSpore.sporeCore.menu.user.messages.MessagesMenu
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import java.util.*

@RegisterCommand
class MessagesCommand : SporeCommand() {

    @Command("messages")
    @Command("mail")
    @Command("checkmessages")
    fun onMessages(player: Player) {
        val user = UserManager.get(player)

        if (user == null) {
            player.userFail()
            return
        }

        if (user.messages.isEmpty()) {
            player.sendErrorMessage("You don't have any messages!")
            return
        }

        MessagesMenu(user, player).open(player)

    }

    @Command("messages admin send <player> <text>")
    @Command("mail admin send <player> <text>")
    @Permission(Perm.MESSAGE_ADMIN)
    fun onAdd(
        player: CommandSender,
        @Argument("player", suggestions = "players") targetName: String,
        @Argument("text") text: String
    ) {
        val user = UserManager.get(targetName)

        if (user == null) {
            player.userJoinFail()
            return
        }

        val message = Message(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            MessageType.MESSAGE,
            text,
            player.uuid(),
            true
        )

        user.queueMessage(message)
        player.sendSuccessMessage("Successfully sent the message to &f${user.playerName}")
    }

    @Command("messages admin view <player>")
    @Command("mail admin view <player>")
    @Permission(Perm.MESSAGE_ADMIN)
    fun onView(player: Player, @Argument("player", suggestions = "players") targetName: String) {
        val user = UserManager.get(targetName)

        if (user == null) {
            player.userJoinFail()
            return
        }

        if(user.messages.isEmpty()) {
            player.sendErrorMessage("That player does not have any messages!")
            return
        }

        MessagesMenu(user, player, true).open(player)

    }

}