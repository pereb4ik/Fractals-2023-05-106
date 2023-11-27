package ru.gr106.fractal.gui

import drawing.Plane
import math.Mandelbrot
import math.splines.AnotherCubicSpline
import math.splines.CubicMomentSpline
import math.splines.Exponent
import math.splines.LinearSpline
import math.splines.Spline
import org.jcodec.api.awt.AWTSequenceEncoder
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Rational
import java.awt.image.BufferedImage
import kotlin.math.exp
import kotlin.math.ln


object MovieMaker {
    var fpp = FractalPainter(Mandelbrot)
    val controlPoints = mutableListOf<Plane>()

    /// time in seconds
    val T = 5.0
    val fps = 24
    val outputFileName = "output.mp4"

    // Content Pane JList
    val cpJList = ListModelPlane(controlPoints)

    fun addControlPoint(p: Plane) {
        controlPoints.add(p)
        cpJList.addItem(p.toString())
    }

    fun printKeyFrames() {
        for (p in controlPoints) {
            println(p.toString())
        }
    }

    fun printList(list: List<Double>) {
        for (d in list) {
            print(d.toString() + " ")
        }
        println()
    }

    fun setUpSystem(sizes: Array<Double>): ArrayList<Double> {
        val N = sizes.size
        val a: Array<Double> = Array(N) { 0.0 }
        a[0] = T
        for (i in 1..<N) {
            val s2 = sizes[i]
            val s1 = sizes[i - 1]
            a[i] = ln(s2) - ln(s1)
            if (i % 2 == 0) {
                a[i] = -a[i]
            }
        }
        return solveSystem(a)
    }

    fun solveSystem(a: Array<Double>): ArrayList<Double> {
        var sum = 0.0
        val n = a.size
        for (i in 1..<n) {
            sum += a[i]
        }
        //val x: ArrayList<Double> = ArrayList(n)
        val x: Array<Double> = Array(n) { 0.0 }
        x[0] = sum / a[0]
        for (i in 1..<n) {
            x[i] = a[i] / x[0]
        }
        return ArrayList(x.toList())
    }

    fun makeVideo() {
        printKeyFrames()

        val width = controlPoints[0].width * 2
        val height = controlPoints[0].height * 2

        val fp = fpp.copy()

        val frames = (T * fps).toInt()
        val frameInd = mutableListOf<Int>()
        val n = controlPoints.size
        frameInd.add(0)
        for (i in 1..<n - 1) {
            val dS1 = controlPoints[i].S - controlPoints[i - 1].S
            val dS2 = controlPoints[i + 1].S - controlPoints[i].S
            if (dS1 * dS2 < 0) {
                frameInd.add(i)
            }
        }
        frameInd.add(n - 1)
        val xsizes = mutableListOf<Double>()
        val ysizes = mutableListOf<Double>()
        for (i in frameInd) {
            xsizes.add(controlPoints[i].xSize)
            ysizes.add(controlPoints[i].ySize)
        }
        val tx = setUpSystem(xsizes.toTypedArray())
        val ty = setUpSystem(ysizes.toTypedArray())
        val c1X = tx[0]
        val t0X = ln(xsizes[0]) / c1X
        tx.removeAt(0)

        val c1Y = ty[0]
        val t0Y = ln(ysizes[0]) / c1Y
        ty.removeAt(0)

        // Segments of increasing and decreasing
        // time of frames
        val segmentTx = mutableListOf<Double>()
        val segmentTy = mutableListOf<Double>()
        segmentTx.add(0.0)
        segmentTy.add(0.0)
        var sum = 0.0
        for (dt in tx) {
            sum += dt
            segmentTx.add(sum)
        }
        sum = 0.0
        for (dt in ty) {
            sum += dt
            segmentTy.add(sum)
        }
        val xE = mutableListOf<Exponent>()
        val yE = mutableListOf<Exponent>()
        for (i in 0..<xsizes.size - 1) {
            if (i % 2 == 0) {
                xE.add(Exponent(c1X, t0X, segmentTx[i], xsizes[i]))
            } else {
                xE.add(Exponent(-c1X, t0X, segmentTx[i], xsizes[i]))
            }
        }
        for (i in 0..<ysizes.size - 1) {
            if (i % 2 == 0) {
                yE.add(Exponent(c1Y, t0Y, segmentTy[i], ysizes[i]))
            } else {
                yE.add(Exponent(-c1Y, t0Y, segmentTy[i], ysizes[i]))
            }
        }
        val Sx = mutableListOf<Spline>()
        val Sy = mutableListOf<Spline>()
        for (i in 0..<frameInd.size - 1) {
            val i1 = frameInd[i]
            val i2 = frameInd[i + 1]
            val xSize = mutableListOf<Double>()
            val ySize = mutableListOf<Double>()
            val xMin = mutableListOf<Double>()
            val yMin = mutableListOf<Double>()
            for (j in i1..i2) {
                xSize.add(controlPoints[j].xSize)
                ySize.add(controlPoints[j].ySize)
                xMin.add(controlPoints[j].xMin)
                yMin.add(controlPoints[j].yMin)
            }
            if (xSize.first() > xSize.last()) {
                xSize.reverse()
                xMin.reverse()
            }
            if (ySize.first() > ySize.last()) {
                ySize.reverse()
                yMin.reverse()
            }
            val N = xSize.size - 1
            Sx.add(CubicMomentSpline(N, xSize.toDoubleArray(), xMin.toDoubleArray()))
            Sy.add(CubicMomentSpline(N, ySize.toDoubleArray(), yMin.toDoubleArray()))
        }
        /// Awful bag fix
        // sometimes last time point < then T
        // this fix that
        segmentTx[segmentTx.size - 1] = T
        segmentTy[segmentTy.size - 1] = T

        var xk = 0
        var yk = 0

        val out = NIOUtils.writableFileChannel(outputFileName)
        val encoder = AWTSequenceEncoder(out, Rational.R(fps, 1))
        for (f in 0..frames) {
            val t = f / (fps.toDouble())
            // find segment where placed t
            while (segmentTx[xk + 1] < t) {
                xk++
            }
            while (segmentTy[yk + 1] < t) {
                yk++
            }

            val dx = xE[xk].sb(t)
            val xx = Sx[xk].sb(dx)

            ///// -----------------------
            val dy = yE[yk].sb(t)
            val yy = Sy[yk].sb(dy)

            val p = Plane(xx, xx + dx, yy, yy + dy, width, height)
            fp.plane = p
            val buff = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            fp.paint(buff.graphics)
            println("encode frame:F${f}")
            encoder.encodeImage(buff)
        }

        encoder.finish()
        NIOUtils.closeQuietly(out)
    }
}