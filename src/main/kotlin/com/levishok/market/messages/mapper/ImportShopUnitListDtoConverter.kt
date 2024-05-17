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


//    override fun convert(value: ImportShopUnitDtoList): Stream<ShopUnit> {
//        val idToEntity = value.items.associate {
//            it.id to ShopUnit(it.id, it.name, value.updateDate, it.type, it.price, null)
//        }
//        for (dto in value.items) {
//            if (dto.parentId !== null) {
//                idToEntity[dto.id]!!.parent = idToEntity[dto.parentId] ?: repository.getReferenceById(dto.parentId)
//            }
//        }
//
//        val idToNode = HashMap<UUID, LinkedListNode<ShopUnit>>()
//        var sortedEntities: LinkedListNode<ShopUnit>? = null
//
//        for (entity in idToEntity.values) {
//            if (!idToNode.containsKey(entity.id)) {
//                var head = LinkedListNode(entity)
//                val tail = head
//                idToNode[entity.id] = head
//
//                var ancestor = idToEntity[entity.parent?.id]
//                var ancestorNode = idToNode[ancestor?.id]
//                while (ancestor !== null && ancestorNode === null) {
//                    head = LinkedListNode(ancestor).also(head::insertPrev)
//                    idToNode[ancestor.id] = head
//
//                    ancestor = idToEntity[ancestor.parent?.id]
//                    ancestorNode = idToNode[ancestor?.id]
//                }
//
//                if (ancestorNode === null) {
//                    sortedEntities?.insertPrev(head, tail)
//                    sortedEntities = head
//                } else {
//                    ancestorNode.insertNext(head, tail)
//                }
//            }
//        }
//
//        return (sortedEntities?.toList() ?: listOf()).stream()
//    }
}
