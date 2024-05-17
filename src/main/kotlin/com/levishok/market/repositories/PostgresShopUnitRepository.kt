package com.levishok.market.repositories

import com.levishok.market.models.ShopUnit
import java.time.Instant
import java.util.*

interface PostgresShopUnitRepository {
    fun upsertAll(entities: Collection<ShopUnit>): List<ShopUnit>

    fun findByIdWithChildren(id: UUID): ShopUnit?

    fun updateAncestorsDate(ids: Iterable<UUID>, date: Instant)
}