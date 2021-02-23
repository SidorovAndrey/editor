import java.io.FileInputStream
import java.util.*

object Configuration {
    private const val fontSizeKey = "font_size"
    private const val linesGapKey = "lines_gap"

    var fontSize: Int = 0
        private set

    var linesGap: Int = 0
        private set

    fun load(fileInputStream: FileInputStream) {
        val props = Properties()
        props.load(fileInputStream)

        fontSize = Integer.parseInt(props.getProperty(fontSizeKey))
        linesGap = Integer.parseInt(props.getProperty(linesGapKey))
    }
}
