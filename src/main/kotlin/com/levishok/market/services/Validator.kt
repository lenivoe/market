package com.levishok.market.services

interface Validator<T> {
    fun isCorrect(value: T): Boolean
}