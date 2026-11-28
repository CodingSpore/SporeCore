package me.clearedSpore.sporeCore.commands.util

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.clearedSpore.sporeAPI.menu.Menu
import me.clearedSpore.sporeAPI.menu.item.Item
import me.clearedSpore.sporeAPI.util.CC.blue
import me.clearedSpore.sporeAPI.util.CC.translate
import me.clearedSpore.sporeAPI.util.Message.sendSuccessMessage
import me.clearedSpore.sporeCore.SporeCore
import me.clearedSpore.sporeAPI.annotation.RegisterCommand
import me.clearedSpore.sporeAPI.util.CC.legacyToMiniMessage
import me.clearedSpore.sporeAPI.util.CC.red
import me.clearedSpore.sporeCore.features.chat.channel.ChatChannelService.chatService
import me.clearedSpore.sporeCore.hook.PlaceholderAPIHook
import me.clearedSpore.sporeCore.inventory.InventoryManager
import me.clearedSpore.sporeCore.util.Perm
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@CommandAlias("util")
@CommandPermission(Perm.UTIL_INVENTORY)
@RegisterCommand
class UtilInventoryCommand : SporeCommand() {

    @Command("util inventory view <player>")
    @Permission(Perm.UTIL_INVENTORY_VIEW)
    fun view(sender: Player, @Argument("player", suggestions = "players") target: Player) {
        val target = target.player as Player
        if (!target.isOnline) {
            sender.sendMessage("${target.name} is not online!".red())
            return
        }

        InventoryPreviewMenu(target, sender).open(sender)
    }

    @Command("util inventory dropall <player>")
    @Permission(Perm.UTIL_INVENTORY_DROPALL)
    fun dropAll(sender: Player, @Argument("player", suggestions = "players") target: Player) {
        val target = target.player as Player
        if (!target.isOnline) {
            sender.sendMessage("${target.name} is not online!".red())
            return
        }

        var suffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""

        target.inventory.contents.forEach { item ->
            if (item != null && item.type != Material.AIR) target.world.dropItemNaturally(target.location, item)
        }

        InventoryManager.clearPlayerInventory(target)
        sender.sendSuccessMessage("Dropped all items from $suffix${target.name}".translate() + "'s inventory.".blue())
    }

    @Command("util inventory giveall")
    @Permission(Perm.UTIL_INVENTORY_GIVEALL)
    fun giveAll(sender: Player) {
        val items = sender.inventory.contents.filterNotNull()

        for (player in Bukkit.getOnlinePlayers()) {
            player.inventory.addItem(*items.toTypedArray())
        }

        sender.sendSuccessMessage("Gave your inventory to all online players.")
    }

    @Command("util inventory give <player>")
    @Permission(Perm.UTIL_INVENTORY_GIVE)
    fun give(sender: Player, @Argument("player", suggestions = "players") target: Player) {
        if (!target.isOnline) {
            sender.sendMessage("${target.name} is not online!".red())
            return
        }

        val target = target.player as Player
        var suffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""
        val items = sender.inventory.contents.filterNotNull()
        target.inventory.addItem(*items.toTypedArray())

        sender.sendSuccessMessage("Gave your inventory to $suffix${target.name}".translate() + ".".blue())
    }

    @Command("util inventory clearall <material>")
    @Permission(Perm.UTIL_INVENTORY_CLEARALL)
    fun clearAll(sender: Player, @Argument("material", suggestions = "materials") material: Material) {
        val item = ItemStack(material)

        for (player in Bukkit.getOnlinePlayers()) {
            player.inventory.remove(item)
        }

        sender.sendSuccessMessage("Cleared all ${material.name.lowercase()} from all inventories.")
    }

    private class InventoryPreviewMenu(
        private val target: Player,
        private val viewer: Player
    ) : Menu(SporeCore.Companion.instance) {

        override fun fillEmptySlots() = true
        override fun getMenuName(): String = "${target.name}'s Inventory"
        override fun getRows(): Int = 6

        override fun setMenuItems() {
            var index = 0
            for (y in 1..9) {
                for (x in 1..9) {
                    val item = target.inventory.contents.getOrNull(index) ?: ItemStack(Material.AIR)
                    setMenuItem(x, y, SimpleItem(item))
                    index++
                }
            }

            for (i in target.inventory.armorContents.indices) {
                val item = target.inventory.armorContents[i] ?: ItemStack(Material.AIR)
                setMenuItem(i + 1, 5, SimpleItem(item))
            }

            val offhand = target.inventory.itemInOffHand ?: ItemStack(Material.AIR)
            setMenuItem(6, 5, SimpleItem(offhand))

            val expItem = ItemStack(Material.EXPERIENCE_BOTTLE)
            val expMeta = expItem.itemMeta
            expMeta?.setDisplayName("Experience: &f${target.exp} &cbXP, Level &f${target.level}".blue())
            expMeta?.lore = listOf(
                "Left click to add XP".blue(),
                "Right click to remove XP".blue(),
                "Shift Left click to add a level".blue(),
                "Shift right click to remove a level".blue()
            )
            expItem.itemMeta = expMeta
            if (viewer.hasPermission(Perm.ADMIN)) {
                setMenuItem(8, 6, object : Item() {
                    override fun createItem(): ItemStack = expItem
                    override fun onClickEvent(clicker: Player, clickType: ClickType) {
                        when (clickType) {
                            ClickType.LEFT -> target.giveExp(1)
                            ClickType.RIGHT -> target.giveExp(-1)
                            ClickType.SHIFT_LEFT -> target.level += 1
                            ClickType.SHIFT_RIGHT -> target.level = maxOf(0, target.level - 1)
                            else -> {}
                        }
                    }
                })
            }

            for (i in 0 until 4) {
                val craftingItem = target.openInventory.topInventory.contents.getOrNull(i) ?: ItemStack(Material.AIR)
                setMenuItem(i + 6, 1, SimpleItem(craftingItem))
            }

            val clearItem = ItemStack(Material.BARRIER)
            val clearMeta = clearItem.itemMeta
            clearMeta?.setDisplayName("Clear Inventory".blue())
            clearItem.itemMeta = clearMeta
            if (viewer.hasPermission(Perm.ADMIN)) {
                setMenuItem(9, 6, object : Item() {
                    override fun createItem(): ItemStack = clearItem
                    override fun onClickEvent(clicker: Player, clickType: ClickType) {
                        target.inventory.clear()
                        target.updateInventory()
                        var suffix = chatService?.getPlayerSuffix(target)?.legacyToMiniMessage() ?: ""
                        viewer.sendSuccessMessage("$suffix${target.name}".translate() + "'s inventory has been cleared.".blue())
                    }
                })
            }
        }

        private class SimpleItem(private val stack: ItemStack) : Item() {
            override fun createItem(): ItemStack = stack
            override fun onClickEvent(clicker: Player, clickType: ClickType) {}
        }
    }
}