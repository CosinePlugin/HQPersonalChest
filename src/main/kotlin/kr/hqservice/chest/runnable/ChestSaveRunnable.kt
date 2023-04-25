package kr.hqservice.chest.runnable

import kr.hqservice.chest.repository.ChestRepository


class ChestSaveRunnable(
    private val chestRepository: ChestRepository
) : Runnable {

    override fun run() {
        chestRepository.saveAll()
    }
}