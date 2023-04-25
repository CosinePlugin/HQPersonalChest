package kr.hqservice.chest.inventory

import kr.hqservice.chest.HQPersonalChest
import kr.hqservice.chest.repository.ChestConfigRepository.Companion.prefix
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class ChestMainBackgroundSettingInventory(
    plugin: HQPersonalChest
) : ChestInventoryHolder("창고 : 배경 설정", plugin.chestConfigRepository.mainRow) {

    private val chestBackgroundRepository = plugin.chestBackgroundRepository

    override fun init(inventory: Inventory) {
        chestBackgroundRepository.getBackgroundItems().forEachIndexed { index, item ->
            inventory.setItem(index, item)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        chestBackgroundRepository.setBackgroundItems(event.inventory.contents)
        chestBackgroundRepository.save()
        player.sendMessage("$prefix 배경 아이템이 저장되었습니다.")
    }
}