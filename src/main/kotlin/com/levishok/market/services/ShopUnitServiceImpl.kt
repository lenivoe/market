package com.levishok.market.services

import com.levishok.market.dto.ImportShopUnitDtoList
import com.levishok.market.dto.mapper.Mapper
import com.levishok.market.dto.mapper.ShopUnitsList
import com.levishok.market.models.ShopUnit
import com.levishok.market.repositories.ShopUnitRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class ShopUnitServiceImpl(
    private val shopUnitRepository: ShopUnitRepository,

    private val toEntityMapper: Mapper<ImportShopUnitDtoList, ShopUnitsList>
) : ShopUnitService {

    @Transactional
    override fun save(dto: ImportShopUnitDtoList): List<ShopUnit> {
        return save(toEntityMapper.converse(dto).items)
    }



    @Transactional
    override fun save(units: Collection<ShopUnit>): List<ShopUnit> {
        if (shopUnitRepository.existsByIdsAndType(units.mapNotNull { it.parent?.id }, ShopUnit.Type.OFFER)) {
            throw IllegalArgumentException("shop unit of type ${ShopUnit.Type.OFFER} cannot be parent")
        }
        return shopUnitRepository.upsertAll(units)
    }

    override fun find(id: UUID): ShopUnit? {
        return shopUnitRepository.findByIdOrNull(id)
    }

    @Transactional
    override fun delete(id: UUID): Boolean {
        if (shopUnitRepository.existsById(id)) {
            shopUnitRepository.deleteById(id)
            return true
        }
        return false
    }
}