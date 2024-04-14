package com.levishok.market.controllers

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
//@AutoConfigureMockMvc(printOnlyOnFailure = false)
@AutoConfigureMockMvc
class ShopUnitControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @BeforeEach
    fun clear() {
        transactionTemplate.executeWithoutResult {
            entityManager.createQuery("delete from ShopUnit").executeUpdate()
        }
    }

    @Test
    fun importsReturns200() {
        val requestsBodies = listOf(
            // 1
            """{
                "items": [
                    {
                        "type": "CATEGORY",
                        "name": "Товары",
                        "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                        "parentId": null
                    }
                ],
                "updateDate": "2022-02-01T12:00:00.000Z"
            }""",
            // 2
            """{
                "items": [
                    {
                        "type": "CATEGORY",
                        "name": "Смартфоны",
                        "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                        "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"
                    },
                    {
                        "type": "OFFER",
                        "name": "jPhone 13",
                        "id": "863e1a7a-1304-42ae-943b-179184c077e3",
                        "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                        "price": 79999
                    },
                    {
                        "type": "OFFER",
                        "name": "Xomiа Readme 10",
                        "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                        "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                        "price": 59999
                    }
                ],
                "updateDate": "2022-02-02T12:00:00.000Z"
            }""",
            // 3
            """{
                "items": [
                    {
                        "type": "CATEGORY",
                        "name": "Телевизоры",
                        "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                        "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"
                    },
                    {
                        "type": "OFFER",
                        "name": "Samson 70\" LED UHD Smart",
                        "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
                        "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                        "price": 32999
                    },
                    {
                        "type": "OFFER",
                        "name": "Phyllis 50\" LED UHD Smarter",
                        "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                        "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                        "price": 49999
                    }
                ],
                "updateDate": "2022-02-03T12:00:00.000Z"
            }""",
            // 4
            """{
                "items": [
                    {
                        "type": "OFFER",
                        "name": "Goldstar 65\" LED UHD LOL Very Smart",
                        "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                        "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                        "price": 69999
                    }
                ],
                "updateDate": "2022-02-03T15:00:00.000Z"
            }
            """
        )

        val expected = 200
        for ((i, body) in requestsBodies.withIndex()) {
            println("\n>>>\n>>> test part #${i+1}\n>>>\n")

            val statusCode = post("/imports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .let(mockMvc::perform)
                .andExpect(status().isOk)
                .andReturn()
                .response
                .status

            assertEquals(expected, statusCode) {
                "part #${i+1}: expected code $expected, actual code $statusCode"
            }
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
            "date": "2022-02-03T15:00:00.000Z",
            "children": [
                {
                    "type": "CATEGORY",
                    "name": "Телевизоры",
                    "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                    "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                    "price": 50999,
                    "date": "2022-02-03T15:00:00.000Z",
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
                            "date": "2022-02-03T15:00:00.000Z",
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
        get("/nodes/069cb8d7-bbdd-47d3-ad8f-82ef4c269df1")
            .contentType(MediaType.APPLICATION_JSON)
            .let(mockMvc::perform)
            .andExpect(status().isOk)
            .andExpect(content().json(responseBody))
    }
}
