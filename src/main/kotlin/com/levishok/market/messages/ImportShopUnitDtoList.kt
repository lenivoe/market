package com.levishok.market.messages

import com.levishok.market.models.ShopUnit
import java.time.Instant
import java.util.*

data class ImportShopUnitDtoList(val items: List<Item>, val updateDate: Instant) {
    class Item(
        val id: UUID,
        val name: String,
        val type: ShopUnit.Type,
        val price: Int?,
        val parentId: UUID?,
    )
}
