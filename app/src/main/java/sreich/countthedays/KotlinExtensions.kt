package sreich.countthedays

import android.app.Fragment
import android.content.Context

fun Context.i18n(resourceId: Int) = getString(resourceId)!!
fun Fragment.i18n(resourceId: Int) = getString(resourceId)!!

/**
 * calls the given block and then calls a 'finalizing' function.
 * basically like Closeable.use, but can be anything you want
 * @sample
        a.finish(a::finalize) {
            a.start()
            println("mid run")
        }

 */
public inline fun <T> T.finish(finalizer: () -> Unit, block: T.() -> Unit): T {
    block()
    finalizer()
    return this
}

