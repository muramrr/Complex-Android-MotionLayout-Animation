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
	private data class Pizza(var name: String, var image: Int)


	// size should be >= 5
	private val pizzaList = listOf(
			Pizza("Chef's pizza", R.drawable.pizza_1_firmennaya),
			Pizza("Bavarian pizza", R.drawable.pizza_2_bavarska),
			Pizza("Margherita", R.drawable.pizza_3_margarita),
			Pizza("Meat pizza", R.drawable.pizza_4_myasna),
			Pizza("Village pizza", R.drawable.pizza_5_po_selyanski) ,
			Pizza("Salami pizza", R.drawable.pizza_6_salyzmi) ,
			Pizza("Vegetarian pizza", R.drawable.pizza_7_vegetarianska)
	)

	private var currentPizzaInCenter: Pizza = pizzaList[0]

	private var currentIter = 0
	private var currentItemInCenterIndex = 0

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
			}

			override fun onTransitionChange(motionLayout: MotionLayout, start: Int, end: Int, position: Float) {}

			override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {

				when (dragDirection) {
					FORWARD -> {
//						if (currentItemInCenterIndex < pizzaList.size - 1) currentItemInCenterIndex++
//						currentPizzaInCenter = pizzaList[currentItemInCenterIndex]

						// (..-5) because 5 images from list are already used
						// size should be greater or equals than 5
						if (currentId == R.id.fourthPos && currentIter < pizzaList.size - 5) {
							forwardChange(motionLayout)
						}
					}
					BACK -> {
//						if (currentItemInCenterIndex > 0) currentItemInCenterIndex--
//						currentPizzaInCenter = pizzaList[currentItemInCenterIndex]


						if (currentId == R.id.secondPos && currentIter > 0){
							backwardChange(motionLayout)
						}

					}
				}
				tvPizzaName.text = currentPizzaInCenter.name
			}
		})
		//tvCardBadgeCount.visibility = View.GONE
	}

	private fun forwardChange(motionLayout: MotionLayout){
		motionLayout.setTransition(R.id.fourthToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIter++
		v1.setImageDrawable(v2.drawable)
		v2.setImageDrawable(v3.drawable)
		v3.setImageDrawable(v4.drawable)
		v4.setImageDrawable(v5.drawable)
		// (...+4) because 4 images from list are already used
		v5.setImageResource(pizzaList[currentIter + 4].image)
		currentPizzaInCenter = pizzaList[currentIter + 2]
		Log.wtf("mylog", "Going forward $currentIter")
	}

	private fun backwardChange(motionLayout: MotionLayout){
		motionLayout.setTransition(R.id.secondToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIter--
		v5.setImageDrawable(v4.drawable)
		v4.setImageDrawable(v3.drawable)
		v3.setImageDrawable(v2.drawable)
		v2.setImageDrawable(v1.drawable)
		v1.setImageResource(pizzaList[currentIter].image)
		currentPizzaInCenter = pizzaList[currentIter + 2]
		Log.wtf("mylog", "Going backward $currentIter")
	}
}