package com.levishok.market.messages

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.levishok.market.models.ShopUnit
import java.time.Instant
import java.util.*
import kotlin.math.roundToInt

@Suppress("CanBeParameter", "unused", "MemberVisibilityCanBePrivate")
class GetShopUnitDto(
    val id: UUID,
    val name: String,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    val date: Instant,
    val type: ShopUnit.Type,
    price: Int?,
    val parentId: UUID?,
    val children: List<GetShopUnitDto>?,
) {
    /**
     * если type == OFFER, price - цена товара,
     *
     * если type == CATEGORY, price - средняя (!) цена товаров из этой категории
     *
     * если в категории нет товаров, в том числе во вложенных категориях, price == null
     */
    val price: Int?
        get() = when (type) {
            ShopUnit.Type.OFFER -> totalPrice
            ShopUnit.Type.CATEGORY -> totalPrice?.let { (it.toDouble() / totalChildrenAmount).roundToInt() }
        }

    @JsonIgnore
    val totalPrice: Int? = when (type) {
        ShopUnit.Type.OFFER -> price!!
        ShopUnit.Type.CATEGORY -> children!!.asSequence()
            .mapNotNull { it.totalPrice }
            .reduceOrNull(Int::plus)
    }

    @JsonIgnore
    val totalChildrenAmount: Int = children?.sumOf { it.totalChildrenAmount } ?: 1
}
