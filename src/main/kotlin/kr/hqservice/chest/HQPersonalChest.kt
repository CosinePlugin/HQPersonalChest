package kr.hqservice.chest

import kr.hqservice.chest.bststs.Metrics
import kr.hqservice.chest.command.ChestAdminCommand
import kr.hqservice.chest.command.ChestCommand
import kr.hqservice.chest.listener.ChestListener
import kr.hqservice.chest.repository.ChestBackgroundRepository
import kr.hqservice.chest.repository.ChestConfigRepository
import kr.hqservice.chest.repository.ChestPermissionRepository
import kr.hqservice.chest.repository.ChestRepository
import kr.hqservice.chest.runnable.ChestPermissionSaveRunnable
import kr.hqservice.chest.runnable.ChestRepositorySaveRunnable
import kr.hqservice.chest.util.CustomConfig
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin

class HQPersonalChest : JavaPlugin() {

    companion object {
        lateinit var plugin: HQPersonalChest
            private set
    }

    lateinit var chestBackgroundRepository: ChestBackgroundRepository
        private set

    lateinit var chestConfigRepository: ChestConfigRepository
        private set

    lateinit var chestPermissionRepository: ChestPermissionRepository
        private set

    lateinit var chestRepository: ChestRepository
        private set

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        Metrics(this, 18264)

        val chestConfigFile = CustomConfig(this, "config.yml")
        chestConfigRepository = ChestConfigRepository(chestConfigFile)
        chestConfigRepository.load()

        val chestPermissionFile = CustomConfig(this, "permission.yml")
        chestPermissionRepository = ChestPermissionRepository(chestPermissionFile, chestConfigRepository.chestCount)
        chestPermissionRepository.load()

        val chestBackgroundFile = CustomConfig(this, "background.yml")
        chestBackgroundRepository = ChestBackgroundRepository(chestBackgroundFile)
        chestBackgroundRepository.load()

        chestRepository = ChestRepository(this)
        chestRepository.loadAll()

        server.scheduler.runTaskTimerAsynchronously(this, ChestRepositorySaveRunnable(chestRepository), 2400, 2400)
        server.scheduler.runTaskTimerAsynchronously(this, ChestPermissionSaveRunnable(chestPermissionRepository), 6000, 6000)

        server.pluginManager.registerEvents(ChestListener(), this)

        getCommand("창고").executor = ChestCommand(this)
        getCommand("창고관리").executor = ChestAdminCommand(this)
    }

    private fun registerPermission(count: Int) {
        val pluginManager = server.pluginManager
        val permissions = pluginManager.permissions

        (1..count).forEach {
            val permission = Permission("kr.hqservice.chest.use.$it")
            if (!permissions.contains(permission)) {
                pluginManager.addPermission(permission)
            }
        }
    }

    override fun onDisable() {
        chestRepository.saveAll()
        chestPermissionRepository.save()
    }
}
