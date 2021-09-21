package com.kg.points.controllers

import com.kg.points.models.Points
import com.kg.points.repos.PointsRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
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
}