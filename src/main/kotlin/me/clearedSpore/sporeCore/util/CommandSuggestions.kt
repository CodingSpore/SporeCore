package me.clearedSpore.sporeCore.util

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player
import org.incendo.cloud.suggestion.SuggestionProvider
import me.clearedSpore.sporeCore.features.vanish.VanishService
import org.bukkit.Bukkit
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

object CommandSuggestions {

    fun register(manager: PaperCommandManager<CommandSourceStack>) {
        manager.brigadierManager()
    }

    fun players(): SuggestionProvider<CommandSourceStack> = SuggestionProvider { context, input ->
        val sender = context.sender().sender
        val canSeeVanished = sender !is Player || sender.hasPermission(Perm.VANISH_SEE)

        CompletableFuture.completedFuture(
            Bukkit.getOnlinePlayers()
                .filter { canSeeVanished || !VanishService.isVanished(it.uniqueId) }
                .map { Suggestion.suggestion(it.name) }
        )

    }
}