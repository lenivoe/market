package com.levishok.market.repositories

import com.levishok.market.models.ShopUnit
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import java.util.stream.Stream
import kotlin.streams.asSequence


@Repository
class PostgresShopUnitRepositoryImpl : PostgresShopUnitRepository {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    override fun upsertAll(entities: Collection<ShopUnit>): List<ShopUnit> {
        val sql = """
            insert into shop_unit (id, name, date, type, price, parent_id)
            values
            ${entities.indices.joinToString(",\n") { "(?, ?, ?, ?, ?, ?)" }}
            on conflict (id) do
            update set
                name = excluded.name,
                date = excluded.date,
                price = excluded.price,
                parent_id = excluded.parent_id
            returning id, name, date, type, price, parent_id
        """
        val query = entityManager.createNativeQuery(sql)

        var parameterIndex = 1
        entities.asSequence()
            .flatMap { with(it) { sequenceOf(id, name, date, type.toString(), price, parent?.id) } }
            .forEach { query.setParameter(parameterIndex++, it) }

        @Suppress("UNCHECKED_CAST")
        return mapQueryResultToShopUnit(query.resultStream as Stream<Array<*>>)
    }

    @Transactional(readOnly = true)
    override fun findByIdWithChildren(id: UUID): ShopUnit? {
        val sql = """
            with recursive parents as (
                select s.id, s.name, s.date, s.type, s.price, s.parent_id
                from shop_unit s
                where s.id = :id
                
                union all
                
                select s.id, s.name, s.date, s.type, s.price, s.parent_id
                from shop_unit s join parents p on s.parent_id = p.id
            )
            select p.id, p.name, p.date, p.type, p.price, p.parent_id
            from parents p
        """
        val query = entityManager.createNativeQuery(sql)
            .setParameter("id", id)

        @Suppress("UNCHECKED_CAST")
        val entities = mapQueryResultToShopUnit(query.resultStream as Stream<Array<*>>)

        if (entities.isEmpty()) {
            return null
        }

        var unit = entities[0]
        while (unit.parent !== null) {
            unit = unit.parent!!
        }
        return unit
    }

    @Transactional
    override fun updateAncestorsDate(ids: Iterable<UUID>, date: Instant) {
        if (!ids.iterator().hasNext()) {
            return
        }

        val sql = """
            with recursive ancestors as (
                select s.id, s.parent_id
                from shop_unit s
                where s.id in (:ids)
                
                union
                
                select s.id, s.parent_id
                from shop_unit s join ancestors a on (s.id = a.parent_id)
            )
            update shop_unit s
            set date = :date
            from ancestors a
            where s.id = a.id
        """
        entityManager.createNativeQuery(sql)
            .setParameter("ids", ids)
            .setParameter("date", date)
            .executeUpdate()
    }

    private fun mapQueryResultToShopUnit(data: Stream<Array<*>>): List<ShopUnit> {
        data class ShopUnitBuilder(
            val entity: ShopUnit,
            val parentId: UUID?,
            val children: MutableList<ShopUnit> = mutableListOf(),
        )

        val idToBuilder = data.asSequence()
            .map {
                val id = it[0] as UUID
                val name = it[1] as String
                val date = it[2] as Instant
                val type = ShopUnit.Type.valueOf(it[3] as String)
                val price = it[4] as Int?
                val parentId = it[5] as UUID?
                ShopUnitBuilder(ShopUnit(id, name, date, type, price, null), parentId)
            }
            .associateBy { it.entity.id }

        for ((entity, parentId, _) in idToBuilder.values) {
            if (parentId !== null) {
                val parentBuilder = idToBuilder[parentId]
                if (parentBuilder !== null) {
                    entity.parent = parentBuilder.entity
                    parentBuilder.children.add(entity)
                } else {
                    entity.parent = entityManager.getReference(ShopUnit::class.java, parentId)
                }
            }
        }

        return idToBuilder.values.map { (entity, _, children) ->
            entity.children = children
            entity
        }
    }
}
