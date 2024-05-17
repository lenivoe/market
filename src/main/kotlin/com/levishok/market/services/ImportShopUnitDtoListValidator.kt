package com.levishok.market.services

import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import org.springframework.stereotype.Service

@Service
class ImportShopUnitDtoListValidator : Validator<ImportShopUnitDtoList> {
    override fun isCorrect(value: ImportShopUnitDtoList): Boolean {
        val priceIsCorrect = value.items.all {
            when (it.type) {
                ShopUnit.Type.OFFER -> it.price !== null
                ShopUnit.Type.CATEGORY -> it.price === null
            }
        }
        if (!priceIsCorrect) {
            return false
        }

        val idToEntity = value.items.associateBy { it.id }
        return value.items
            .mapNotNull { it.parentId?.let(idToEntity::get) }
            .all { it.type == ShopUnit.Type.CATEGORY }
    }
}
