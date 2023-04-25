package kr.hqservice.chest.repository

import kr.hqservice.chest.data.ChestMainItem
import kr.hqservice.chest.extension.applyColor
import kr.hqservice.chest.util.CustomConfig
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ChestConfigRepository(private val file: CustomConfig) {

    companion object {
        var prefix = "§6[ 창고 ]§f"
            private set
    }

    private val config = file.getConfig()

    var mainRow = 3
        private set

    var chestRow = 6
        private set

    val chestCount get() = slots.size

    var slots: List<Int> = mutableListOf(10, 12, 14, 16)
        private set

    var accessItem: ChestMainItem? = null
        private set

    var deniedItem: ChestMainItem? = null
        private set

    fun load() {
        loadSetting()
        loadItem()
    }

    private fun loadSetting() {
        val settingSection = config.getConfigurationSection("setting")
        prefix = settingSection.getString("prefix").applyColor()
        val detailSection = settingSection.getConfigurationSection("details")
        val sizeSection = detailSection.getConfigurationSection("row")
        mainRow = sizeSection.getInt("main")
        chestRow = sizeSection.getInt("chest")
        slots = detailSection.getIntegerList("slots")
    }

    private fun loadItem() {
        val itemSection = config.getConfigurationSection("item")
        accessItem = createChestMainItem(itemSection.getConfigurationSection("access"))
        deniedItem = createChestMainItem(itemSection.getConfigurationSection("denied"))
    }

    private fun createChestMainItem(section: ConfigurationSection): ChestMainItem {
        val material = Material.valueOf(section.getString("material"))
        val display = section.getString("display")
        val lore = section.getStringList("lore")
        return ChestMainItem(material, display, lore)
    }

    fun reload() {
        file.reloadConfig()
        load()
    }
}
