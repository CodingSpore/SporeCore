package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.features.eco.EconomyService.parseAmount
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.checkerframework.checker.units.qual.m
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector

@CommandAlias("i|item|give")
@CommandPermission(Perm.GIVE)
@RegisterCommand
class ItemCommand : SporeCommand() {

    @Command("i <material> <amount> [players]")
        fun onGive(
        sender: Player,
        @Argument("material", suggestions = "materials") material: Material,
        @Argument("amount") @Range(min = "1", max = "64") @org.incendo.cloud.annotations.Default("64") amount: Int?,
        @Argument("players", suggestions = "players") players: MultiplePlayerSelector?) {

        val material = Material.matchMaterial(material.toString())
        if (material == null) {
            sender.sendMessage("Invalid material: $material".red())
            return
        }

        val amount = amount ?: 64

        if (players == null) {
            sender.inventory.addItem(ItemStack(material, amount))
            sender.sendMessage("You received ${amount}x of ${material.name}!".blue())
            return
        }

        val players = players.values()

        if (players.isEmpty()) {
            sender.sendErrorMessage("No players matched that selector.")
            return
        }

        players.forEach { player ->
            if (players.first() != sender) {
                var suffix = chatService?.getPlayerSuffix(player)?.legacyToMiniMessage() ?: ""
                sender.sendMessage("Gave ${amount}x ${material.name} to $suffix${player.name}".blue() + "!".blue())
                sender.inventory.addItem(ItemStack(material, amount))
                player.sendMessage("You received ${amount}x of ${material.name}!".blue())
                return
            } else {
                sender.sendMessage("Gave ${amount}x ${material.name} to all players!".blue())
                player.inventory.addItem(ItemStack(material, amount))
                player.sendMessage("You received ${amount}x of ${material.name}!".blue())
                return
            }
        }

/*
        val enchants: MutableMap<Enchantment, Int> = mutableMapOf()
        if (index < args.size && (args[index].contains("|") || args[index].contains(","))) {
            val enchParts = args[index].split(",")
            for (enchPart in enchParts) {
                val parts = enchPart.split("|")
                val enchName = parts.getOrNull(0)?.uppercase()
                if (enchName != null) {
                    val ench = Enchantment.getByName(enchName)
                    if (ench != null) {
                        val level = parts.getOrNull(1)?.toIntOrNull() ?: 1
                        enchants[ench] = level
                    } else {
                        sender.sendMessage("Invalid enchantment: $enchName".red())
                    }
                }
            }
            index++
        }

        val displayName = if (index < args.size) {
            args.copyOfRange(index, args.size).joinToString(" ").translate()
        } else null

        val item = ItemStack(material, amount)
        val meta = item.itemMeta ?: return
        displayName?.let { meta.setDisplayName(it) }
        enchants.forEach { (ench, level) -> meta.addEnchant(ench, level, true) }
        item.itemMeta = meta
        */
    }

}

