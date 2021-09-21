package com.kg.points.repos

import com.kg.points.models.Points
import org.springframework.data.jpa.repository.JpaRepository

interface PointsRepo: JpaRepository<Points, Long> {

}
