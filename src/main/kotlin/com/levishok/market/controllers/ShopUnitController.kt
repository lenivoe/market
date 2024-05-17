package com.levishok.market.controllers

import com.levishok.market.messages.GetShopUnitDto
import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.messages.mapper.Converter
import com.levishok.market.models.ShopUnit
import com.levishok.market.services.ShopUnitService
import com.levishok.market.services.Validator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
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
        if (!importDtoValidator.isCorrect(dto)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
        shopUnitService.save(dto)
    }

    @GetMapping("/nodes/{id}")
    fun getShopUnit(@PathVariable id: UUID): GetShopUnitDto {
        val shopUnit = shopUnitService.find(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return shopUnitToDtoConverter.convert(shopUnit)
    }
}
