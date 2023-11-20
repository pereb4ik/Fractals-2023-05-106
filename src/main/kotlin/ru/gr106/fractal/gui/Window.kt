package ru.gr106.fractal.gui

import ru.smak.drawing.Converter
import ru.smak.drawing.Plane
import math.Mandelbrot
import ru.gr106.fractal.main
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.MenuBar
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.GroupLayout
import javax.swing.GroupLayout.PREFERRED_SIZE
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.event.MenuListener
import kotlin.math.*

class Window : JFrame() {

    private val mainPanel: DrawingPanel
    private val fp: FractalPainter

    init{
        fp = FractalPainter(Mandelbrot)
        val menuBar = createMenuBar()
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 550)
        mainPanel = DrawingPanel(fp)



        mainPanel.addComponentListener(object : ComponentAdapter(){
            override fun componentResized(e: ComponentEvent?) {
                fp.plane?.width = mainPanel.width
                fp.plane?.height = mainPanel.height
                mainPanel.repaint()
            }
        })
        mainPanel.addSelectedListener {rect ->
            fp.plane?.let {
                val xMin = Converter.xScr2Crt(rect.x, it)
                val yMax = Converter.yScr2Crt(rect.y, it)
                val xMax = Converter.xScr2Crt(rect.x + rect.width, it)
                val yMin = Converter.yScr2Crt(rect.y + rect.height, it)
                it.xMin = xMin
                it.yMin = yMin
                it.xMax = xMax
                it.yMax = yMax
                mainPanel.repaint()
            }
        }
        mainPanel.background = Color.WHITE
        layout = GroupLayout(contentPane).apply {
            setVerticalGroup(
                createSequentialGroup()
                    .addComponent(menuBar, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
                    .addGap(4)
                    .addComponent(mainPanel)
                    .addGap(8)

            )
            setHorizontalGroup(
                createParallelGroup()
                    .addComponent(menuBar)
                    .addGroup(
                        createSequentialGroup()
                            .addGap(8)
                            .addComponent(mainPanel)
                            .addGap(8)
                    )
                    .addGap(4)
            )
        }
        pack()
        fp.plane = Plane(-2.0, 1.0, -1.0, 1.0, mainPanel.width, mainPanel.height)
        fp.pointColor = {
            if (it == 1f) Color.BLACK else
            Color(
                0.5f*(1-cos(16f*it*it)).absoluteValue,
                sin(5f*it).absoluteValue,
                log10(1f + 5*it).absoluteValue
            )
        }
    }




    private fun createMenuBar(): JMenuBar {
        val menuBar = JMenuBar()
        this.add(menuBar)
        val file = JMenu("Файл")
        file.setMnemonic('Ф')
        menuBar.add(file)

        val saveJPG = JMenuItem("Сохранить картинку")
        file.add(saveJPG)
        saveJPG.addActionListener { _: ActionEvent -> saveJPGFunc() }

        val save = JMenuItem("Сохранить проект")
        file.add(save)
        save.addActionListener { _: ActionEvent -> saveFunc() }

        val edit = JMenu("Изменить")
        edit.setMnemonic('И')
        menuBar.add(edit)

        val undo = JMenuItem("Назад")
        edit.add(undo)
        undo.addActionListener { _: ActionEvent -> undoFunc() }

        val redo = JMenuItem("Вперёд")
        edit.add(redo)
        redo.addActionListener { _: ActionEvent -> redoFunc() }

        val theme = JMenuItem("Тема")
        edit.add(theme)
        theme.setMnemonic('Т')
        theme.addActionListener { _: ActionEvent -> themeFunc()}

        val observe = JMenu("Обозреть")
        observe.setMnemonic('О')
        menuBar.add(observe)
        observe.addActionListener { _: ActionEvent -> joulbertFunc() }

        val joulbert = JMenuItem("Отрисовать множество Жюльберта")
        joulbert.setMnemonic('Ж')
        joulbert.addActionListener { _: ActionEvent -> joulbertFunc()}
        observe.add(joulbert)

        val view = JMenuItem("Экскурсия")
        view.setMnemonic('Э')
        view.addActionListener { _: ActionEvent -> viewFunc()}
        observe.add(view)

        /*
        val joulbert = JMenuItem("Жюльберт")

        joulbert.setMnemonic('Ж')
        menuBar.add(joulbert)
        joulbert.addActionListener { _: ActionEvent -> joulbertFunc() }
        */
        /*
        val joulbertBtn = JButton("Отрисовать множество Жюльберта")
        joulbertBtn.addActionListener { joulbertFunc() }
        this.add(joulbertBtn)

        val viewBtn = JButton("Экскурсия по фракталу")
        viewBtn.addActionListener { viewFunc() }
        viewBtn.alignmentX = RIGHT_ALIGNMENT
        //viewBtn.alignmentY = RIGHT_ALIGNMENT
        this.add(viewBtn)
        * */

        return menuBar
    }



    private fun joulbertFunc() {

    }

    private fun themeFunc() {

    }

    private fun redoFunc() {

    }
    private fun saveJPGFunc(){

    }
    private fun saveFunc(){

    }

    private fun viewFunc() {

    }

    private fun undoFunc() {

    }

}