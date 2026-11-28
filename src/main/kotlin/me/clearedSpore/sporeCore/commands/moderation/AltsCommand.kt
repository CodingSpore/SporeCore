package me.clearedSpore.sporeCore.commands.moderation

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.task.Tasks
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeCore.user.User
import me.clearedSpore.sporeCore.user.UserManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

class AltsCommand : SporeCommand() {

    @Command("alts <player>")
    @Permission(Perm.ALTS)
    fun onAlts(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        handleAlts(sender, targetName, useDeep = false)
    }

    @Command("alts deep <player>")
    @Permission(Perm.ALTS_DEEP)
    fun onAltsDeep(sender: CommandSender, @Argument("player", suggestions = "players") targetName: String) {
        handleAlts(sender, targetName, useDeep = true)
    }

    private fun handleAlts(sender: CommandSender, targetName: String, useDeep: Boolean) {
        val start = System.currentTimeMillis()
        sender.sendMessage("Checking alts for $targetName...".blue())

        val target = Bukkit.getOfflinePlayer(targetName)
        val targetUser = UserManager.get(target)

        if (targetUser == null) {
            sender.sendMessage("Could not find user     $targetName.".red())
            return
        }

        Tasks.runAsync(Runnable {
            val alts: List<User> = if (useDeep) {
                UserManager.getAltsDeep(targetUser)
            } else {
                UserManager.getAltsByLastIp(targetUser.lastIp.toString(), excludeUuid = targetUser.uuid)
            }

            val end = System.currentTimeMillis()
            val time = end - start
            sender.sendMessage("Took $time ms to check the alts for $targetName".blue())

            val allUsers = listOf(targetUser) + alts

            sender.sendMessage("Linked accounts for ${targetUser.playerName}:".blue())
            allUsers.forEach { user ->
                val onlineStatus = if (user.isOnline()) "&aOnline" else "&7Offline"
                val banned = user.isBanned()
                val muted = user.isMuted()
                val punishmentStatus = when {
                    banned && muted -> "&cBanned & &6Muted"
                    banned -> "&cBanned"
                    muted -> "&6Muted"
                    else -> "&aNo active punishment"
                }

                sender.sendMessage("&f- ".translate() + "${user.playerName} ($onlineStatus | $punishmentStatus".blue() + ")".blue())
            }
        })
    }

}
