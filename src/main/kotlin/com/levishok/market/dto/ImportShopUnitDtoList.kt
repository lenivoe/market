package com.levishok.market.dto

import com.levishok.market.models.ShopUnit
import java.time.LocalDateTime
import java.util.*

class ImportShopUnitDtoList(
    val items: List<Item>,
    val updateDate: LocalDateTime
) {
    class Item(
        val id: UUID,
        val name: String,
        val type: ShopUnit.Type,
        val price: Int?,
        val parentId: UUID?,
    )
}
