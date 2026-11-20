package me.clearedSpore.sporeCore.commands.currency

import me.clearedSpore.sporeAPI.command.SporeCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import me.clearedSpore.sporeCore.features.currency.menu.main.CurrencyMainMenu
import org.bukkit.entity.Player

@CommandAlias("%currencyshopalias")
class CurrencyShopCommand : SporeCommand() {

    @Default()
    fun onShop(player: Player) {
        CurrencyMainMenu(player).open(player)
    }
}