package kr.hqservice.chest.command

import kr.hqservice.chest.HQPersonalChest
import kr.hqservice.chest.extension.later
import kr.hqservice.chest.inventory.ChestMainInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChestCommand(private val plugin: HQPersonalChest) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return true
        val player: Player = sender
        later { ChestMainInventory(plugin, player).openInventory(player) }
        return true
    }
}
