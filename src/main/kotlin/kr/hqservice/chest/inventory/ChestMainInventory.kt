package kr.hqservice.chest.inventory

import kr.hqservice.chest.HQPersonalChest
import kr.hqservice.chest.extension.later
import kr.hqservice.chest.repository.ChestConfigRepository.Companion.prefix
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

class ChestMainInventory(
    private val plugin: HQPersonalChest,
    private val player: OfflinePlayer
) : ChestInventoryHolder("${player.name}님의 창고 : 메인", plugin.chestConfigRepository.mainRow, true) {

    private val chestConfigRepository = plugin.chestConfigRepository
    private val chestPermissionRepository = plugin.chestPermissionRepository
    private val chestBackgroundRepository = plugin.chestBackgroundRepository

    private val slots = chestConfigRepository.slots
    private val preventSize = plugin.chestConfigRepository.mainRow * 9

    override fun init(inventory: Inventory) {
        chestBackgroundRepository.getBackgroundItems().forEachIndexed { index, item ->
            if (index >= preventSize) return
            inventory.setItem(index, item)
        }
    }

    override fun initWithPlayer(inventory: Inventory, player: Player) {
        slots.forEach { slot ->
            if (slot >= preventSize) return
            val index = slots.indexOf(slot) + 1
            val item = if (player.isOp || chestPermissionRepository.hasPermission(player.uniqueId, index)) {
                chestConfigRepository.accessItem?.build(index)
            } else {
                chestConfigRepository.deniedItem?.build(index)
            }
            inventory.setItem(slot, item)
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return

        val slot = event.rawSlot
        if (!slots.contains(slot)) return

        val player = event.whoClicked as Player
        val index = slots.indexOf(slot) + 1

        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)

        if (!player.isOp && !chestPermissionRepository.hasPermission(player.uniqueId, index)) {
            return
        }
        player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 0.5f, 1f)
        later { ChestInventory(plugin, this.player, index).openInventory(player) }
    }
}
