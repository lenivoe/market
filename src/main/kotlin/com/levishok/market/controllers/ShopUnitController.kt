package com.levishok.market.controllers

import com.levishok.market.dto.ImportShopUnitDtoList
import com.levishok.market.dto.mapper.Mapper
import com.levishok.market.dto.mapper.ShopUnitsList
import com.levishok.market.services.ShopUnitService
import com.levishok.market.services.Validator
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/")
class ShopUnitController(
    private val shopUnitService: ShopUnitService,
    private val importDtoValidator: Validator<ImportShopUnitDtoList>,
//    private val toEntityMapper: Mapper<ImportShopUnitDtoList, ShopUnitsList>
) {
    @Transactional
    @PostMapping("/imports")
    fun importShopUnits(@RequestBody dto: ImportShopUnitDtoList) {
        if (!importDtoValidator.isCorrect(dto)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
//        shopUnitService.save(toEntityMapper.converse(dto).items)
        shopUnitService.save(dto)
    }
}
