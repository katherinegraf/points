package com.kg.points.mocks

import com.kg.points.models.Points
import java.time.LocalDateTime

val transaction1 = Points(
    payer = "Frito-Lay",
    points = 500,
    timestamp = LocalDateTime.now().minusDays(7)
)

val transaction2 = Points(
    payer = "Charmin",
    points = 100,
    timestamp = LocalDateTime.now().minusDays(3)
)

val transaction3 = Points(
    payer = "Frito-Lay",
    points = 750,
    timestamp = LocalDateTime.now().minusDays(1)
)

val transaction4 = Points(
    payer = "Tillamook",
    points = 50,
    timestamp = LocalDateTime.now().minusDays(2)
)

val transaction5 = Points(
    payer = "Frito-Lay",
    points = 200,
    timestamp = LocalDateTime.now().minusDays(5)
)
