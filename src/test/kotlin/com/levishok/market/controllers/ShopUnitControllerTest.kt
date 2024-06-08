package com.levishok.market.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.levishok.market.messages.ImportShopUnitDtoList
import com.levishok.market.models.ShopUnit
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class ShopUnitControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val log = LoggerFactory.getLogger(ShopUnitControllerTest::class.java)

    private val ids = arrayOf(
        "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",

        "d515e43f-f3f6-4471-bb77-6b455017a2d2",
        "863e1a7a-1304-42ae-943b-179184c077e3",
        "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",

        "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
        "98883e8f-0507-482f-bce2-2fb306cf6483",
        "74b81fda-9cdc-4b63-8927-c978afed5cf4",

        "73bc3b36-02d1-4245-ab35-3106c9ee1c65"
    )

    private val importDtoList = listOf(
        ImportShopUnitDtoListBuilder("2022-02-01T12:00:00.000Z")
            .addItem("Товары", ShopUnit.Type.CATEGORY, null, ids[0]),

        ImportShopUnitDtoListBuilder("2022-02-02T12:00:00.000Z")
            .addItem("jPhone 13", ShopUnit.Type.OFFER, 79999, ids[2], ids[1])
            .addItem("Xomiа Readme 10", ShopUnit.Type.OFFER, 59999, ids[3], ids[1])
            .addItem("Смартфоны", ShopUnit.Type.CATEGORY, null, ids[1], ids[0]),

        ImportShopUnitDtoListBuilder("2022-02-03T12:00:00.000Z")
            .addItem("Samson 70\" LED UHD Smart", ShopUnit.Type.OFFER, 32999, ids[5], ids[4])
            .addItem("Phyllis 50\" LED UHD Smarter", ShopUnit.Type.OFFER, 49999, ids[6], ids[4])
            .addItem("Телевизоры", ShopUnit.Type.CATEGORY, null, ids[4], ids[0]),

        ImportShopUnitDtoListBuilder("2022-02-03T21:30:00.000Z")
            .addItem("Goldstar 65\" LED UHD LOL Very Smart", ShopUnit.Type.OFFER, 69999, ids[7], ids[4]),
    ).map { it.build() }

    @BeforeEach
    fun clear() {
        transactionTemplate.executeWithoutResult {
            entityManager.createNativeQuery("truncate table shop_unit").executeUpdate()
        }
    }

    @Test
    fun importsReturns200() {
        for ((i, dto) in importDtoList.withIndex()) {
            log.info(">>> test part #$i <<<")

            val body = objectMapper.writeValueAsString(dto)
            val result = sendRequest(HttpMethod.POST, "/nodes", null, body)
                .andReturn()

            assertEquals(200, result.response.status) { "part #$i: expected code 200, actual code $result" }
        }
    }

    @ParameterizedTest
    @MethodSource("importReturns400Provider")
    fun importReturns400(body: String) {
        sendRequest(HttpMethod.POST, "/nodes", HttpStatus.BAD_REQUEST, body)
    }

    companion object {
        @JvmStatic
        fun importReturns400Provider(): Array<String> {
            val rootId = UUID.randomUUID().toString()
            val childId = UUID.randomUUID().toString()

            return arrayOf(
                """{
                    "items": [ ${createShopUnitJson("товар без цены", "OFFER", null, rootId)} ],
                    "updateDate": "2022-02-01T12:00:00.000Z"
                }""".trimIndent(),

                """{
                    "items": [ ${createShopUnitJson("категория с ценой", "CATEGORY", 499, rootId)} ],
                    "updateDate": "2022-02-01T12:00:00.000Z"
                }""".trimIndent(),

                """{
                    "items": [ ${createShopUnitJson("некорректная дата", "CATEGORY", null, rootId)} ],
                    "updateDate": "01-02-22T12:00:00.000Z"
                }""".trimIndent(),

                """{
                    "items": [
                        ${createShopUnitJson("родитель-offer", "OFFER", 123, rootId)},
                        ${createShopUnitJson("любой ребенок", "OFFER", 456, childId, rootId)},
                    ],
                    "updateDate": "2022-02-01T12:00:00.000Z"
                }""".trimMargin()
            )
        }

        private fun createShopUnitJson(
            name: String, type: String, price: Int?, id: String, parentId: String? = null, date: String? = null
        ): String {
            val dto = mutableListOf(
                "name" to name,
                "type" to type,
                "id" to id,
                "parentId" to parentId,
                "price" to price
            )

            if (date !== null) {
                dto.add("date" to date)
            }

            val fields = dto.map { (key, value) ->
                if (value !== null && value is String) {
                    "\"$key\": \"$value\""
                } else {
                    "\"$key\": $value"
                }
            }

            return "{ ${fields.joinToString(", ")} }"
        }
    }

    @Test
    fun getReturns200() {
        val responseBody = """{
            "type": "CATEGORY",
            "name": "Товары",
            "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            "price": 58599,
            "parentId": null,
            "date": "2022-02-03T21:30:00.000Z",
            "children": [
                {
                    "type": "CATEGORY",
                    "name": "Телевизоры",
                    "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                    "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                    "price": 50999,
                    "date": "2022-02-03T21:30:00.000Z",
                    "children": [
                        {
                            "type": "OFFER",
                            "name": "Samson 70\" LED UHD Smart",
                            "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
                            "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                            "price": 32999,
                            "date": "2022-02-03T12:00:00.000Z",
                            "children": null
                        },
                        {
                            "type": "OFFER",
                            "name": "Phyllis 50\" LED UHD Smarter",
                            "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                            "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                            "price": 49999,
                            "date": "2022-02-03T12:00:00.000Z",
                            "children": null
                        },
                        {
                            "type": "OFFER",
                            "name": "Goldstar 65\" LED UHD LOL Very Smart",
                            "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                            "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                            "price": 69999,
                            "date": "2022-02-03T21:30:00.000Z",
                            "children": null
                        }
                    ]
                },
                {
                    "type": "CATEGORY",
                    "name": "Смартфоны",
                    "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                    "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                    "price": 69999,
                    "date": "2022-02-02T12:00:00.000Z",
                    "children": [
                        {
                            "type": "OFFER",
                            "name": "jPhone 13",
                            "id": "863e1a7a-1304-42ae-943b-179184c077e3",
                            "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                            "price": 79999,
                            "date": "2022-02-02T12:00:00.000Z",
                            "children": null
                        },
                        {
                            "type": "OFFER",
                            "name": "Xomiа Readme 10",
                            "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                            "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                            "price": 59999,
                            "date": "2022-02-02T12:00:00.000Z",
                            "children": null
                        }
                    ]
                }
            ]
        }""".trimIndent()

        log.info(">>> preparing data: creating entities <<<")
        for (dto in importDtoList) {
            sendRequest(HttpMethod.POST, "/nodes", HttpStatus.OK, objectMapper.writeValueAsString(dto))
        }
        log.info(">>> checking: found entity <<<")
        sendRequest(HttpMethod.GET, "/nodes/${ids[0]}", HttpStatus.OK)
            .andExpect(content().json(responseBody))
    }

    @Test
    fun getReturns400() {
        sendRequest(HttpMethod.GET, "/nodes/invalid-uuid", HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getReturns404() {
        sendRequest(HttpMethod.GET, "/nodes/${ids[0]}", HttpStatus.NOT_FOUND)
    }


    @Test
    fun deleteReturns200() {
        log.info(">>> preparing data: creating entities <<<")
        for (dto in importDtoList) {
            sendRequest(HttpMethod.POST, "/nodes", HttpStatus.OK, objectMapper.writeValueAsString(dto))
        }

        log.info(">>> main part: deleting of entity <<<")
        sendRequest(HttpMethod.DELETE, "/nodes/${ids[0]}", HttpStatus.OK)

        log.info(">>> checking: missing entity <<<")
        sendRequest(HttpMethod.GET, "/nodes/${ids[0]}", HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteReturns400() {
        sendRequest(HttpMethod.DELETE, "/nodes/invalid-uuid", HttpStatus.BAD_REQUEST)
    }

    @Test
    fun deleteReturns404() {
        sendRequest(HttpMethod.DELETE, "/nodes/${ids[0]}", HttpStatus.NOT_FOUND)
    }


    private class ImportShopUnitDtoListBuilder(updateDate: String) {
        val items = mutableListOf<ImportShopUnitDtoList.Item>()
        val updateDate: Instant = Instant.parse(updateDate)

        fun addItem(
            name: String, type: ShopUnit.Type, price: Int?, id: String, parentId: String? = null
        ): ImportShopUnitDtoListBuilder {
            val parentUuid = parentId?.let { UUID.fromString(it) }
            items.add(ImportShopUnitDtoList.Item(UUID.fromString(id), name, type, price, parentUuid))
            return this
        }

        fun build() = ImportShopUnitDtoList(items, updateDate)
    }

    private fun sendRequest(
        method: HttpMethod, url: String, expectedStatus: HttpStatus? = null, requestBody: String? = null
    ): ResultActions {
        var req = request(method, url)
            .contentType(MediaType.APPLICATION_JSON)
        if (requestBody !== null) {
            req = req.content(requestBody)
        }

        var result = mockMvc.perform(req)
        if (expectedStatus !== null) {
            result = result.andExpect(status().`is`(expectedStatus.value()))
            if (expectedStatus.isError) {
                result = result.andExpect(jsonPath("$.code").value(expectedStatus.value()))
            }
        }
        return result
    }
}
