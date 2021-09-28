package com.kg.points.controllers

import com.kg.points.models.Points
import com.kg.points.models.SpentPoints
import com.kg.points.repos.PointsRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
class PointsController {

    val logger: Logger = Logger.getLogger("logger")

    @Autowired
    private lateinit var pointsRepo: PointsRepo

    @GetMapping("/pointsBalance")
    fun getPointsBalances(): ResponseEntity<MutableMap<String, Int>> {
        val pointsBalance = mutableMapOf<String, Int>()
        val activePoints = pointsRepo.findAll()
        activePoints.forEach{
            pointsBalance[it.payer] = it.points + pointsBalance.getOrDefault(it.payer, 0)
        }
        return ResponseEntity(pointsBalance, HttpStatus.OK)
    }

    @PostMapping("/addTransaction")
    fun addTransaction(
        @RequestBody transaction: Points
    ): ResponseEntity<Points> {
        val newTransaction = Points(
            payer = transaction.payer,
            points = transaction.points,
            timestamp = transaction.timestamp
        )
        pointsRepo.save(newTransaction)
        return ResponseEntity(newTransaction, HttpStatus.CREATED)
    }

    @PatchMapping("/spendPoints")
    fun spendPoints(
        @RequestBody request: Map<String, Int>
    ): ResponseEntity<List<SpentPoints>> {
        val pointsToSpend: Int = request.getValue("points")
        return if (!canRequestBeFilled(pointsToSpend)) {
            ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        } else {
            val pointsDeducted = fulfillSpendRequest(pointsToSpend)
            val spentPointsSummary = convertMapToListOfSpentPoints(pointsDeducted)
            ResponseEntity(spentPointsSummary, HttpStatus.OK)
        }
    }

    fun canRequestBeFilled(request: Int): Boolean {
        return (request < pointsRepo.fetchTotalPoints())
    }

    fun fulfillSpendRequest(request: Int): Map<String, Int> {
        var pointsToSpend = request
        val availablePoints = pointsRepo.findAllByOrderByTimestamp()
        val pointsDeducted = mutableMapOf<String, Int>()
        availablePoints.forEach {
            if (pointsToSpend > 0 && it.points > pointsToSpend) {
                pointsDeducted[it.payer] =
                    pointsToSpend * -1 + pointsDeducted.getOrDefault(it.payer, 0)
                it.points -= pointsToSpend
                pointsRepo.save(it)
                pointsToSpend = 0
            } else if (pointsToSpend > 0){
                pointsDeducted[it.payer] =
                    it.points * -1 + pointsDeducted.getOrDefault(it.payer, 0)
                pointsToSpend -= it.points
                it.points = 0
                pointsRepo.save(it)
            }
        }
        return pointsDeducted
    }

    fun convertMapToListOfSpentPoints(pointsDeducted: Map<String,Int>): List<SpentPoints> {
        val listToReturn = mutableListOf<SpentPoints>()
        pointsDeducted.forEach {
            listToReturn.add(
                SpentPoints(
                    payer = it.key,
                    points = it.value
                )
            )
        }
        return listToReturn
    }

}
