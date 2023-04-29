package kr.hqservice.chest.repository

import kr.hqservice.chest.util.CustomConfig
import java.util.*

class ChestPermissionRepository(private val file: CustomConfig, private val chestConfigRepository: ChestConfigRepository) {

    private val config = file.getConfig()

    private val permissions = mutableMapOf<Int, MutableSet<UUID>>()

    fun load() {
        val chestCount = chestConfigRepository.chestCount
        (1..chestCount).forEach {
            val uuidList = config.getStringList("$it").map(UUID::fromString)
            permissions[it] = uuidList.toMutableSet()
        }
    }

    fun save() {
        for (key in config.getKeys(false)) {
            config.set(key, null)
        }
        permissions.forEach { (order, uuids) ->
            config.set("$order", uuids.map(UUID::toString).toList())
        }
        file.saveConfig()
    }

    fun reload() {
        file.reloadConfig()
        permissions.clear()
        load()
    }

    fun hasPermission(uuid: UUID, order: Int): Boolean {
        return permissions[order]?.contains(uuid) == true
    }

    fun addPermission(uuid: UUID, order: Int) {
        (permissions[order] ?: mutableSetOf<UUID>().apply { permissions[order] = this }).add(uuid)
    }

    fun removePermission(uuid: UUID, order: Int) {
        permissions[order]?.remove(uuid)
    }
}