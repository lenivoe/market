package com.levishok.market.controllers

import com.levishok.market.messages.ErrorMessage
import com.levishok.market.messages.GetShopUnitDto
import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.messages.mapper.Converter
import com.levishok.market.models.ShopUnit
import com.levishok.market.services.MissingEntityException
import com.levishok.market.services.ShopUnitService
import com.levishok.market.services.validators.IncorrectValueException
import com.levishok.market.services.validators.Validator
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import java.util.*


@RestController
@RequestMapping("/")
class ShopUnitController(
    private val shopUnitService: ShopUnitService,
    private val importDtoValidator: Validator<ImportShopUnitDtoList>,
    private val shopUnitToDtoConverter: Converter<ShopUnit, GetShopUnitDto>,
) {
    @PostMapping("/imports")
    fun importShopUnits(@RequestBody dto: ImportShopUnitDtoList) {
        importDtoValidator.requireCorrect(dto)
        shopUnitService.save(dto)
    }

    @GetMapping("/nodes/{id}")
    fun getShopUnit(@PathVariable id: UUID): GetShopUnitDto {
        val shopUnit = shopUnitService.find(id) ?: throw MissingEntityException()
        return shopUnitToDtoConverter.convert(shopUnit)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteShopUnit(@PathVariable id: UUID) {
        if (!shopUnitService.delete(id)) {
            throw MissingEntityException()
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        value = [
            IllegalArgumentException::class,
            IncorrectValueException::class,
            HttpMessageNotReadableException::class,
            MethodArgumentNotValidException::class
        ]
    )
    private fun handleIncorrectValue(ex: Exception, request: WebRequest): ErrorMessage {
        return ErrorMessage(HttpStatus.BAD_REQUEST, "Validation Failed")
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = [MissingEntityException::class])
    private fun handleMissingEntity(ex: Exception, request: WebRequest): ErrorMessage {
        return ErrorMessage(HttpStatus.NOT_FOUND, "Item not found")
    }
}
