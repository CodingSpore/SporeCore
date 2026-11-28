package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.annotation.Optional
import me.clearedSpore.sporeAPI.task.Tasks
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.gray
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.white
import me.clearedSpore.sporeAPI.util.time.TimeUtil
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeCore.features.logs.LogsService
import me.clearedSpore.sporeCore.features.logs.`object`.LogType
import me.clearedSpore.sporeCore.util.Perm
import me.clearedSpore.sporeCore.util.button.TextButton
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Default
import org.incendo.cloud.annotations.Flag
import org.incendo.cloud.annotations.Permission
import java.util.*
import javax.annotation.Nullable

@RegisterCommand
class GetLogsCommand : SporeCommand() {

    private val pageSize = 6

    @Command("getlogs <target> <logTypes> [page] [time] [contains]")
    @Permission(Perm.VIEW_LOGS)
    fun onLogs(
        sender: CommandSender,
        @Argument("target", suggestions = "players") targetName: String,
        @Argument("logTypes", suggestions = "logTypes") typeArg: String,
        @Argument("page") pageArg: Int?,
        @Argument("time", suggestions = "time") @Default("30d") time: String?,
        @Argument("contains") contains: String?,

    ) {
        val targetPlayer = Bukkit.getOfflinePlayer(targetName)
        if (!targetPlayer.hasPlayedBefore()) {
            sender.sendMessage("Player not found.".red())
            return
        }

        val targetUUID = targetPlayer.uniqueId

        val type = try {
            LogType.valueOf(typeArg.uppercase())
        } catch (_: Exception) {
            sender.sendMessage("Invalid log type.".red())
            return
        }

        var page = pageArg ?: 1
        var timeFilter: Long?
        var containsFilter: String? = null

        if (time == null) {
            val durationMs = TimeUtil.parse("30d").millis
            timeFilter = System.currentTimeMillis() - durationMs
        } else {
            val durationMs = TimeUtil.parse(time).millis
            timeFilter = System.currentTimeMillis() - durationMs
        }

        if (contains != null) {
            containsFilter = contains
        }

//        extraArgs.forEach { arg ->
//            when {
//                arg.startsWith("time=", true) -> {
//                    val durationStr = arg.substringAfter("=")
//                    val durationMs = TimeUtil.parse(durationStr).millis
//                    timeFilter = System.currentTimeMillis() - durationMs
//                }
//
//                arg.startsWith("contains=", true) -> containsFilter = arg.substringAfter("=")
//            }
//        }

        sender.sendMessage("Fetching logs async...".blue())

        Tasks.runAsync {
            var logs = LogsService.findLogs(type, targetUUID.toString())

            timeFilter.let { fromTime -> logs = logs.filter { it.timestamp >= fromTime } }
            containsFilter?.let { contains -> logs = logs.filter { it.action.contains(contains, ignoreCase = true) } }

            logs = logs.sortedByDescending { it.timestamp }

            if (logs.isEmpty()) {
                sender.sendMessage("No logs found.".red())
                return@runAsync
            }

            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, logs.size)
            if (startIndex >= logs.size) {
                sender.sendMessage("No logs on this page.".red())
                return@runAsync
            }

            sender.sendMessage("----------------------------------".gray())
            val timeText = timeFilter?.let {
                " after ${
                    TimeUtil.formatDuration(
                        System.currentTimeMillis() - it,
                        TimeUtil.TimeUnitStyle.SHORT,
                        2
                    )
                }"
            } ?: ""
            sender.sendMessage("Showing ${type.name.lowercase()} logs for ${targetPlayer.name}$timeText".blue())
            sender.sendMessage("")

            logs.subList(startIndex, endIndex).forEach { log ->
                val durationAgo = TimeUtil.formatDuration(System.currentTimeMillis() - log.timestamp)
                val hoverTime = Date(log.timestamp).toString()
                val senderName = try {
                    val uuid = UUID.fromString(log.sender)
                    Bukkit.getOfflinePlayer(uuid).name ?: "Console"
                } catch (_: Exception) {
                    "Console"
                }

                val msg = TextButton("[${durationAgo} ago]".gray() + " ${senderName.blue()} -> ${log.action.white()}")
                    .hoverEvent(hoverTime)
                    .build(sender)
                sender.sendMessage(msg)
            }

            sender.sendMessage("")

            val isLastPage = endIndex >= logs.size
            val nextPage = page + 1
            val baseCmd = mutableListOf("/getlogs", targetName, type.name, nextPage.toString())
//            extraArgs.forEach { arg ->
//                if (!arg.startsWith("time=", true) && !arg.startsWith("contains=", true)) return@forEach
//                baseCmd.add(arg)
//            }
            val nextPageCmd = baseCmd.joinToString(" ")

            val nextPageText = if (isLastPage) {
                Component.text("This was the last page. There are no more logs.", NamedTextColor.GRAY)
            } else {
                TextButton("Run to view page $nextPage")
                    .runCommand(nextPageCmd)
                    .hoverEvent(nextPageCmd.gray())
                    .build(sender)
            }

            sender.sendMessage(nextPageText)
            sender.sendMessage("----------------------------------".gray())
        }
    }
}
