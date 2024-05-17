package com.levishok.market.messages.mapper

import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import com.levishok.market.repositories.ShopUnitRepository
import org.springframework.stereotype.Service
import java.util.stream.Stream

@Service
class ImportShopUnitListDtoConverter(private val repository: ShopUnitRepository) :
    ConverterToStream<ImportShopUnitDtoList, ShopUnit> {

    override fun convert(value: ImportShopUnitDtoList): Stream<ShopUnit> {
        val map = value.items.associate {
            it.id to ShopUnit(it.id, it.name, value.updateDate, it.type, it.price, null)
        }
        for (dto in value.items) {
            if (dto.parentId !== null) {
                map[dto.id]!!.parent = map[dto.parentId] ?: repository.getReferenceById(dto.parentId)
            }
        }
        return map.values.stream()
    }
}
