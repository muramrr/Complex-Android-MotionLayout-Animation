package com.mmdev.pizzatime

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.mmdev.pizzatime.MainActivity.Direction.BACK
import com.mmdev.pizzatime.MainActivity.Direction.FORWARD
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

	private enum class Direction { BACK, FORWARD }

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

	private var dragDirection: Direction = FORWARD

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		motionLayout.addTransitionListener(object : MotionLayout.TransitionListener{
			override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {}


			// 2131231040 -> 2131231012
			// 2131231012 -> 2131231077
			// 2131231077 -> 2131230881
			// 2131230881 -> 2131230913
			override fun onTransitionStarted(motionLayout: MotionLayout, start: Int, end: Int) {
				if (start == R.id.thirdPos)
					when (end) {
						R.id.secondPos -> dragDirection = BACK
						R.id.fourthPos -> dragDirection = FORWARD
					}
				Log.wtf("mylog", dragDirection.name + " $start to $end ")
			}

			override fun onTransitionChange(motionLayout: MotionLayout, start: Int, end: Int, position: Float) {}

			override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
				when (dragDirection) {
					FORWARD -> {
						if (currentId == R.id.fourthPos && currentIndex < pizzaList.size-5) {
							forwardChange(motionLayout)
						}
					}
					BACK -> {
						if (currentId == R.id.secondPos && currentIndex > 0){
							backwardChange(motionLayout)
						}

					}
				}

			}
		})
		//tvCardBadgeCount.visibility = View.GONE
	}

	private fun forwardChange(motionLayout: MotionLayout){
		motionLayout.setTransition(R.id.fourthToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIndex++
		v1.setImageDrawable(v2.drawable)
		v2.setImageDrawable(v3.drawable)
		v3.setImageDrawable(v4.drawable)
		v4.setImageDrawable(v5.drawable)
		v5.setImageResource(pizzaList[currentIndex+4])
		Log.wtf("mylog", "Going forward $currentIndex")
	}

	private fun backwardChange(motionLayout: MotionLayout){
		motionLayout.setTransition(R.id.secondToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIndex--
		v5.setImageDrawable(v4.drawable)
		v4.setImageDrawable(v3.drawable)
		v3.setImageDrawable(v2.drawable)
		v2.setImageDrawable(v1.drawable)
		v1.setImageResource(pizzaList[currentIndex])
		Log.wtf("mylog", "Going backward $currentIndex")
	}
}