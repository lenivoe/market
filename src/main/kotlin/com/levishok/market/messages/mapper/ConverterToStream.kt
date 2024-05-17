package com.levishok.market.messages.mapper

import java.util.stream.Stream

interface ConverterToStream<T, ItemT> {
    fun convert(value: T): Stream<ItemT>
}