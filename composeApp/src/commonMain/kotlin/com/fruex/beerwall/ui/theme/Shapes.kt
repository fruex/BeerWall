package com.fruex.beerwall.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardShape = RoundedCornerShape(16.dp)
val IconBoxShape = RoundedCornerShape(12.dp)
val ButtonShape = RoundedCornerShape(12.dp)

val BeerWallShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = IconBoxShape,
    large = CardShape
)
