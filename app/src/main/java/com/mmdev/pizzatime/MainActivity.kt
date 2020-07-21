package com.mmdev.pizzatime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cart_with_badge.*

class MainActivity: AppCompatActivity() {

	private val pizzaList = arrayListOf(
			R.drawable.pizza_1_firmennaya,
			R.drawable.pizza_2_bavarska,
			R.drawable.pizza_3_margarita,
			R.drawable.pizza_4_myasna,
			R.drawable.pizza_5_po_selyanski,
			R.drawable.pizza_6_salyzmi,
			R.drawable.pizza_7_vegetarianska
	)
	private var currentIndex = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		motionLayout.addTransitionListener(object : MotionLayout.TransitionListener{
			override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {}

			override fun onTransitionStarted(motionLayout: MotionLayout, start: Int, end: Int) {}

			override fun onTransitionChange(motionLayout: MotionLayout, start: Int, end: Int, position: Float) {}

			override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
				if (currentId == R.id.thirdPos && currentIndex < pizzaList.size-4) {
					forwardChange(motionLayout)
				}
				else if (currentId == R.id.thirdPos && currentIndex == pizzaList.size-4){
					//backwardChange(motionLayout)
				}

			}
		})
		//tvCardBadgeCount.visibility = View.GONE
	}

	private fun forwardChange(motionLayout: MotionLayout){
		motionLayout.setTransition(R.id.thirdPos, R.id.secondPos)
		motionLayout.setTransitionDuration(0)
		motionLayout.transitionToEnd()
		currentIndex++
		v1.setImageDrawable(v2.drawable)
		v2.setImageDrawable(v3.drawable)
		v3.setImageDrawable(v4.drawable)
		v4.setImageResource(pizzaList[currentIndex+3])
		Log.wtf("mylog", "Going forward $currentIndex")
	}

	private fun backwardChange(motionLayout: MotionLayout){
		motionLayout.setTransition(R.id.thirdPos, R.id.secondPos)
		motionLayout.setTransitionDuration(0)
		motionLayout.transitionToEnd()
		currentIndex--
		v4.setImageResource(currentIndex)
		v1.setImageResource(pizzaList[currentIndex])
		v2.setImageResource(pizzaList[currentIndex-1])
		v3.setImageResource(pizzaList[currentIndex-2])
		v4.setImageResource(pizzaList[currentIndex-3])
		Log.wtf("mylog", "Going backward $currentIndex")
	}
}