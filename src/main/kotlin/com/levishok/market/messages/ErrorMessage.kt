package com.levishok.market.messages

import org.springframework.http.HttpStatus

data class ErrorMessage(val code: Int, val message: String) {
    constructor(status: HttpStatus, message: String): this(status.value(), message)
}
