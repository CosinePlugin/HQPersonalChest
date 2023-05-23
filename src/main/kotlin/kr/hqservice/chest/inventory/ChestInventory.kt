package kr.hqservice.chest.inventory

import kr.hqservice.chest.HQPersonalChest
import kr.hqservice.chest.extension.later
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class ChestInventory(
    private val plugin: HQPersonalChest,
    private val player: OfflinePlayer,
    order: Int
) : ChestInventoryHolder("${player.name}님의 창고 : ${order}번", plugin.chestConfigRepository.chestRow) {

    private val chestRepository = plugin.chestRepository

    private val realOrder = order - 1
    private val chestData = chestRepository.getChestData(player.uniqueId)
    private val chestContents = chestData.getContents(realOrder)

    override fun init(inventory: Inventory) {
        if (chestContents == null) return
        val chestRow = plugin.chestConfigRepository.chestRow * 9
        chestContents.forEach { (slot, item) ->
            if (slot <= chestRow) {
                inventory.setItem(slot, item)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        val player = event.player as Player

        chestData.setContents(realOrder, event.inventory.contents)

        player.playSound(player.location, Sound.BLOCK_CHEST_CLOSE, 0.5f, 1f)
        later { ChestMainInventory(plugin, this.player).openInventory(player) }
    }
}
