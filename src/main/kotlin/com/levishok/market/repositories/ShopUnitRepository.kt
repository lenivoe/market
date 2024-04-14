package com.levishok.market.repositories

import com.levishok.market.models.ShopUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
interface ShopUnitRepository : JpaRepository<ShopUnit, UUID> {
    @Query("""
        select
            case
                when count(1) > 0 then true
                else false
            end
        from #{#entityName}
        where id in :ids and type = :type
    """)
    fun existsByIdsAndType(ids: Iterable<UUID>, type: ShopUnit.Type): Boolean

    @Transactional
    fun upsertAll(shopUnits: Collection<ShopUnit>): List<ShopUnit> {
        val existingUnits = findAllById(shopUnits.map { it.id }).associateBy { it.id }
        saveAll(shopUnits.filterNot { existingUnits.contains(it.id) })
        return shopUnits.map {
            val updated = existingUnits[it.id]?.apply {
                name = it.name
                date = it.date
                parent = it.parent
                price = it.price
            }
            updated ?: it
        }
    }

    @Query("select su from #{#entityName} su where su.id in :ids")
    override fun findAllById(ids: Iterable<UUID>): List<ShopUnit>
}
