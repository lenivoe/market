package com.levishok.market.dto.mapper

import com.levishok.market.dto.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import com.levishok.market.repositories.ShopUnitRepository
import org.springframework.stereotype.Service

@Service
class ImportShopUnitDtoListMapper(private val shopUnitRepository: ShopUnitRepository) :
    Mapper<ImportShopUnitDtoList, ShopUnitsList> {

    override fun converse(value: ImportShopUnitDtoList): ShopUnitsList {
        return value.items
            .map {
                val parent = it.parentId?.let(shopUnitRepository::getReferenceById)
//                com.levishok.market.models.ShopUnit(it.id, it.name, value.updateDate, it.type, it.price, parent)
                ShopUnit(
                    it.id,
                    it.name,
                    value.updateDate,
                    it.type,
                    it.price,
                    null
                )
            }
            .let(::ShopUnitsList)
    }
}
