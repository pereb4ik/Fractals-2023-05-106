package ru.gr106.fractal.gui

import drawing.Plane
import java.awt.BorderLayout
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException
import javax.swing.*

class ListModelPlane(val keyFrames: MutableList<Plane>) : JPanel() {

    var list: JList<String>

    var model: DefaultListModel<String>
    var movedPlane = Plane(0.0, 0.0, 0.0, 0.0, 1, 1)

    init {
        setLayout(BorderLayout())
        model = DefaultListModel<String>()
        list = JList(model)
        list.dragEnabled = true
        list.dropMode = DropMode.INSERT
        // https://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop
        // https://docs.oracle.com/javase/tutorial/uiswing/dnd/dropmodedemo.html
        list.transferHandler = object : TransferHandler() {
            var index = 0
            var beforeIndex =
                false //Start with `false` therefore if it is removed from or added to the list it still works

            @Override
            override fun getSourceActions(comp: JComponent): Int {
                return MOVE
            }

            @Override
            override fun createTransferable(comp: JComponent): Transferable {
                index = list.selectedIndex
                movedPlane = keyFrames[index]
                return StringSelection(list.getSelectedValue())
            }

            @Override
            override fun exportDone(comp: JComponent, trans: Transferable, action: Int) {
                if (action == MOVE) {
                    if (beforeIndex) {
                        model.remove(index + 1)
                        keyFrames.removeAt(index + 1)
                    } else {
                        model.remove(index)
                        keyFrames.removeAt(index)
                    }
                }
            }

            @Override
            override fun canImport(support: TransferSupport): Boolean {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor)
            }

            @Override
            override fun importData(support: TransferSupport): Boolean {
                try {
                    val s = (support.getTransferable().getTransferData(DataFlavor.stringFlavor)) as String
                    val dl: JList.DropLocation = (support.getDropLocation()) as JList.DropLocation
                    model.add(dl.index, s)
                    keyFrames.add(dl.index, movedPlane)
                    beforeIndex = (dl.index < index)
                    return true
                } catch (e: UnsupportedFlavorException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return false
            }
        }
        // http://www.java2s.com/Code/Java/Swing-JFC/AnexampleofJListwithaDefaultListModel.htm
        val pane = JScrollPane(list)
        val addButton = JButton("Add Element")
        val removeButton = JButton("Remove Element")
        val burnButton = JButton("Burn")

        addButton.addActionListener {
            model.addElement("Element ")
        }
        removeButton.addActionListener {
            removeSelectedItem()
        }
        burnButton.addActionListener {
            MovieMaker.makeVideo()
        }
        add(pane, BorderLayout.NORTH)
        //add(addButton, BorderLayout.WEST)
        add(burnButton, BorderLayout.WEST)
        add(removeButton, BorderLayout.EAST)
    }

    fun addItem(s: String) {
        model.addElement(s)
    }

    fun removeSelectedItem() {
        if (model.size() > 0) {
            val i = list.anchorSelectionIndex
            model.removeElementAt(i)
            keyFrames.removeAt(i)
        }
    }

}

class FractalTourMenu : JFrame() {
    init {
        title = "Key frames list"
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane = MovieMaker.cpJList
        setSize(260, 200)
        isVisible = true
    }
}