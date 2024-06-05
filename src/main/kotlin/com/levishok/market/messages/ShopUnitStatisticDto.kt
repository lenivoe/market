package com.levishok.market.messages

import com.levishok.market.models.ShopUnit
import java.time.Instant
import java.util.*

data class ShopUnitStatisticDto(
    val id: UUID,
    val name: String,
    val date: Instant,
    val parentId: UUID,
    val price: Int,
    val type: ShopUnit.Type,
)