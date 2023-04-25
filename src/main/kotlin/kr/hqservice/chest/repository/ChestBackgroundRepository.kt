package kr.hqservice.chest.repository

import kr.hqservice.chest.extension.async
import kr.hqservice.chest.util.CustomConfig
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ChestBackgroundRepository(
    private val file: CustomConfig
) {

    private companion object {
        val air = ItemStack(Material.AIR)
    }

    private val config = file.getConfig()

    private var backgrounds = mutableListOf<ItemStack>()

    @Suppress("unchecked_cast")
    fun load() {
        if (config.contains("backgrounds")) {
            backgrounds = config.getList("backgrounds") as MutableList<ItemStack>
        }
    }

    fun save() {
        async {
            config.set("backgrounds", backgrounds)
            file.saveConfig()
        }
    }

    fun reload() {
        file.reloadConfig()
        load()
    }

    fun getBackgroundItems() = backgrounds

    fun setBackgroundItems(contents: Array<ItemStack?>) {
        backgrounds.clear()
        backgrounds.addAll(contents.map { it ?: air })
    }
}