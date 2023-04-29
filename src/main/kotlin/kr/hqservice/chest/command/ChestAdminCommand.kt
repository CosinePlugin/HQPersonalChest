package kr.hqservice.chest.command

import kr.hqservice.chest.HQPersonalChest
import kr.hqservice.chest.extension.*
import kr.hqservice.chest.extension.async
import kr.hqservice.chest.extension.later
import kr.hqservice.chest.inventory.ChestMainBackgroundSettingInventory
import kr.hqservice.chest.inventory.ChestMainInventory
import kr.hqservice.chest.repository.ChestConfigRepository.Companion.prefix
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ChestAdminCommand(private val plugin: HQPersonalChest) : CommandExecutor, TabCompleter {

    private companion object {
        const val open = "열기"
        const val addPermission = "권한추가"
        const val removePermission = "권한제거"
        val permissionTabList = listOf(addPermission, removePermission)
        val commandTabList = mutableListOf(open, "배경설정", "설정리로드").apply { addAll(permissionTabList) }
    }

    private val chestConfigRepository = plugin.chestConfigRepository
    private val chestPermissionRepository = plugin.chestPermissionRepository
    private val chestBackgroundRepository = plugin.chestBackgroundRepository

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<String>): List<String>? {
        if (args.size <= 1) {
            return commandTabList
        }
        if (args.size == 2 && args[0] == open) {
            return null
        }
        if (args.size == 2 && permissionTabList.contains(args[0])) {
            return null
        }
        if (args.size == 3 && permissionTabList.contains(args[0])) {
            return mutableListOf<String>().apply {
                (1..chestConfigRepository.chestCount).forEach {
                    add("$it")
                }
            }
        }
        return emptyList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            printHelp(sender)
            return true
        }
        checker(sender, args)
        return true
    }

    private fun printHelp(sender: CommandSender) {
        sender.sendMessages(
            "$prefix 창고 명령어 도움말",
            "",
            "$prefix /창고관리 열기 [닉네임]",
            "$prefix /창고관리 권한추가 [닉네임] [번호]",
            "$prefix /창고관리 권한제거 [닉네임] [번호]",
            "$prefix /창고관리 배경설정",
            "$prefix /창고관리 설정리로드"
        )
    }

    private fun checker(sender: CommandSender, args: Array<String>) {
        when (args[0]) {
            "열기" -> {
                if (sender !is Player) {
                    sender.sendMessage("$prefix 콘솔에서 사용할 수 없는 명령어입니다.")
                    return
                }
                openChest(sender, args)
            }
            "권한추가" -> addChestPermission(sender, args)
            "권한제거" -> removeChestPermission(sender, args)
            "배경설정" -> {
                if (sender !is Player) {
                    sender.sendMessage("$prefix 콘솔에서 사용할 수 없는 명령어입니다.")
                    return
                }
                setBackground(sender)
            }
            "설정리로드" -> reload(sender)
        }
    }

    private fun getOfflinePlayer(sender: CommandSender, args: Array<String>, block: (OfflinePlayer) -> Unit) {
        if (args.size == 1) {
            sender.sendMessage("$prefix 닉네임을 적어주세요.")
            return
        }
        val server = sender.server
        async {
            val target = server.getOfflinePlayer(args[1]) ?: run {
                sender.sendMessage("$prefix 존재하지 않는 유저입니다.")
                return@async
            }
            if (!target.hasPlayedBefore()) {
                sender.sendMessage("$prefix 존재하지 않는 유저입니다.")
                return@async
            }
            block(target)
        }
    }

    private fun openChest(player: Player, args: Array<String>) {
        getOfflinePlayer(player, args) { target ->
            later { ChestMainInventory(plugin, target).openInventory(player) }
        }
    }

    private fun setPermission(player: CommandSender, args: Array<String>, block: (OfflinePlayer, Int) -> Unit) {
        getOfflinePlayer(player, args) { target ->
            if (args.size == 2) {
                player.sendMessage("$prefix 번호를 입력해주세요.")
                return@getOfflinePlayer
            }
            val numberText = args[2]
            if (!numberText.isInt()) {
                player.sendMessage("$prefix 숫자만 입력 가능합니다.")
                return@getOfflinePlayer
            }
            val number = numberText.toInt()
            if (number !in 1..chestConfigRepository.chestCount) {
                player.sendMessage("$prefix 존재하지 않는 번호입니다.")
                return@getOfflinePlayer
            }
            block(target, number)
        }
    }

    private fun addChestPermission(player: CommandSender, args: Array<String>) {
        setPermission(player, args) { target, number ->
            chestPermissionRepository.addPermission(target.uniqueId, number)
            player.sendMessage("$prefix ${target.name}님에게 ${number}번 창고 권한을 추가하였습니다.")
        }
    }

    private fun removeChestPermission(player: CommandSender, args: Array<String>) {
        setPermission(player, args) { target, number ->
            chestPermissionRepository.removePermission(target.uniqueId, number)
            player.sendMessage("$prefix ${target.name}님의 ${number}번 창고 권한을 제거하였습니다.")
        }
    }

    private fun setBackground(player: Player) {
        later { ChestMainBackgroundSettingInventory(plugin).openInventory(player) }
    }

    private fun reload(player: CommandSender) {
        chestBackgroundRepository.reload()
        chestPermissionRepository.reload()
        chestConfigRepository.reload()
        player.sendMessage("$prefix 설정이 리로드되었습니다.")
    }
}
