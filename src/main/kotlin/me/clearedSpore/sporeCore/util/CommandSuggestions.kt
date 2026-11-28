package me.clearedSpore.sporeCore.util

import io.papermc.paper.command.brigadier.CommandSourceStack
import me.clearedSpore.sporeCore.commands.PlayerTimeCommand
import me.clearedSpore.sporeCore.features.kit.KitService
import me.clearedSpore.sporeCore.features.kit.`object`.Kit
import me.clearedSpore.sporeCore.features.logs.`object`.LogType
import me.clearedSpore.sporeCore.features.vanish.VanishService
import me.clearedSpore.sporeCore.features.vanish.VanishService.vanish
import me.clearedSpore.sporeCore.features.warp.WarpService
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Registry
import org.bukkit.WeatherType
import org.bukkit.World
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.incendo.cloud.CommandManager
import org.incendo.cloud.suggestion.Suggestion
import org.incendo.cloud.suggestion.SuggestionProvider
import java.util.concurrent.CompletableFuture

object CommandSuggestions {

    fun register(manager: CommandManager<CommandSourceStack>) {
        val registry = manager.parserRegistry()

        registry.registerSuggestionProvider("players", players())
        registry.registerSuggestionProvider("time", time())
        registry.registerSuggestionProvider("logTypes", logTypes())
        registry.registerSuggestionProvider("features", features())
        registry.registerSuggestionProvider("gamemodes", gamemodes())
        registry.registerSuggestionProvider("warps", warps())
        registry.registerSuggestionProvider("weatherTypes", weatherTypes())
        registry.registerSuggestionProvider("enchants", enchants())
        registry.registerSuggestionProvider("times", times())
        registry.registerSuggestionProvider("materials", materials())
        registry.registerSuggestionProvider("worlds", worlds())
        registry.registerSuggestionProvider("kits", kits() )
    }

    fun players(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { context, _ ->
        val sender = context.sender().sender
        val canSeeVanished = sender !is Player || sender.hasPermission(Perm.VANISH_SEE)

        CompletableFuture.completedFuture(
            Bukkit.getOnlinePlayers()
                .filter { canSeeVanished || !VanishService.isVanished(it.uniqueId) }
                .map { Suggestion.suggestion(it.name) }
        )

    }

    fun time(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, input ->
        val time = input.lastRemainingToken().trim()
        if (time.isEmpty()) {
            return@SuggestionProvider CompletableFuture.completedFuture(
                listOf("1s", "1m", "1h", "1d", "1w").map(Suggestion::suggestion)
            )
        }

        val lower = time.lowercase()
        val suffixes = listOf("s", "m", "h", "d", "w")

        val suggestions = LinkedHashSet<String>()
        suggestions.add(time)
        for (s in suffixes) {
            if (!lower.endsWith(s)) {
                suggestions.add(time + s)
            }
        }

        CompletableFuture.completedFuture(
            suggestions.asSequence()
                .filter { it.startsWith(time, ignoreCase = true) }
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun logTypes(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val logTypes = LogType.entries.map { it.displayName.lowercase() }

        CompletableFuture.completedFuture(
            logTypes.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun features(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        var features = arrayOf(
            "currency",
            "punishments",
            "channels",
            "modes",
            "inventory",
            "discord",
            "selector",
            "reports",
            "investigation"
        )

        CompletableFuture.completedFuture(
            features.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun gamemodes(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val gamemodes = arrayOf(
            "survival",
            "adventure",
            "creative",
            "spectator"
        )

        CompletableFuture.completedFuture(
            gamemodes.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun warps(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val warps = WarpService().getAllWarps().map { warp -> warp.name }

        CompletableFuture.completedFuture(
            warps.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )

    }

    private fun weatherTypes(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val weatherTypes = WeatherType.entries.map { weatherType -> weatherType.name.lowercase() }

        CompletableFuture.completedFuture(
            weatherTypes.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )

    }

    private fun enchants(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val enchants = Registry.ENCHANTMENT.map { enchantment -> enchantment.name.lowercase() }

        CompletableFuture.completedFuture(
            enchants.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )

    }

    private fun times(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val times = PlayerTimeCommand.Time.entries.map { time -> time.name.lowercase() }

        CompletableFuture.completedFuture(
            times.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun materials(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val materials = Material.values().map { material -> material.name.lowercase() }

        CompletableFuture.completedFuture(
            materials.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun worlds(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val worlds = Bukkit.getWorlds().map { world -> world.name }

        CompletableFuture.completedFuture(
            worlds.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }

    private fun kits(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { _, _ ->
        val kits = KitService().kits.values.map { kit -> kit.name }

        CompletableFuture.completedFuture(
            kits.asSequence()
                .map(Suggestion::suggestion)
                .toList()
        )
    }
}