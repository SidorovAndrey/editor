package tokenizer

fun readString(str: String, idx: Int): Pair<String, Int> {
    if (idx >= str.length) return Pair("", idx)

    if (str[idx] == '\"') {
        val stringEnds = str.drop(idx + 1).indexOf('\"')
        if (stringEnds == -1)
            return Pair(str.substring(idx, str.length), str.length)

        return Pair(str.substring(idx, idx + stringEnds + 2), idx + stringEnds + 2)
    }

    if (str[idx] == '\'') {
        val stringEnds = str.drop(idx + 1).indexOf('\'')
        if (stringEnds == -1)
            return Pair(str.substring(idx, str.length), str.length)

        return Pair(str.substring(idx, stringEnds + 2), stringEnds + 2)
    }

    return Pair("", idx)
}

fun readWord(str: String, idx: Int): Pair<String, Int> {
    var i = idx
    if (i >= str.length || str[i].isDigit()) return Pair("", i)

    val res = StringBuilder()
    while (i < str.length && str[i].isLetterOrDigit()) {
        res.append(str[i])
        i++
    }

    return Pair(res.toString(), i)
}

fun readComment(str: String, idx: Int): Pair<String, Int> {
    var i = idx
    if (i >= str.length || str[i] != '/') return Pair("", i)

    i++
    if (i >= str.length || (str[i] != '/' && str[i] != '*')) return Pair("", i)
    if (str[i] == '/')
        return Pair(str.substring(i - 1), str.length)

    val commentEnd = str.lastIndexOf("*/")
    if (commentEnd == -1)
        return Pair(str.substring(i - 1), str.length)

    return Pair(str.substring(i - 1, commentEnd + 2), commentEnd + 2)
}

fun readToCommentEnd(str: String, idx: Int): Pair<String, Int> {
    var i = idx
    while (i < str.length && !str.startsWith("*/", i))
        i++

    val lastIdx = minOf(i + 2, str.length)
    return Pair(str.substring(0, lastIdx), lastIdx)
}

fun readNothing(str: String, idx: Int): Pair<String, Int> {
    val isNothing = { i: Int ->
        !str.drop(i)[0].isLetter() &&
        !str.drop(i).startsWith("/*") &&
        !str.drop(i).startsWith("//") &&
        !str.drop(i).startsWith("\"") &&
        !str.drop(i).startsWith("\'")
    }

    var i = idx
    while (i < str.length && isNothing(i)) {
        i++
    }

    return Pair(str.substring(idx, i), i)
}
