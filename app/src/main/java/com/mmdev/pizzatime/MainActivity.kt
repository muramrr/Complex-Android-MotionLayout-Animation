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

	private var currentPizzaInFocus: Pizza = pizzaList[0]

	private var currentIter = 0


	private var dragDirection: Direction = FORWARD

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		//init name for pizzas
		name1.text = pizzaList[0].name
		name2.text = pizzaList[1].name
		name3.text = pizzaList[2].name
		name4.text = pizzaList[3].name
		name5.text = pizzaList[4].name

		motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
			override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {}


			// 2131231040 -> 2131231012
			// 2131231012 -> 2131231077
			// 2131231077 -> 2131230881
			// 2131230881 -> 2131230913
			override fun onTransitionStarted(motionLayout: MotionLayout, start: Int, end: Int) {

				//Log.wtf("mylogs", "$start $end")


				when (start) {
					R.id.thirdPos -> when (end) {
						R.id.secondPos -> dragDirection = BACK
						R.id.fourthPos -> dragDirection = FORWARD
					}
				}

			}

			override fun onTransitionChange(motionLayout: MotionLayout, start: Int, end: Int, position: Float) {}

			override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {

				val findPizzaInFocus = {
					when (currentId) {
						R.id.firstPos -> currentPizzaInFocus =
							pizzaList.find { it.name == name1.text }!!

						R.id.secondPos -> currentPizzaInFocus =
							pizzaList.find { it.name == name2.text }!!

						R.id.thirdPos -> currentPizzaInFocus =
							pizzaList.find { it.name == name3.text }!!

						R.id.fourthPos -> currentPizzaInFocus =
							pizzaList.find { it.name == name4.text }!!

						R.id.lastPos -> currentPizzaInFocus =
							pizzaList.find { it.name == name5.text }!!

					}
				}

				when (dragDirection) {
					FORWARD -> {
//						if (currentItemInCenterIndex < pizzaList.size - 1) currentItemInCenterIndex++
//						currentPizzaInCenter = pizzaList[currentItemInCenterIndex]

						// (..-5) because 5 images from list are already used
						// size should be greater or equals than 5
						if (currentId == R.id.fourthPos && currentIter < pizzaList.size - 5) {
							forwardChange(motionLayout)
						}
						else findPizzaInFocus.invoke()

					}
					BACK -> {
//						if (currentItemInCenterIndex > 0) currentItemInCenterIndex--
//						currentPizzaInCenter = pizzaList[currentItemInCenterIndex]

						if (currentId == R.id.secondPos && currentIter > 0) {
							backwardChange(motionLayout)
						}
						else findPizzaInFocus.invoke()

					}

				}

				Log.wtf("mylog", "current pizza in focus = ${currentPizzaInFocus.name}")
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
		pizza1.setImageDrawable(pizza2.drawable)
		name1.text = name2.text
		pizza2.setImageDrawable(pizza3.drawable)
		name2.text = name3.text
		pizza3.setImageDrawable(pizza4.drawable)
		name3.text = name4.text
		pizza4.setImageDrawable(pizza5.drawable)
		name4.text = name5.text
		// (...+4) because 4 images from list are already used
		pizza5.setImageResource(pizzaList[currentIter + 4].image)
		name5.text = pizzaList[currentIter + 4].name


		//third pos in focus
		currentPizzaInFocus = pizzaList.find { it.name == name3.text }!!

		//Log.wtf("mylog", "Going forward $currentIter")
	}

	private fun backwardChange(motionLayout: MotionLayout) {
		motionLayout.setTransition(R.id.secondToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIter--
		pizza5.setImageDrawable(pizza4.drawable)
		name5.text = name4.text
		pizza4.setImageDrawable(pizza3.drawable)
		name4.text = name3.text
		pizza3.setImageDrawable(pizza2.drawable)
		name3.text = name2.text
		pizza2.setImageDrawable(pizza1.drawable)
		name2.text = name1.text
		pizza1.setImageResource(pizzaList[currentIter].image)
		name1.text = pizzaList[currentIter].name


		//third pos in focus
		currentPizzaInFocus = pizzaList.find { it.name == name3.text }!!

		//Log.wtf("mylog", "Going backward $currentIter")
	}
}