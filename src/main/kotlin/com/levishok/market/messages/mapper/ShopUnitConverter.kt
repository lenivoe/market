package com.levishok.market.messages.mapper

import com.levishok.market.messages.GetShopUnitDto
import com.levishok.market.models.ShopUnit
import org.springframework.stereotype.Component

@Component
class ShopUnitConverter : Converter<ShopUnit, GetShopUnitDto> {
    override fun convert(value: ShopUnit): GetShopUnitDto {
        val children = when(value.type) {
            ShopUnit.Type.CATEGORY -> value.children.map(this::convert)
            ShopUnit.Type.OFFER -> null
        }
        return GetShopUnitDto(value.id, value.name, value.date, value.type, value.price, value.parent?.id, children)
    }
}
