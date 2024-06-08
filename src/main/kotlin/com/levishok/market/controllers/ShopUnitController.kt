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
import java.util.*


@RestController
@RequestMapping("/nodes")
class ShopUnitController(
    private val shopUnitService: ShopUnitService,
    private val importDtoValidator: Validator<ImportShopUnitDtoList>,
    private val shopUnitToDtoConverter: Converter<ShopUnit, GetShopUnitDto>,
) {
    @PostMapping
    fun importShopUnits(@RequestBody dto: ImportShopUnitDtoList) {
        importDtoValidator.requireCorrect(dto)
        shopUnitService.save(dto)
    }

    @GetMapping("/{id}")
    fun getShopUnit(@PathVariable id: UUID): GetShopUnitDto {
        val shopUnit = shopUnitService.find(id) ?: throw MissingEntityException()
        return shopUnitToDtoConverter.convert(shopUnit)
    }

    @DeleteMapping("/{id}")
    fun deleteShopUnit(@PathVariable id: UUID) {
        if (!shopUnitService.delete(id)) {
            throw MissingEntityException()
        }
    }

//    @GetMapping("/sales")
//    fun getSalesStatistic(@RequestParam(required = true) date: Instant): List<ShopUnitStatisticDto> {
//        throw NotImplementedError()
//    }
//
//    @GetMapping("/{id}/statistic")
//    fun getShopUnitStatistic(@PathVariable id: UUID): List<ShopUnitStatisticDto> {
//        throw NotImplementedError()
//    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        value = [
            IllegalArgumentException::class,
            IncorrectValueException::class,
            HttpMessageNotReadableException::class,
            MethodArgumentNotValidException::class
        ]
    )
    private fun handleIncorrectValue(): ErrorMessage {
        return ErrorMessage(HttpStatus.BAD_REQUEST, "Validation Failed")
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = [MissingEntityException::class])
    private fun handleMissingEntity(): ErrorMessage {
        return ErrorMessage(HttpStatus.NOT_FOUND, "Item not found")
    }
}
