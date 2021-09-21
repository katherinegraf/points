package com.kg.points.controllers

import com.kg.points.models.Points
import com.kg.points.repos.PointsRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@RestController
class PointsController {

    val logger: Logger = Logger.getLogger("logger")

    @Autowired
    private lateinit var pointsRepo: PointsRepo

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