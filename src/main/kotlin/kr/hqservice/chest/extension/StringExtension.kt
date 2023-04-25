package kr.hqservice.chest.extension

import org.bukkit.ChatColor

internal fun String.applyColor(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

internal fun List<String>.applyColor(): List<String?> {
    return map { it.applyColor() }
}