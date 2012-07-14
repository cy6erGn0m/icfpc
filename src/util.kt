package util

fun _assert(c: Boolean, message: String) {
    if (!c) {
        throw AssertionError(message)
    }
}

fun String.repeat(count : Int) : String {
    val buf = StringBuilder()
    for (i in 1..count) {
        buf.append(this)
    }
    return buf.toString()!!
}

fun String.trimTrailingSpaces() : String {
    // workaround for downto bug
    if (length == 1) {
        return if (Character.isWhitespace(this[0])) "" else this
    }

    for (i in length - 1 downto 0) {
        if (!Character.isWhitespace(this[i])) {
            return substring(0, i + 1)
        }
    }
    return ""
}