package com.levishok.market.messages.mapper

interface Converter<T1, T2> {
    fun convert(value: T1): T2
}
