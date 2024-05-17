package com.levishok.market.services

import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.messages.mapper.ConverterToStream
import com.levishok.market.models.ShopUnit
import com.levishok.market.repositories.ShopUnitRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class ShopUnitServiceImpl(
    private val repository: ShopUnitRepository,
    private val toEntityConverter: ConverterToStream<ImportShopUnitDtoList, ShopUnit>
) : ShopUnitService {

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Throws(IllegalArgumentException::class)
    override fun save(dto: ImportShopUnitDtoList) {
        val units = toEntityConverter.convert(dto).toList()
        val parentsIds = units.asSequence().mapNotNull { it.parent?.id }.toSet()

        if (repository.existsByIdsAndType(parentsIds, ShopUnit.Type.OFFER)) {
            throw IllegalArgumentException("shop unit of type ${ShopUnit.Type.OFFER} cannot be parent")
        }

        try {
            repository.upsertAll(units)

            val ids = units.asSequence().map { it.id }.toSet()
            val parentsForUpdateIds = units.asSequence()
                .mapNotNull { it.parent?.id }
                .filter { !ids.contains(it) }
                .toList()

            repository.updateAncestorsDate(parentsForUpdateIds, dto.updateDate)

        } catch (e: DataIntegrityViolationException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun find(id: UUID): ShopUnit? {
        return repository.findByIdWithChildren(id)
    }

    @Transactional
    override fun delete(id: UUID): Boolean {
        if (repository.existsById(id)) {
            repository.deleteById(id)
            return true
        }
        return false
    }
}
