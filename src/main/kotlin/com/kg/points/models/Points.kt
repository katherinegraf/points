package com.kg.points.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "points")
class Points (

    @Column(name = "payer")
    val payer: String,

    @Column(name = "points")
    var points: Int,

    @Column(name = "timestamp")
    val timestamp: LocalDateTime
)
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0;
}