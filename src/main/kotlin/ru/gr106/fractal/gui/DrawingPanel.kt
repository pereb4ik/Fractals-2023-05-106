package ru.gr106.fractal.gui

import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

class DrawingPanel(val p:FractalPainter) : JPanel() {
    private var rect = SelectionRect()
    private val selectedListener = mutableListOf<(SelectionRect)->Unit>()

    private var startX = 0
    private var startY = 0


    fun addSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.add(l)
    }

    fun removeSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.remove(l)
    }

    init {

        this.addMouseListener(object : MouseAdapter(){
            override fun mousePressed(e: MouseEvent?) {
                if (e?.button == MouseEvent.BUTTON3) {
                    if (e != null) {
                        startX = e.x
                    }
                    if (e != null) {
                        startY = e.y
                    }
                }
                if (e?.button == MouseEvent.BUTTON1) {
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
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (e?.button == MouseEvent.BUTTON3) {
                    if (e != null) {
                        startX = e.x
                    }
                    if (e != null) {
                        startY = e.y
                    }
                }
                if (e?.button == MouseEvent.BUTTON1) {
                    e?.let {
                    if (rect.isCreated) drawRect()
                    rect.addPoint(it.x, it.y)
                    selectedListener.forEach { it(rect) }
                    }
                }
            }

        })
        this.addMouseMotionListener(object : MouseMotionAdapter(){

            override fun mouseDragged(e: MouseEvent?) {
                if (e?.button == MouseEvent.BUTTON3) {
                    var d = e?.x?.minus(startX)
                    p.plane?.xMin = p.plane?.xMin?.minus(d!!/ p.plane?.xDen!!)!!
                    p.plane?.xMax = p.plane?.xMax?.minus(d!!/p.plane?.xDen!!)!!

                    var d2 = e?.y?.minus(startY)
                    p.plane?.yMin = p.plane?.yMin?.minus(-d2!!/p.plane?.yDen!!)!!
                    p.plane?.yMax = p.plane?.yMax?.minus(-d2!!/p.plane?.yDen!!)!!
                    if (e != null) {
                        startX = e.x
                    }
                    if (e != null) {
                        startY = e.y
                    }
                    p.dx = d!!
                    p.dy = d2!!

                    p.paint(this@DrawingPanel.graphics)

                }
                if (e?.button == MouseEvent.BUTTON1) {
                    e?.let {
                        if (rect.isCreated)
                            drawRect()
                        rect.addPoint(it.x, it.y)
                        drawRect()
                    }
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