package com.levishok.market.controllers

import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LogManager.getLogger(RestResponseEntityExceptionHandler::class.java)

    @ExceptionHandler(value = [IllegalArgumentException::class])
    private fun handleIllegalArgument(e: Exception, request: WebRequest): ResponseEntity<Any>? {
        log.info(e)
        return handleExceptionInternal(e, "", HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }
}
