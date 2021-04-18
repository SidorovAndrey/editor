package tokenizer

import core.Text

class Tokenizer(private val text: Text) {
    private val keywords = hashSetOf("abstract", "assert", "boolean",
        "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "extends", "false",
        "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native",
        "new", "null", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "transient", "true",
        "try", "void", "volatile", "while")

    var state: TokenKinds = TokenKinds.NOTHING
        private set

    var linesState: MutableList<TokenKinds> = mutableListOf()
        private set

    init {
        val lines = text.getRange(1, text.totalLines)
        for (line in lines) {
            val tokenized = tokenizeLine(line)
            if (tokenized.any()) {
                val lineState = tokenizeLine(line).last().kind
                linesState.add(lineState)
                state = lineState
            }
        }
    }

    private fun tokenizeLine(line: String): MutableList<Token> {
        val res = mutableListOf<Token>()
        var idx = 0
        while (idx < line.length) {
            if (state == TokenKinds.COMMENT) {
                // trying to read 'till the end of the comment
                val commentRes = readToCommentEnd(line, idx)
                val comment = commentRes.first
                idx = commentRes.second
                res.add(Token(comment, TokenKinds.COMMENT))
                state = if (comment.endsWith("*/")) TokenKinds.NOTHING else TokenKinds.COMMENT
            }

            // trying to read string
            val stringRes = readString(line, idx)
            val string = stringRes.first
            idx = stringRes.second
            if (string.isNotEmpty()) {
                res.add(Token(string, TokenKinds.STRING))
                state = TokenKinds.NOTHING
            }

            // trying to read word
            val wordRes = readWord(line, idx)
            val word = wordRes.first
            idx = wordRes.second
            if (word.isNotEmpty()) {
                val kind = if (keywords.contains(word)) TokenKinds.KEYWORD else TokenKinds.IDENTIFIER
                res.add(Token(word, kind))
                state = TokenKinds.NOTHING
            }

            // trying to read comment
            val commentRes = readComment(line, idx)
            val comment = commentRes.first
            idx = commentRes.second
            if (comment.isNotEmpty()) {
                res.add(Token(comment, TokenKinds.COMMENT))
                state = if (comment.startsWith("/*") && !comment.endsWith("*/")) TokenKinds.COMMENT else TokenKinds.NOTHING
            }

            // trying to read 'nothing'
            val nothingRes = readNothing(line, idx)
            val nothing = nothingRes.first
            idx = nothingRes.second
            if (nothing.isNotEmpty()) {
                res.add(Token(nothing, TokenKinds.NOTHING))
                state = TokenKinds.NOTHING
            }
        }

        return res
    }

    fun getRange(start: Int, end: Int): MutableList<Token> {
        val lines = text.getRange(start, end)
        val res = mutableListOf<Token>()
        for (line in lines) {
            val tokenized = tokenizeLine(line)
            res.addAll(tokenized)
            res.add(Token("", TokenKinds.END_LINE))
        }

        return res
    }
}