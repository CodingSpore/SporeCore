package me.clearedSpore.sporeCore.commands.teleport

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@CommandAlias("tpcoords|tpc|tppos")
@CommandPermission(Perm.TELEPORT_CORDS)
@RegisterCommand
class TpCoordsCommand : SporeCommand() {

    @Command("tpcoords <x> <y> <z>")
    @Command("tpc <x> <y> <z>")
    @Command("tppos <x> <y> <z>")
    @Permission(Perm.TELEPORT_CORDS)
    fun onTpCoords(
        executor: Player,
        @Argument("x") arg1: String,
        @Argument("y") arg2: String,
        @Argument("z") arg3: String
    ) {

        val base = executor.location

        val x = parseCoordinate(base.x, arg1)?.let { String.format("%.1f", it).toDouble() }
        val y = parseCoordinate(base.y, arg2)?.let { String.format("%.1f", it).toDouble() }
        val z = parseCoordinate(base.z, arg3)?.let { String.format("%.1f", it).toDouble() }

        if (x == null || y == null || z == null) {
            executor.sendErrorMessage("Invalid coordinates.")
            return
        }


        executor.teleportAsync(Location(executor.world, x, y, z))
        executor.sendSuccessMessage("Teleported ${executor.name} to &f$x $y $z".blue())
    }

    private fun parseCoordinate(base: Double, input: String): Double? {
        return try {
            if (input.startsWith("~")) {
                if (input == "~") base else base + input.substring(1).toDouble()
            } else input.toDouble()
        } catch (e: Exception) {
            null
        }
    }
}
