package kr.hqservice.chest.inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class ItemBuilder {

    private val item: ItemStack
    private var meta: ItemMeta

    constructor(material: Material?) {
        item = ItemStack(material, 1)
        meta = item.itemMeta
    }

    constructor(material: Material?, amount: Int) {
        item = ItemStack(material, amount)
        meta = item.itemMeta
    }

    fun setDisplayName(name: String?): ItemBuilder {
        meta.displayName = name
        return this
    }

    fun setLore(vararg lores: String?): ItemBuilder {
        meta.lore = Arrays.asList(*lores)
        return this
    }

    fun setLore(lores: List<String?>?): ItemBuilder {
        meta.lore = lores
        return this
    }

    fun setGlow(): ItemBuilder {
        item.setItemMeta(meta)
        item.addUnsafeEnchantment(Enchantment.LURE, 1)
        meta = item.itemMeta
        addItemFlags(ItemFlag.HIDE_ENCHANTS)
        return this
    }

    fun addItemFlags(vararg flags: ItemFlag?): ItemBuilder {
        meta.addItemFlags(*flags)
        return this
    }

    fun hideAllItemFlags(): ItemBuilder {
        meta.addItemFlags(*ItemFlag.values())
        return this
    }

    fun setUnbreakable(): ItemBuilder {
        meta.isUnbreakable = true
        return this
    }

    fun build(): ItemStack {
        item.setItemMeta(meta)
        return item
    }
}
