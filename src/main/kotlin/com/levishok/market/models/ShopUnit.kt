package com.levishok.market.models

import jakarta.persistence.*
import org.hibernate.annotations.Check
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant
import java.util.*

@Entity(name = "ShopUnit")
@Table(name = "shop_unit", indexes = [Index(columnList = "parent_id", name = "shop_unit__parent_id__index")])
@Check(constraints = "(type = 'OFFER' AND price IS NOT NULL) OR (type = 'CATEGORY' AND price IS NULL)")
open class ShopUnit(
    @Id
    @Column(name = "id")
    open var id: UUID,

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    open var name: String,

    @Column(name = "date", nullable = false)
    open var date: Instant,

    // TODO: create mapping to Postgres enum
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    open var type: Type,

    @Column(name = "price")
    open var price: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    open var parent: ShopUnit? = null,

    /** readonly property */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, targetEntity = ShopUnit::class, cascade = [])
    open var children: List<ShopUnit> = listOf(),
) {
    enum class Type { OFFER, CATEGORY }

    override fun equals(other: Any?): Boolean =
        this === other || other is ShopUnit && id == other.id

    override fun hashCode(): Int = Objects.hashCode(id)

    override fun toString(): String = "ShopUnit {id=$id}"
}
