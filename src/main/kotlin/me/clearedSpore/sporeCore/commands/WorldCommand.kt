package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.command.SporeCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Logger
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.util.log
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.World
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@RegisterCommand
class WorldCommand : SporeCommand() {

    @Command("world <world>")
    @Permission(Perm.TELEPORT_CORDS)
    fun onWorld(player: Player, @Argument("world", suggestions = "worlds") world: World) {
        val worldName = world.name.uppercase()
        val worldSpawn = world.spawnLocation
        val suffix = chatService?.getPlayerSuffix(player)?.legacyToMiniMessage() ?: ""

        player.teleportAsync(worldSpawn)
        player.sendSuccessMessage("Successfully teleported to $worldName world!")
        Logger.log(suffix, player, Perm.MODE_ALLOW, "has teleported to the '$worldName' world", false)

    }
}