package com.levishok.market.services.validators

import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import org.springframework.stereotype.Service

@Service
class ImportShopUnitDtoListValidator : Validator<ImportShopUnitDtoList> {
    override fun isCorrect(value: ImportShopUnitDtoList): Boolean {
        val hasCorrectPrice = value.items.all {
            when (it.type) {
                ShopUnit.Type.OFFER -> it.price !== null
                ShopUnit.Type.CATEGORY -> it.price === null
            }
        }
        if (!hasCorrectPrice) {
            return false
        }

        val idToEntity = value.items.associateBy { it.id }
        val areParentsCorrect = value.items.asSequence()
            .filter { it.parentId !== null }
            .mapNotNull { idToEntity[it.parentId] }
            .all { it.type == ShopUnit.Type.CATEGORY }

        return areParentsCorrect
    }
}
