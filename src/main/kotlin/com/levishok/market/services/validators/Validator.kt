package com.levishok.market.services.validators

interface Validator<T> {
    fun isCorrect(value: T): Boolean

    @Throws(IncorrectValueException::class)
    fun requireCorrect(value: T, getErrorMessage: (() -> String)? = null) {
        if (!isCorrect(value)) {
            throw IncorrectValueException(getErrorMessage?.invoke())
        }
    }
}
