package drawing

data class Plane (
    var xMin: Double,
    var xMax: Double,
    var yMin: Double,
    var yMax: Double,
    var width: Int,
    var height: Int
){
    val xSize: Double
        get() = xMax - xMin
    val ySize: Double
        get() = yMax - yMin
    val xDen: Double
        get() = width/(xMax-xMin)

    val yDen: Double
        get() = height/(yMax-yMin)
}