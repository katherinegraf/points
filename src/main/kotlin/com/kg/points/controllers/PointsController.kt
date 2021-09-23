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
            if (it.payer !in pointsBalance.keys) {
                pointsBalance[it.payer] = it.points
            } else {
                pointsBalance[it.payer] = it.points + pointsBalance.getValue(it.payer)
            }
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
        return ResponseEntity(transaction, HttpStatus.CREATED)
    }

    @PatchMapping("/spendPoints")
    fun spendPoints(
        @RequestBody pointRequest: Map<String, Int>
    ): ResponseEntity<MutableList<SpentPoints>> {
        var pointsToSpend: Int = pointRequest.getValue("points")
        val availablePoints = pointsRepo.findAllByOrderByTimestamp()
        val spentPoints = mutableListOf<SpentPoints>()
        availablePoints.forEach {
            if (pointsToSpend > 0 && it.points > pointsToSpend) {
                spentPoints.add(
                    SpentPoints(
                    payer = it.payer,
                    points = pointsToSpend * -1
                ))
                it.points -= pointsToSpend
                pointsRepo.save(it)
                pointsToSpend = 0
            } else if (pointsToSpend > 0){
                spentPoints.add(
                    SpentPoints(
                        payer = it.payer,
                        points = it.points * -1
                    ))
                pointsToSpend -= it.points
                it.points = 0
                pointsRepo.save(it)
            }
        }
        return ResponseEntity(spentPoints, HttpStatus.OK)
    }

}
