package kr.hqservice.chest.runnable

import kr.hqservice.chest.repository.ChestPermissionRepository

class ChestPermissionSaveRunnable(
    private val chestPermissionRepository: ChestPermissionRepository
) : Runnable {

    override fun run() {
        chestPermissionRepository.save()
    }
}