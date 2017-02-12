package sreich.countthedays

import android.content.Context

/**
 * Created by sreich on 2/12/17.
 */

fun Context.i18n(resourceId: Int) = getString(resourceId)!!

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

