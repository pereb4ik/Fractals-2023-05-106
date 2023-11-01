package ru.gr106.fractal.gui

import math.AlgebraicFractal
import math.Complex
import java.awt.Graphics
import drawing.Converter
import drawing.Plane
import math.Mandelbrot
import java.awt.Color

class FractalPainter (val fractal: AlgebraicFractal) : Painter{

    var plane: Plane? = null
    override val width: Int
        get() = plane?.width?:0
    override val height: Int
        get() = plane?.height?:0
    var pointColor: (Float) -> Color = {if (it < 1f) Color.WHITE else Color.BLACK }


    override fun paint(g: Graphics) {
        //как рисовать фрактал
        plane?.let{plane ->
            for(x in 0..width){
                for (y in 0..height){
                    val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                    g.color = pointColor(Mandelbrot.isInSet(z))
                    g.fillRect(x, y, 1, 1)
                }
            }
        }

    }

}
