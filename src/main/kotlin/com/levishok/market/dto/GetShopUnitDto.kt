package com.levishok.market.dto

import com.levishok.market.models.ShopUnit
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

class GetShopUnitDto(
    val id: UUID,
    val name: String,
    val type: ShopUnit.Type,
    price: Int?,
    val parentId: UUID?,
    val children: List<GetShopUnitDto>?,
    val date: LocalDateTime,
) {
    val price: Int? = price ?: children!!
        .mapNotNull { it.price }
        .reduceOrNull(Int::plus)
        ?.let { (it.toDouble() / children.size).roundToInt() }
}
