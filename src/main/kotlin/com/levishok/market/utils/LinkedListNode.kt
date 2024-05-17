package com.levishok.market.utils


class LinkedListNode<T>(val value: T, var prev: LinkedListNode<T>? = null, var next: LinkedListNode<T>? = null) {
    fun insertPrev(node: LinkedListNode<T>) {
        prev?.next = node
        node.prev = prev
        node.next = this
        prev = node
    }

    fun insertNext(node: LinkedListNode<T>) {
        next?.prev = node
        node.next = next
        node.prev = this
        next = node
    }

    fun insertPrev(head: LinkedListNode<T>, tail: LinkedListNode<T>) {
        prev?.next = head
        head.prev = prev
        tail.next = this
        prev = tail
    }

    fun insertNext(head: LinkedListNode<T>, tail: LinkedListNode<T>) {
        next?.prev = tail
        tail.next = next
        head.prev = this
        next = head
    }

    fun toList(): List<T> {
        val list = mutableListOf(this.value)
        var cur = next
        while (cur !== null) {
            list.add(cur.value)
            cur = cur.next
        }
        return list
    }
}
