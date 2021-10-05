package com.kg.points.controllers

import com.kg.points.mocks.*
import com.kg.points.models.Points
import com.kg.points.repos.PointsRepo
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.logging.Logger

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class PointsControllerTests {

    val logger: Logger = Logger.getLogger("logger")

    @Autowired
    private lateinit var pointsController: PointsController

    @Autowired
    private lateinit var pointsRepo: PointsRepo

    @Test
    @Order(1)
    fun addTransaction_Test() {
        val result = pointsController.addTransaction(transaction1)
        assert(result.statusCode == HttpStatus.CREATED)
        val resultBody = result.body as Points
        val foundRecord = pointsRepo.findById(resultBody.id)
        assert(foundRecord.isPresent)
        assert(foundRecord.get().payer == resultBody.payer)
        assert(foundRecord.get().points == resultBody.points)
        assert(foundRecord.get().timestamp == resultBody.timestamp)
        assert(foundRecord.get().id == resultBody.id)
    }

    @Test
    @Order(2)
    fun getPointsBalances_Test() {
        // add additional data to test repo
        val transactions = listOf(transaction2, transaction3, transaction4, transaction5)
        transactions.forEach {
            pointsController.addTransaction(it)
        }
        val result = pointsController.getPointsBalances()
        assert(result.statusCode == HttpStatus.OK)
        val resultBody = result.body
        assert(resultBody?.get(transaction5.payer) == 1450)
        assert(resultBody?.get(transaction4.payer) == 50)
        assert(resultBody?.get(transaction2.payer) == 100)
    }

    @Test
    @Order(3)
    fun canRequestBeFilled_Test() {
        val expectTrue = pointsController.canRequestBeFilled(50)
        assert(expectTrue)
        val expectFalse = pointsController.canRequestBeFilled(50000)
        assert(!expectFalse)
    }

    @Test
    @Order(4)
    fun fulfillSpendRequest_Test() {
        val result = pointsController.fulfillSpendRequest(1000)
        assert(result["Frito-Lay"] == -850)
        assert(result["Charmin"] == -100)
        assert(result["Tillamook"] == -50)
    }

    @Test
    @Order(5)
    fun convertMapToListOfSpentPoints_Test() {
        val map = mapOf<String, Int>(
            "Frito-Lay" to 850,
            "Charmin" to 100,
            "Tillamook" to 50
        )
        val result = pointsController.convertMapToListOfSpentPoints(map)
        assert(result[0].payer == "Frito-Lay" && result[0].points == 850)
        assert(result[1].payer == "Charmin" && result[1].points == 100)
        assert(result[2].payer == "Tillamook" && result[2].points == 50)
    }

    @Test
    @Order(6)
    fun spendPoints_Test() {
        val expectUnprocessable = pointsController.spendPoints(mapOf("points" to 1000))
        assert(expectUnprocessable.statusCode == HttpStatus.UNPROCESSABLE_ENTITY)
        val expectOk = pointsController.spendPoints(mapOf("points" to 50))
        assert(expectOk.statusCode == HttpStatus.OK)
        val expectOkBody = expectOk.body
        assert(expectOkBody?.get(0)?.payer == "Frito-Lay")
        assert(expectOkBody?.get(0)?.points == -50)
    }

    @AfterAll
    fun clearRepoAfterTesting() {
        pointsRepo.deleteAll()
    }

}
