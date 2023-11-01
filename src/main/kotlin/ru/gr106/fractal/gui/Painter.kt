package ru.gr106.fractal.gui

import java.awt.Graphics

interface Painter {
    val width: Int
    val height: Int
    fun paint(g: Graphics)
}
