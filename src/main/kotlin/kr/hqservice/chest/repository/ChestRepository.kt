package kr.hqservice.chest.repository

import kr.hqservice.chest.data.ChestData
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.*

class ChestRepository(plugin: Plugin) {

    private val chestFolder = File(plugin.dataFolder, "Chests").apply { if (!exists()) mkdirs() }
    private val chests = mutableMapOf<UUID, ChestData>()

    fun loadAll() {
        chestFolder.listFiles()?.forEach {
            val fileName = it.name.run { substring(0, length - 4) }
            val uuid = UUID.fromString(fileName)

            val chestData = ChestData(chestFolder, uuid)
            chests[uuid] = chestData
        }
    }

    fun saveAll() {
        chests.values.filter { it.isChanged }.forEach { it.save() }
    }

    fun getChestData(uuid: UUID): ChestData {
        return chests[uuid] ?: ChestData(chestFolder, uuid).apply { chests[uuid] = this }
    }
}
