package kr.hqservice.chest.data

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

class ChestData(folder: File, owner: UUID) {

    private companion object {
        val air = ItemStack(Material.AIR)
    }

    private val file = File(folder, "$owner.yml")
    private val contentsList = ArrayList<Array<ItemStack>>()
    var isChanged = false

    init { load() }

    @Suppress("unchecked_cast")
    fun load() {
        if (!file.exists()) {
            // 4번 상자까지 있는거임
            repeat(4) {
                contentsList.add(Array(54) { ItemStack(Material.AIR) })
            }
            return
        }
        val config = YamlConfiguration.loadConfiguration(file)
        val contentsSection = config.getConfigurationSection("contents")
        for (key in contentsSection.getKeys(false)) {
            val contents = contentsSection.getList(key) as List<ItemStack>
            val items = contents.toTypedArray()
            contentsList.add(items)
        }
    }

    fun save() {
        val config = YamlConfiguration.loadConfiguration(file)
        config.set("contents", null)
        contentsList.forEachIndexed { index, contents ->
            config.set("contents.$index", contents.toList())
        }
        config.save(file)
        isChanged = false
    }

    fun getContents(order: Int): Array<ItemStack> {
        return contentsList[order]
    }

    fun setContents(order: Int, contents: Array<ItemStack?>) {
        contentsList[order] = contents.map { it ?: air }.toTypedArray()
        isChanged = true
    }
}
