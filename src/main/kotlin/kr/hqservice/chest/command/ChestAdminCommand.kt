package kr.hqservice.chest.command

import kr.hqservice.chest.HQPersonalChest
import kr.hqservice.chest.extension.async
import kr.hqservice.chest.extension.later
import kr.hqservice.chest.extension.sendMessages
import kr.hqservice.chest.extension.sync
import kr.hqservice.chest.inventory.ChestInventoryHolder
import kr.hqservice.chest.inventory.ChestMainBackgroundSettingInventory
import kr.hqservice.chest.inventory.ChestMainInventory
import kr.hqservice.chest.repository.ChestConfigRepository.Companion.prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ChestAdminCommand(private val plugin: HQPersonalChest) : CommandExecutor, TabCompleter {

    private companion object {
        const val open = "열기"
        val commandTabList = listOf(open, "배경설정", "설정리로드")
    }

    private val chestConfigRepository = plugin.chestConfigRepository
    private val chestBackgroundRepository = plugin.chestBackgroundRepository

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<String>): List<String>? {
        if (args.size <= 1) {
            return commandTabList
        }
        if (args.size == 2 && args[0] == open) {
            return null
        }
        return emptyList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true

        val player: Player = sender

        if (args.isEmpty()) {
            printHelp(player)
            return true
        }
        checker(player, args)
        return true
    }

    private fun printHelp(player: Player) {
        player.sendMessages(
            "$prefix 창고 명령어 도움말",
            "",
            "$prefix /창고관리 열기 [닉네임]",
            "$prefix /창고관리 배경설정",
            "$prefix /창고관리 설정리로드"
        )
    }

    private fun checker(player: Player, args: Array<String>) {
        when (args[0]) {
            "열기" -> openChest(player, args)
            "배경설정" -> setBackground(player)
            "설정리로드" -> reload(player)
        }
    }

    private fun openChest(player: Player, args: Array<String>) {
        if (args.size == 1) {
            player.sendMessage("$prefix 닉네임을 적어주세요.")
            return
        }
        val server = player.server
        async {
            val target = server.getOfflinePlayer(args[1]) ?: run {
                player.sendMessage("$prefix 존재하지 않는 유저입니다.")
                return@async
            }
            if (!target.hasPlayedBefore()) {
                player.sendMessage("$prefix 존재하지 않는 유저입니다.")
                return@async
            }
            later { ChestMainInventory(plugin, target).openInventory(player) }
        }
    }

    private fun setBackground(player: Player) {
        later { ChestMainBackgroundSettingInventory(plugin).openInventory(player) }
    }

    private fun reload(player: Player) {
        chestBackgroundRepository.reload()
        chestConfigRepository.reload()
        player.sendMessage("$prefix 설정이 리로드되었습니다.")
    }
}
