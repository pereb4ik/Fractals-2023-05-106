package ru.gr106.fractal.gui

import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

class DrawingPanel(val p:Painter) : JPanel() {
    private var rect = SelectionRect()
    private val selectedListener = mutableListOf<(SelectionRect)->Unit>()

    fun addSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.add(l)
    }

    fun removeSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.remove(l)
    }

    init {

        this.addMouseListener(object : MouseAdapter(){
            override fun mousePressed(e: MouseEvent?) {
                e?.let {
                    rect = SelectionRect().apply {
                        addPoint(it.x, it.y)
                        graphics.apply {
                            setXORMode(Color.WHITE)
                            drawRect(-10, -10, 1, 1)
                            setPaintMode()
                        }
                    }
                }

            }

            override fun mouseReleased(e: MouseEvent?) {
                e?.let {
                    if (rect.isCreated) drawRect()
                    rect.addPoint(it.x, it.y)
                    if(rect.notZero()) {
                        selectedListener.forEach { it(rect) }
                    }
                }
            }

        })
        this.addMouseMotionListener(object : MouseMotionAdapter(){
            override fun mouseDragged(e: MouseEvent?) {
                e?.let {
                    if (rect.isCreated)
                        drawRect()
                    rect.addPoint(it.x, it.y)
                    drawRect()
                }
            }

        })
    }

    private fun drawRect() {
        graphics.apply{
            setXORMode(Color.WHITE)
            color = Color.BLACK
            drawRect(rect.x, rect.y, rect.width, rect.height)
            setPaintMode()
        }
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        g?.let{ p.paint(it) }
    }
}