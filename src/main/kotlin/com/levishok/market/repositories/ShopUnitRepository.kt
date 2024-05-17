package com.levishok.market.repositories

import com.levishok.market.models.ShopUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(readOnly = true)
interface ShopUnitRepository : JpaRepository<ShopUnit, UUID>, PostgresShopUnitRepository {
    @Query("""
        select count(id) > 0
        from #{#entityName}
        where id in :ids and type = :type
    """)
    fun existsByIdsAndType(ids: Iterable<UUID>, type: ShopUnit.Type): Boolean
}
