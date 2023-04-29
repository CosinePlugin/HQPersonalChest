package kr.hqservice.chest.repository

import kr.hqservice.chest.util.CustomConfig
import java.util.*

class ChestPermissionRepository(private val file: CustomConfig, private val chestCount: Int) {

    private val config = file.getConfig()

    private val permissions = mutableMapOf<Int, MutableSet<UUID>>()

    fun load() {
        (1..chestCount).forEach {
            permissions[it] = config.getStringList("$it").map(UUID::fromString).toMutableSet()
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
        load()
    }

    fun hasPermission(uuid: UUID, order: Int): Boolean {
        return permissions[order]?.contains(uuid) == true
    }

    fun addPermission(uuid: UUID, order: Int) {
        permissions[order]?.add(uuid)
    }

    fun removePermission(uuid: UUID, order: Int) {
        permissions[order]?.remove(uuid)
    }
}