package me.clearedSpore.sporeCore.commands

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.*
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Message.sendErrorMessage
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.jetbrains.annotations.Range

@RegisterCommand
class EditItemCommand : SporeCommand() {

    private fun getItemMeta(player: Player): ItemMeta? {
        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            player.sendErrorMessage("You must be holding an item!")
            return null
        }
        return item.itemMeta
    }

    private fun getItem(player: Player) = player.inventory.itemInMainHand


    @Command("edititem rename <name>")
    @Permission(Perm.ITEM_EDITOR)
    fun onRename(player: Player, @Argument("name") name: String) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        meta.setDisplayName(name.translate())
        item.itemMeta = meta
        player.sendSuccessMessage("Changed the item name to '${name.translate()}'")
    }


    @Command("edititem enchant <enchantment> <level>")
    @Permission(Perm.ITEM_EDITOR)
    fun onEnchant(
        player: Player,
        @Argument("enchantment", suggestions = "enchants") enchant: Enchantment,
        @Argument("level") @org.incendo.cloud.annotation.specifier.Range(min = "1", max = "255") levelStr: Int?) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        val level = levelStr ?: 1
        meta.addEnchant(enchant, level, true)
        item.itemMeta = meta

        player.sendSuccessMessage("Successfully added ${enchant.key.key.lowercase()} level $level to your item.")
    }


    @Command("edititem lore add <text>")
    @Permission(Perm.ITEM_EDITOR)
    fun onLoreAdd(player: Player, @Argument("text") text: String) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        lore.add(text.translate())
        meta.lore = lore
        item.itemMeta = meta

        player.sendSuccessMessage("Added lore line: '${text.translate()}'")
    }

    @Command("edititem lore set <line> <text>")
    @Permission(Perm.ITEM_EDITOR)
    fun onLoreSet(player: Player, @Argument("line") line: Int, @Argument("text") text: String) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        if (line < 1 || line > lore.size) {
            player.sendErrorMessage("Invalid line number!")
            return
        }
        lore[line - 1] = text.translate()
        meta.lore = lore
        item.itemMeta = meta

        player.sendSuccessMessage("Set lore line $line to '${text.translate()}'")
    }

    @Command("edititem lore remove <line>")
    @Permission(Perm.ITEM_EDITOR)
    fun onLoreRemove(player: Player, @Argument("line") line: Int) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        if (line < 1 || line > lore.size) {
            player.sendErrorMessage("Invalid line number!")
            return
        }
        val removed = lore.removeAt(line - 1)
        meta.lore = lore
        item.itemMeta = meta

        player.sendSuccessMessage("Removed lore line: '$removed'")
    }


    @Subcommand("attribute add")
    @Syntax("<attribute> <amount>")
    @CommandCompletion("@attributes")
    fun onAttributeAdd(player: Player, attribute: Attribute, amount: Double) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        meta.addAttributeModifier(
            attribute,
            org.bukkit.attribute.AttributeModifier(
                "custom",
                amount,
                org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER
            )
        )
        item.itemMeta = meta

        player.sendSuccessMessage("Added $amount to attribute ${attribute.name.lowercase()}")
    }

    @Subcommand("attribute remove")
    @Syntax("<attribute>")
    @CommandCompletion("@attributes")
    fun onAttributeRemove(player: Player, attribute: Attribute) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        val modifiers = meta.getAttributeModifiers(attribute)
            ?: return player.sendErrorMessage("No modifiers for ${attribute.name.lowercase()} found")
        modifiers.forEach { meta.removeAttributeModifier(attribute, it) }
        item.itemMeta = meta

        player.sendSuccessMessage("Removed all modifiers for attribute ${attribute.name.lowercase()}")
    }


    @Subcommand("unbreakable")
    @Syntax("<true|false>")
    @CommandCompletion("true|false")
    fun onUnbreakable(player: Player, value: Boolean) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        meta.isUnbreakable = value
        item.itemMeta = meta

        player.sendSuccessMessage("Set unbreakable: $value")
    }

    @Subcommand("glow")
    @Syntax("<true|false>")
    @CommandCompletion("true|false")
    fun onGlow(player: Player, value: Boolean) {
        val item = getItem(player)
        val meta = getItemMeta(player) ?: return

        if (value) {
            meta.setEnchantmentGlintOverride(true)
        } else {
            meta.setEnchantmentGlintOverride(false)
        }
        item.itemMeta = meta

        player.sendSuccessMessage("Set glowing: $value")
    }

    @Subcommand("amount")
    @Syntax("<amount>")
    fun onAmount(player: Player, amount: Int) {
        val item = getItem(player)
        if (amount < 1) return player.sendErrorMessage("Amount must be at least 1")
        item.amount = amount
        player.sendSuccessMessage("Set item amount to $amount")
    }

    @Subcommand("durability")
    @Syntax("<damage>")
    fun onDurability(player: Player, damage: Int) {
        val item = getItem(player)
        val meta =
            getItemMeta(player) as? Damageable ?: return player.sendErrorMessage("This item cannot have durability")
        meta.damage = damage
        item.itemMeta = meta
        player.sendSuccessMessage("Set item durability to $damage")
    }

    @Subcommand("type")
    @Syntax("<material>")
    @CommandCompletion("@materials")
    fun onType(player: Player, material: Material) {
        val item = getItem(player)
        item.type = material
        player.sendSuccessMessage("Changed item type to ${material.name.lowercase()}")
    }

    @Subcommand("flag add")
    @Syntax("<flag>")
    @CommandCompletion("@itemflags")
    fun onFlagAdd(player: Player, flag: ItemFlag) {
        val meta = getItemMeta(player) ?: return
        meta.addItemFlags(flag)
        getItem(player).itemMeta = meta
        player.sendSuccessMessage("Added item flag ${flag.name.lowercase()}")
    }

    @Subcommand("flag remove")
    @Syntax("<flag>")
    @CommandCompletion("@itemflags")
    fun onFlagRemove(player: Player, flag: ItemFlag) {
        val meta = getItemMeta(player) ?: return
        meta.removeItemFlags(flag)
        getItem(player).itemMeta = meta
        player.sendSuccessMessage("Removed item flag ${flag.name.lowercase()}")
    }

    @Subcommand("modeldata")
    @Syntax("<data>")
    fun onModelData(player: Player, data: Int) {
        val meta = getItemMeta(player) ?: return
        meta.setCustomModelData(data)
        getItem(player).itemMeta = meta
        player.sendSuccessMessage("Set custom model data to $data")
    }
}
