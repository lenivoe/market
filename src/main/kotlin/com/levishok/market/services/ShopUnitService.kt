package com.levishok.market.services

import com.levishok.market.dto.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import java.util.UUID

interface ShopUnitService {
    fun save(dto: ImportShopUnitDtoList): List<ShopUnit>

    fun save(units: Collection<ShopUnit>): List<ShopUnit>
    fun find(id: UUID): ShopUnit?
    fun delete(id: UUID): Boolean
}
