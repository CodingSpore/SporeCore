package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Private
import co.aikar.commands.annotation.Subcommand
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import me.clearedSpore.sporeAPI.util.ChatInputService
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.extension.PlayerExtension.uuidStr
import me.clearedSpore.sporeCore.features.investigation.IGService
import me.clearedSpore.sporeCore.features.investigation.IGService.igCollection
import me.clearedSpore.sporeCore.features.investigation.`object`.enum.InvestigationPriority
import me.clearedSpore.sporeCore.menu.investigation.list.InvestigationListMenu
import me.clearedSpore.sporeCore.menu.investigation.manage.ManageIGMenu
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.entity.Player
import org.dizitart.no2.filters.FluentFilter.where
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission


@CommandAlias("investigation")
@CommandPermission(Perm.INVESTIGATION_STAFF)
class InvestigationCommand : SporeCommand() {

    val service = IGService

    @Command("investigation")
    @Permission(Perm.INVESTIGATION_STAFF)
    fun onMenu(sender: Player) {
        InvestigationListMenu(sender).open(sender)
    }

    @Command("investigation start <name> <priority>")
    @Permission(Perm.INVESTIGATION_ADMIN)
    fun onStart(
        player: Player,
        @Argument("name") name: String,
        @Argument("priority") priority: InvestigationPriority,
    ) {

        if (igCollection.find(where("name").eq(name)).firstOrNull() != null) {
            player.sendErrorMessage("An investigation with that name already exists.")
            return
        }


        player.sendSuccessMessage("Please provide the description of the investigation in chat.")
        ChatInputService.begin(player, true) { input ->
            service.startInvestigation(player, name, input, priority)
        }
    }

    @Private
    @Command("investigation add punishment <ID> <punishmentID>")
    @Permission(Perm.INVESTIGATION_ADMIN)
    fun onAddPunishment(sender: Player, @Argument("ID") investigationID: String, @Argument("punishmentID") id: String) {
        IGService.addPunishment(sender, investigationID, id).let {
            sender.sendSuccessMessage("Added the investigation punishment")
        }
    }

    @Private
    @Command("investigation add report <ID> <reportID>")
    @Permission(Perm.INVESTIGATION_ADMIN)
    fun onAddReport(sender: Player, @Argument("ID") investigationID: String, @Argument("reportID") id: String) {
        IGService.addReport(investigationID, sender.uuidStr(), id).let {
            sender.sendSuccessMessage("Added the investigation report")
        }
    }

    @Private
    @Command("investigation add note <ID> <text>")
    @Permission(Perm.INVESTIGATION_ADMIN)
    fun onAddNote(sender: Player, @Argument("ID") investigationID: String, @Argument("text") name: String) {
        ChatInputService.begin(sender) { input ->
            IGService.addNote(investigationID, name, input, sender).let {
                sender.sendSuccessMessage("Added the investigation note")
            }
        }
    }

    @Private
    @Command("investigation add suspect <ID> <suspect>")
    @Permission(Perm.INVESTIGATION_ADMIN)
    fun onAddSuspect(sender: Player, @Argument("ID") investigationID: String, @Argument("suspect", suggestions = "players") name: String) {
        ChatInputService.begin(sender) { input ->
            IGService.addSuspect(investigationID, name, input, sender.name).let {
                sender.sendSuccessMessage("Added the suspect successfully")
            }
        }
    }
}