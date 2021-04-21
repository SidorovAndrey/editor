import java.awt.Color
import java.io.FileInputStream
import java.util.*

object Configuration {
    private const val fontSizeKey = "font_size"
    private const val linesGapKey = "lines_gap"

    private const val bgColorKey = "bg_color"
    private const val cursorColorKey = "cursor_color"
    private const val selectionColorKey = "selection_color"
    private const val mainFontColorKey = "main_font_color"
    private const val keywordFontColorKey = "keyword_font_color"
    private const val identifierFontColorKey = "identifier_font_color"
    private const val stringFontColorKey = "string_font_color"
    private const val commentFontColorKey = "comment_font_color"

    var fontSize: Int = 0
        private set

    var linesGap: Int = 0
        private set

    var bgColor: Color = Color(0, 0, 0)
        private set

    var cursorColor: Color = Color(0, 0, 0)
        private set

    var mainFontColor: Color = Color(0, 0, 0)
        private set

    var keywordFontColor: Color = Color(0, 0, 0)
        private set

    var identifierFontColor: Color = Color(0, 0, 0)
        private set

    var stringFontColor: Color = Color(0, 0, 0)
        private set

    var commentFontColor: Color = Color(0, 0, 0)
        private set

    var selectionColor: Color = Color(0, 0, 0)
        private set

    fun load(fileInputStream: FileInputStream) {
        val props = Properties()
        props.load(fileInputStream)

        fontSize = Integer.parseInt(props.getProperty(fontSizeKey))
        linesGap = Integer.parseInt(props.getProperty(linesGapKey))

        bgColor = readColor(bgColorKey, props)
        cursorColor = readColor(cursorColorKey, props)
        selectionColor = readColor(selectionColorKey, props)
        mainFontColor = readColor(mainFontColorKey, props)
        keywordFontColor = readColor(keywordFontColorKey, props)
        identifierFontColor = readColor(identifierFontColorKey, props)
        stringFontColor = readColor(stringFontColorKey, props)
        commentFontColor = readColor(commentFontColorKey, props)
    }

    private fun readColor(key: String, props: Properties): Color {
        val values = props.getProperty(key).split(',')
        return Color(
            Integer.parseInt(values[0]),
            Integer.parseInt(values[1]),
            Integer.parseInt(values[2]),
        )
    }
}
