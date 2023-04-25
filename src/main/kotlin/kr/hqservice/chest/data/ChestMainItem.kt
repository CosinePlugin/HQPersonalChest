package kr.hqservice.chest.data

import kr.hqservice.chest.extension.applyColor
import kr.hqservice.chest.inventory.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ChestMainItem(private val material: Material, display: String, lore: List<String>) {

    private val display = display.applyColor()
    private val lore = lore.applyColor()

    fun build(index: Int): ItemStack {
        val finalIndex = index.toString()
        val finalDisplay = display.replace("%index%", finalIndex)
        val finalLore = lore.map { it?.replace("%index%", finalIndex) }
        return ItemBuilder(material).setDisplayName(finalDisplay).setLore(finalLore).build()
    }
}
