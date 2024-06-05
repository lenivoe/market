package com.levishok.market.services

import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import java.util.*

interface ShopUnitService {
    fun save(dto: ImportShopUnitDtoList)
    fun find(id: UUID): ShopUnit?
    fun delete(id: UUID): Boolean
}
