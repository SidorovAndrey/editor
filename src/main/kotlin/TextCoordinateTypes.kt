data class TextCoordinate(var row: Int, var column: Int)
data class SelectCoordinates(var start: TextCoordinate, var end: TextCoordinate)

fun textCoordinateDefault(): TextCoordinate {
    return TextCoordinate(0, 0)
}

fun textCoordinateNegative(): TextCoordinate {
    return TextCoordinate(-1, -1)
}

fun textCoordinateCopy(coordinate: TextCoordinate): TextCoordinate {
    return TextCoordinate(coordinate.row, coordinate.column)
}
