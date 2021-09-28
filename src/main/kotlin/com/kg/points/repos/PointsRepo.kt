package com.kg.points.repos

import com.kg.points.models.Points
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PointsRepo: JpaRepository<Points, Long> {

    fun findAllByOrderByTimestamp(): List<Points>

    @Query("SELECT SUM(a.points) FROM Points a")
    fun fetchTotalPoints(): Int
}
