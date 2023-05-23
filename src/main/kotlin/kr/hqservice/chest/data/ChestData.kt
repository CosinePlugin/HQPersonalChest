package kr.hqservice.chest.data

import kr.hqservice.chest.HQPersonalChest.Companion.plugin
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

class ChestData(folder: File, owner: UUID) {

    private companion object {
        val air = ItemStack(Material.AIR)
        var airs = Array(54) { ItemStack(Material.AIR) }
    }

    private val file = File(folder, "$owner.yml")
    private val contentsList = ArrayList<MutableMap<Int, ItemStack>>()
    var isChanged = false

    init { load() }

    @Suppress("unchecked_cast")
    fun load() {
        if (!file.exists()) {
            val chestCount = plugin.chestConfigRepository.chestCount
            repeat(chestCount) {
                contentsList.add(mutableMapOf())
            }
        }

        val config = YamlConfiguration.loadConfiguration(file)
        val contentSection = config.getConfigurationSection("contents") ?: return

        for (key in contentSection.getKeys(false)) {
            val slotSection = contentSection.getConfigurationSection(key)
            val itemMap = mutableMapOf<Int, ItemStack>()
            for (slot in slotSection.getKeys(false)) {
                itemMap[slot.toInt()] = slotSection.getItemStack(slot)
            }
            contentsList.add(itemMap)
        }
    }

    fun save() {
        val config = YamlConfiguration.loadConfiguration(file)
        config.set("contents", null)
        contentsList.forEachIndexed { index, itemMap ->
            itemMap.forEach { (slot, item) ->
                config.set("contents.$index.$slot", item)
            }
        }
        config.save(file)
        isChanged = false
    }

    fun getContents(order: Int): Map<Int, ItemStack>? {
        return try {
            contentsList[order]
        } catch (_: Exception) {
            null
        }
    }

    fun setContents(order: Int, contents: Array<ItemStack?>) {
        val itemMap = mutableMapOf<Int, ItemStack>().apply {
            contents.forEachIndexed { slot, item ->
                if (item != null) {
                    this[slot] = item
                }
            }
        }
        contentsList[order] = itemMap
        isChanged = true
    }
}
