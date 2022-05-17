//package com.example.test30
//
//
//import android.content.Context
//import android.view.GestureDetector
//import android.view.MotionEvent
//import android.view.View
//import androidx.recyclerview.widget.RecyclerView
//
//
///**
// * Created by VMac on 06/01/17.
// */
//class RecyclerTouchListener(
//    context: Context?,
//    recyclerView: RecyclerView,
//    clickListener: ClickListener?
//) :
//    RecyclerView.OnItemTouchListener {
//    private val gestureDetector: GestureDetector
//    private val clickListener: ClickListener?
//    fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//        val child: View = rv.findChildViewUnder(e.x, e.y)
//        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//            clickListener.onClick(child, rv.getChildPosition(child))
//        }
//        return false
//    }
//
//    fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {}
//    fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//
//    init {
//        this.clickListener = clickListener
//        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
//            override fun onSingleTapUp(e: MotionEvent): Boolean {
//                return true
//            }
//
//            override fun onLongPress(e: MotionEvent) {
//                val child: View = recyclerView.findChildViewUnder(e.x, e.y)
//                if (child != null && clickListener != null) {
//                    clickListener.onLongClick(child, recyclerView.getChildPosition(child))
//                }
//            }
//        })
//    }
//}