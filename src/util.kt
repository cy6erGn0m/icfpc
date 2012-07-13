package util

fun _assert(c: Boolean, message: String) {
    if (!c) {
        throw AssertionError(message)
    }
}