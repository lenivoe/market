package com.levishok.market.dto.mapper

interface Mapper<T1, T2> {
    fun converse(value: T1): T2
}
