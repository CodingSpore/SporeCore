package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.warp.WarpService
import me.clearedSpore.sporeCore.menu.util.confirm.ConfirmMenu
import me.clearedSpore.sporeCore.menu.warps.WarpsMenu
import me.clearedSpore.sporeCore.util.Perm
import me.clearedSpore.sporeCore.util.TeleportService.awaitTeleport
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

class WarpCommand : SporeCommand() {

    @Dependency
    lateinit var warpService: WarpService

    @Command("warp [warp]")
    @Command("warps [warp]")
    fun warp(player: Player, @Argument("warp", suggestions = "warps") name: String?) {
        val warps = warpService.getAllWarps()

        if (name.isNullOrEmpty()) {
            WarpsMenu(player).open(player)
            return
        }

        val warp = warps.find { it.name.equals(name, ignoreCase = true) }

        if (warp == null) {
            player.sendErrorMessage("There was no warp found with that name!")
            return
        }

        if (warp.permission != null && !player.hasPermission(warp.permission)) {
            player.sendErrorMessage("You don't have permission to use this warp!")
            return
        }

        player.awaitTeleport(warp.location)
    }

    @Command("warp create <name> [permission]")
    @Command("warps create <name> [permission]")
    @Permission(Perm.WARP_CREATE)
    fun createWarp(player: Player, @Argument("name") name: String, @Argument("permission") permission: String?) {
        val location = player.location
        var suffix = chatService?.getPlayerSuffix(player)?.legacyToMiniMessage() ?: ""
        val warps = warpService.getAllWarps()
        val warp = warps.find { it.name.equals(name, ignoreCase = true) }
        val permission = permission

        if (warp != null) {
            player.sendErrorMessage("There is already a warp with that name!")
            return
        }

        if (permission.isNullOrEmpty()) {
            warpService.createWarp(name, location, null)
            player.sendSuccessMessage("You have successfully created a new warp called '$name'")
            Logger.log(suffix, player, Perm.LOG, "created a new warp called '$name'", false)
            return
        }

        warpService.createWarp(name, location, permission)
        player.sendSuccessMessage("You have successfully created a new warp called '$name'")
        Logger.log(suffix, player, Perm.LOG, "created a new warp called '$name'", false)
    }


    @Command("warp delete <warp>")
    @Command("warps delete <warp>")
    @Permission(Perm.WARP_DELETE)
    fun deleteWarp(player: Player, @Argument("warp", suggestions = "warps") name: String) {
        var suffix = chatService?.getPlayerSuffix(player)?.legacyToMiniMessage() ?: ""
        val warps = warpService.getAllWarps()

        val warp = warps.find { it.name.equals(name, ignoreCase = true) }

        if (warp != null) {
            ConfirmMenu(player) {
                warpService.deleteWarp(name)
                player.sendSuccessMessage("Warp '$name' has been deleted.")
            }.open(player)
        } else {
            player.sendErrorMessage("No warp found with the name '$name'.")
        }

        Logger.log(suffix, player, Perm.LOG, "deleted a warp called '$name'", false)
    }

    @Command("warp setpermission <warp> <permission>")
    @Command("warps setpermission <warp> <permission>")
    @Permission(Perm.WARP_PERMISSION)
    fun permission(
        player: Player,
        @Argument("warp", suggestions = "warps") name: String,
        @Argument("permission") permission: String?
    ) {
        val warps = warpService.getAllWarps()
        val warp = warps.find { it.name.equals(name, ignoreCase = true) }

        if (warp == null) {
            player.sendErrorMessage("No warp found with the name '$name'.")
            return
        }

        if (permission.isNullOrBlank()) {
            player.sendErrorMessage("You must specify a permission to set.")
            return
        }

        warpService.setPermission(name, permission)
        player.sendSuccessMessage("Set permission for warp '$name' to '$permission'.")
    }

    @Command("warp reload")
    @Command("warps reload")
    @Permission(Perm.ADMIN)
    fun reload(sender: CommandSender) {
        try {
            warpService.reloadWarps()
            sender.sendMessage("You have reloaded all warps.".blue())
            Logger.info("Warps reloaded by ${sender.name}")
        } catch (ex: Exception) {
            sender.sendMessage("Failed to reload warps: ${ex.message}".red())
            ex.printStackTrace()
        }
    }

}