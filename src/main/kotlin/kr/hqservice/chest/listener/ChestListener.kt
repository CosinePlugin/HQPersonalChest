package kr.hqservice.chest.listener

import kr.hqservice.chest.inventory.ChestInventoryHolder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class ChestListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        event.inventory.holder?.let {
            if (it is ChestInventoryHolder) {
                it.onInventoryClick(event)
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        event.inventory.holder?.let {
            if (it is ChestInventoryHolder) {
                it.onInventoryClose(event)
            }
        }
    }
}

