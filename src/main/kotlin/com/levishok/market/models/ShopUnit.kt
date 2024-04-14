package com.levishok.market.models

import jakarta.persistence.*
import org.hibernate.annotations.Check
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import java.util.*

@Entity(name = "ShopUnit")
@Table(name = "shop_unit", indexes = [Index(columnList = "parent_id", name = "shop_unit__parent_id__index")])
@Check(constraints = "(type = 'OFFER' AND price IS NOT NULL) OR (type = 'CATEGORY' AND price IS NULL)")
class ShopUnit(
    @Id
    @Column(name = "id")
    var id: UUID,

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    var name: String,

    @Column(name = "date", nullable = false)
    var date: LocalDateTime,

    // TODO: create mapping to Postgres enum
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    var type: Type,

    @Column(name = "price")
    var price: Int? = null,

    parent: ShopUnit? = null,

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    var children: MutableList<ShopUnit>? = null,
) {
    enum class Type { OFFER, CATEGORY }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parent_id")
    var parent: ShopUnit? = parent
        set(value) {
            parent?.children?.remove(this)
            field = value
            value?.children?.add(this)
        }

    override fun equals(other: Any?) =
        this === other || other is ShopUnit && id == other.id

    override fun hashCode() = Objects.hashCode(id)

    override fun toString() = "ShopUnit {id=$id}"
}
