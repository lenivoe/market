package com.levishok.market.dto.mapper

import com.levishok.market.dto.GetShopUnitDto
import com.levishok.market.models.ShopUnit
import org.springframework.stereotype.Component

@Component
class ShopUnitMapper : Mapper<ShopUnit, GetShopUnitDto> {
    override fun converse(value: ShopUnit): GetShopUnitDto {
        return with(value) {
            GetShopUnitDto(id, name, type, price, parent?.id, children?.map(::converse), date)
        }
    }
}
