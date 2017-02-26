package sreich.countthedays


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: ClickListener) : RecyclerView.OnItemTouchListener {
    interface ClickListener {
        fun onClick(view: View, position: Int)
        fun onLongClick(view: View, position: Int)
    }

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent) = true

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && true) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val child = view.findChildViewUnder(e.x, e.y)
        if (child != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, view.getChildAdapterPosition(child))
            return true
        }

        return false
    }

    override fun onTouchEvent(view: RecyclerView, e: MotionEvent) = Unit
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
}