package ru.gr106.fractal

import math.Complex
import ru.gr106.fractal.gui.Window

fun main() {
    println(Complex(1.0, 3.0) * Complex(2.0, 2.0) + Complex(1.0, 2.0))
    val z = Complex(1.0, 3.0)
    z *= Complex(2.0, 2.0)
    z += Complex(1.0, 2.0)
    println(z)
    Window().apply { isVisible = true }
}