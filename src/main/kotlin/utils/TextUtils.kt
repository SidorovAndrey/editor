package utils

import TextCoordinate
import java.security.InvalidParameterException

fun getBracketCoordinates(text: MutableList<String>, cursorRow: Int, cursorColumn: Int): Pair<TextCoordinate, TextCoordinate> {
    val row = text[cursorRow]
    if (row.isEmpty() || cursorColumn >= row.length)
        return Pair(TextCoordinate(-1, -1), TextCoordinate(-1, -1))

    val bracket =  row[cursorColumn]
    val bracketTest = isBracket(bracket)

    // go to right
    if (bracketTest == 1) {
        val bracketPair = getBracketPair(bracket)

        var i = cursorColumn
        var rowIdx = cursorRow
        var brackets = 1
        while (brackets > 0) {
            i++
            if (i >= text[rowIdx].length) {
                i = 0
                rowIdx++
                while (text[rowIdx].isEmpty()) rowIdx++
            }

            if (rowIdx == text.size) {
                // not found, return empty pair
                return Pair(TextCoordinate(-1, -1), TextCoordinate(-1, -1))
            } else {
                if (text[rowIdx][i] == bracket)
                    brackets++
                else if (text[rowIdx][i] == bracketPair)
                    brackets--
            }
        }

        return Pair(TextCoordinate(cursorRow, cursorColumn), TextCoordinate(rowIdx, i))
    }

    // go to left
    if (bracketTest == -1) {
        val bracketPair = getBracketPair(bracket)

        var i = cursorColumn
        var rowIdx = cursorRow
        var brackets = 1
        while (brackets > 0) {
            i--
            if (i < 0) {
                rowIdx--
                if (rowIdx < 0) {
                    // not found, return empty pair
                    return Pair(TextCoordinate(-1, -1), TextCoordinate(-1, -1))
                }

                i = text[rowIdx].length
            } else {
                if (text[rowIdx][i] == bracket)
                    brackets++
                else if (text[rowIdx][i] == bracketPair)
                    brackets--
            }
        }

        return Pair(TextCoordinate(cursorRow, cursorColumn), TextCoordinate(rowIdx, i))
    }

    // it is not a bracket
    return Pair(TextCoordinate(-1, -1), TextCoordinate(-1, -1))
}

private val openBrackets = setOf('(', '[', '{', '<')
private val closeBrackets = setOf(')', ']', '}', '>')

private fun isBracket(ch: Char): Int {
    if (openBrackets.contains(ch))
        return 1

    if (closeBrackets.contains(ch))
        return -1

    return 0
}

private fun getBracketPair(ch: Char): Char {
    if (ch == '(')
        return ')'

    if (ch == '[')
        return ']'

    if (ch == '{')
        return '}'

    if (ch == '<')
        return '>'

    if (ch == ')')
        return '('

    if (ch == ']')
        return '['

    if (ch == '}')
        return '{'

    if (ch == '>')
        return '<'

    throw InvalidParameterException("'$ch' is not a bracket")
}
