package ru.gr106.fractal.gui

import drawing.Plane
import math.Mandelbrot
import org.jcodec.api.awt.AWTSequenceEncoder
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Rational
import java.awt.image.BufferedImage
import kotlin.math.exp
import kotlin.math.ln


object MovieMaker {
    /*fun toStr(v: Int): String {
        var ans = v.toString()
        if (v < 10) {
            ans = "0" + ans
        }
        if (v < 100) {
            ans = "0" + ans
        }
        if (v < 1000) {
            ans = "0" + ans
        }
        return ans
    }*/
    var fpp = FractalPainter(Mandelbrot)
    val controlPoints = mutableListOf<Plane>()
    // Content Pane JList
    val cpJList = ListModelPlane(controlPoints)

    fun addControlPoint(p: Plane) {
        controlPoints.add(p)
        cpJList.addItem(p.toString())
    }

    /*fun makeVideo(p1: Plane, p2: Plane, fpp: FractalPainter) {

        val fp = fpp.copy()
        //fp.plane = p2

        /// time in seconds
        val T = 10.0
        val fps = 24
        val frames = (T * fps).toInt()
        val x1 = p1.xSize
        val x2 = p2.xSize
        val y1 = p1.ySize
        val y2 = p2.ySize
        val c1X = (ln(x2) - ln(x1)) / T
        val t0X = ln(x1) / c1X

        val c1Y = (ln(y2) - ln(y1)) / T
        val t0Y = ln(y1) / c1Y

        val out = NIOUtils.writableFileChannel("output.mp4")
        // https://github.com/jcodec/jcodec
        val encoder = AWTSequenceEncoder(out, Rational.R(24, 1))

        for (f in 0..frames) {
            val t = f / (fps.toDouble())
            //val xx = p1.xMin + (p2.xMin - p1.xMin) * (t / T)
            val dx = exp(c1X * (t0X + t))
            val tx = ((dx - x1) / (x2 - x1)) * T
            val xx = p1.xMin + (p2.xMin - p1.xMin) * (tx / T)

            //val yy = p1.yMin + (p2.yMin - p1.yMin) * (t / T)
            val dy = exp(c1Y * (t0Y + t))
            // maybe here remove T?????????????
            val ty = ((dy - y1) / (y2 - y1)) * T
            // AND here remove T !!!!!
            val yy = p1.yMin + (p2.yMin - p1.yMin) * (ty / T)


            val p = Plane(xx, xx + dx, yy, yy + dy, p1.width * 2, p1.height * 2)
            fp.plane = p
            val buff = BufferedImage(p1.width * 2, p1.height * 2, BufferedImage.TYPE_INT_RGB)
            fp.paint(buff.graphics)
            //val file = File("video/f${f}.png")
            //val file = File("video/f" + toStr(f) + ".png")
            println("encode frame:F${f}")
            //ImageIO.write(buff, "png", file)
            encoder.encodeImage(buff)
        }
        encoder.finish()
        NIOUtils.closeQuietly(out)
    }*/

    fun printKeyFrames() {
        for (p in controlPoints) {
            println(p.toString())
        }
    }

    fun makeVideo() {
        printKeyFrames()
        val p1 = controlPoints.first()
        val p2 = controlPoints.last()


        val fp = fpp.copy()
        //fp.plane = p2

        /// time in seconds
        val T = 5.0
        val fps = 24
        val frames = (T * fps).toInt()
        val x1 = p1.xSize
        val x2 = p2.xSize
        val y1 = p1.ySize
        val y2 = p2.ySize
        val c1X = (ln(x2) - ln(x1)) / T
        val t0X = ln(x1) / c1X

        val c1Y = (ln(y2) - ln(y1)) / T
        val t0Y = ln(y1) / c1Y

        val out = NIOUtils.writableFileChannel("output.mp4")
        // view example on https://github.com/jcodec/jcodec
        val encoder = AWTSequenceEncoder(out, Rational.R(24, 1))

        val tkX = mutableListOf<Double>()
        val tkY = mutableListOf<Double>()
        tkX.add(0.0)
        tkY.add(0.0)
        for (p in controlPoints) {
            if (p != p1 && p != p2) {
                val yk = p.ySize
                val tky = ln(yk) / c1Y - t0Y
                val xk = p.xSize
                val tkx = ln(xk) / c1X - t0X
                tkX.add(tkx)
                tkY.add(tky)
            }
        }
        tkX.add(T)
        tkY.add(T)

        val cp = controlPoints
        var xk = 0
        var yk = 0

        for (f in 0..frames) {
            val t = f / (fps.toDouble())
            while (tkX[xk + 1] < t) {
                xk++
            }
            while (tkY[yk + 1] < t) {
                yk++
            }

            val dx = exp(c1X * (t0X + t))
            val tx = (dx - cp[xk].xSize) / (cp[xk + 1].xSize - cp[xk].xSize)
            val xx = cp[xk].xMin + (cp[xk + 1].xMin - cp[xk].xMin) * tx


            val dy = exp(c1Y * (t0Y + t))
            val ty = (dy - cp[yk].ySize) / (cp[yk + 1].ySize - cp[yk].ySize)
            val yy = cp[yk].yMin + (cp[yk + 1].yMin - cp[yk].yMin) * ty


            val p = Plane(xx, xx + dx, yy, yy + dy, p1.width * 2, p1.height * 2)
            fp.plane = p
            val buff = BufferedImage(p1.width * 2, p1.height * 2, BufferedImage.TYPE_INT_RGB)
            fp.paint(buff.graphics)
            //val file = File("video/f${f}.png")
            //val file = File("video/f" + toStr(f) + ".png")
            println("encode frame:F${f}")
            //ImageIO.write(buff, "png", file)
            encoder.encodeImage(buff)
        }
        encoder.finish()
        NIOUtils.closeQuietly(out)
    }
}