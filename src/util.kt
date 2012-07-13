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