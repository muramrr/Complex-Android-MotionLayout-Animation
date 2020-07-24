package com.mmdev.pizzatime

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.mmdev.pizzatime.MainActivity.Direction.BACK
import com.mmdev.pizzatime.MainActivity.Direction.FORWARD
import com.mmdev.pizzatime.MainActivity.PizzaSize.*
import com.mmdev.pizzatime.MainActivity.PizzaToppings.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pizza_views.*


class MainActivity: AppCompatActivity() {

	private enum class Direction { BACK, FORWARD }
	private enum class PizzaSize { S, M, L }
	private enum class PizzaToppings { MUSHROOMS, TOMATOES, BACON, CHEESE, CHILI }

	private data class PizzaSizeAndPrice (val size: PizzaSize, val price: Int)
	private data class PizzaToppingAndPrice (val topping: PizzaToppings, val price: Int)

	private val pizzaToppingsList = listOf(
			PizzaToppingAndPrice(MUSHROOMS, 1),
			PizzaToppingAndPrice(TOMATOES, 2),
			PizzaToppingAndPrice(BACON, 3),
			PizzaToppingAndPrice(CHEESE, 1),
			PizzaToppingAndPrice(CHILI, 2)
	)

	private val pizzaSizeList = listOf(
			PizzaSizeAndPrice (S, 3),
			PizzaSizeAndPrice (M, 5),
			PizzaSizeAndPrice (L, 7)
	)

	private data class Pizza(val name: String,
	                         val image: Int,
	                         var price: Int,
	                         var size: PizzaSizeAndPrice = PizzaSizeAndPrice (M, 5),
	                         val toppings: MutableList<PizzaToppingAndPrice> = mutableListOf()) {

		fun copy() = Pizza (name, image, price, size, toppings)

	}



	// size should be >= 5
	private val pizzaList = listOf(
			Pizza("Chef's pizza", R.drawable.pizza_1_firmennaya, 10),
			Pizza("Bavarian", R.drawable.pizza_2_bavarska, 12),
			Pizza("Margherita", R.drawable.pizza_3_margarita, 10),
			Pizza("Meat pizza", R.drawable.pizza_4_myasna, 13),
			Pizza("Village pizza", R.drawable.pizza_5_po_selyanski, 11),
			Pizza("Salami pizza", R.drawable.pizza_6_salyzmi, 9),
			Pizza("Vegetarian", R.drawable.pizza_7_vegetarianska, 8)
	)

	private var pizza1 = pizzaList[0]
	private var pizza2 = pizzaList[1]
	private var pizza3 = pizzaList[2]
	private var pizza4 = pizzaList[3]
	private var pizza5 = pizzaList[4]

	private var currentPizzaInFocus: Pizza = pizzaList[0]

	private var sizeSelected : PizzaSizeAndPrice = pizzaSizeList[1]

	private var currentIter = 0
	private var dragDirection: Direction = FORWARD





	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		setupViews()

		setupInitialState()

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
						R.id.firstPos -> {
							currentPizzaInFocus = pizza1.copy()
							setupInitialState()
						}
						R.id.secondPos -> { currentPizzaInFocus = pizza2.copy() }
						R.id.thirdPos -> { currentPizzaInFocus = pizza3.copy() }
						R.id.fourthPos -> { currentPizzaInFocus = pizza4.copy() }
						R.id.lastPos -> { currentPizzaInFocus = pizza5.copy() }
					}
				}

				when (dragDirection) {
					FORWARD -> {
						// (..-5) because 5 images from list are already used
						// size should be greater or equals than 5
						if (currentId == R.id.fourthPos && currentIter < pizzaList.size - 5) {
							forwardChange(motionLayout)
						}
						else findPizzaInFocus.invoke()
					}
					BACK -> {
						if (currentId == R.id.secondPos && currentIter > 0) {
							backwardChange(motionLayout)
						}
						else findPizzaInFocus.invoke()
					}
				}

				if (currentId !in arrayOf(R.id.pizzaCustomizeState,
				                          R.id.preCustomizerState,
				                          R.id.firstPos)) {
					pizzaName.setText(currentPizzaInFocus.name)
					applySizeToCurrentPizzaAndDisplay(sizeSelected)
				}

			}
		})

	}

	private fun setupInitialState() {
		//init imgs for pizza
		pizzaImg_1.setImageResource(pizzaList[0].image)
		pizzaImg_2.setImageResource(pizzaList[1].image)
		pizzaImg_3.setImageResource(pizzaList[2].image)
		pizzaImg_4.setImageResource(pizzaList[3].image)
		pizzaImg_5.setImageResource(pizzaList[4].image)

		pizza1 = pizzaList[0]
		pizza2 = pizzaList[1]
		pizza3 = pizzaList[2]
		pizza4 = pizzaList[3]
		pizza5 = pizzaList[4]

		currentPizzaInFocus = pizzaList[0].copy()
		currentPizzaInFocus.price

		//select size by default M
		setSelectedSizeM()

		currentIter = 0
		dragDirection = FORWARD

		//init first prices and names
		pizzaName.setText(currentPizzaInFocus.name)

		//clear toppings selection
		btnAddMushrooms.isSelected = false
		btnAddTomato.isSelected = false
		btnAddBacon.isSelected = false
		btnAddCheese.isSelected = false
		btnAddChili.isSelected = false
	}

	private fun setupViews() {
		val initialTranslationX = ivHotIndicator.translationX
		//back to pizzas carousel
		toolbarBackBtn.setOnClickListener {
			motionLayout.setTransition(R.id.backToPizzaCarousel)
			motionLayout.transitionToEnd()
			it.isClickable = false
			pizzaViewsBounds.isClickable = true
		}

		//open pizza customizer
		pizzaViewsBounds.setOnClickListener {
			pizzaImg_1.setImageResource(currentPizzaInFocus.image)
			motionLayout.setTransition(R.id.openCustomizer)
			motionLayout.transitionToEnd()
			//prevent to click again on this
			it.isClickable = false
			toolbarBackBtn.isClickable = true
		}

		// pizza name appearance anim
		val inAnimName = AnimationUtils.loadAnimation(this, android.R.anim.fade_in).apply {
			duration = 300
		}
		// pizza name disappearing anim
		val outAnimName = AnimationUtils.loadAnimation(this, android.R.anim.fade_out).apply {
			duration = 300
		}

		//apply anim to textSwitcher
		pizzaName.apply {
			inAnimation = inAnimName
			outAnimation = outAnimName
		}

		// pizza price appearance anim
		val inAnimPrice = AnimationUtils.loadAnimation(this, R.anim.price_in)

		// pizza price disappearing anim
		val outAnimPrice = AnimationUtils.loadAnimation(this, R.anim.price_out)

		pizzaPrice.apply {
			inAnimation = inAnimPrice
			outAnimation = outAnimPrice
		}

		btnSize_S.setOnClickListener { setSelectedSizeS() }
		btnSize_M.setOnClickListener { setSelectedSizeM() }
		btnSize_L.setOnClickListener { setSelectedSizeL() }

		btnAddMushrooms.setOnClickListener {
			with(pizzaToppingsList[0]){
				if (it.isSelected) {
					it.isSelected = false
					currentPizzaInFocus.toppings.remove(this)
					decreaseCurrentPizzaPrice(this.price)
				}
				else {
					it.isSelected = true
					currentPizzaInFocus.toppings.add(this)
					increaseCurrentPizzaPrice(this.price)
				}
			}
		}

		btnAddTomato.setOnClickListener {
			with(pizzaToppingsList[1]){
				if (it.isSelected) {
					it.isSelected = false
					currentPizzaInFocus.toppings.remove(this)
					decreaseCurrentPizzaPrice(this.price)
				}
				else {
					it.isSelected = true
					currentPizzaInFocus.toppings.add(this)
					increaseCurrentPizzaPrice(this.price)
				}
			}
		}

		btnAddBacon.setOnClickListener {
			with(pizzaToppingsList[2]){
				if (it.isSelected) {
					it.isSelected = false
					currentPizzaInFocus.toppings.remove(this)
					decreaseCurrentPizzaPrice(this.price)
				}
				else {
					it.isSelected = true
					currentPizzaInFocus.toppings.add(this)
					increaseCurrentPizzaPrice(this.price)
				}
			}
		}

		btnAddCheese.setOnClickListener {
			with(pizzaToppingsList[3]){
				if (it.isSelected) {
					it.isSelected = false
					currentPizzaInFocus.toppings.remove(this)
					decreaseCurrentPizzaPrice(this.price)
				}
				else {
					it.isSelected = true
					currentPizzaInFocus.toppings.add(this)
					increaseCurrentPizzaPrice(this.price)
				}
			}
		}

		btnAddChili.setOnClickListener {
			with(pizzaToppingsList[4]){
				if (it.isSelected) {
					it.isSelected = false
					currentPizzaInFocus.toppings.remove(this)
					decreaseCurrentPizzaPrice(this.price)
					ivHotIndicator.animate().translationX(initialTranslationX).setDuration(500)
				}
				else {
					it.isSelected = true
					currentPizzaInFocus.toppings.add(this)
					increaseCurrentPizzaPrice(this.price)
					ivHotIndicator.animate().translationX(0f).setDuration(500)
				}
			}
		}
	}

	/**
	 * This method primary use to imitate views loop when swiping forward
	 * Swap between thirdPos and fourthPos
	 * ThirdPos always in center
	 */
	private fun forwardChange(motionLayout: MotionLayout) {
		motionLayout.setTransition(R.id.fourthToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIter++
		pizzaImg_1.setImageDrawable(pizzaImg_2.drawable)
		pizza1 = pizza2
		pizzaImg_2.setImageDrawable(pizzaImg_3.drawable)
		pizza2 = pizza3
		pizzaImg_3.setImageDrawable(pizzaImg_4.drawable)
		pizza3 = pizza4
		pizzaImg_4.setImageDrawable(pizzaImg_5.drawable)
		pizza4 = pizza5
		// (...+4) because 4 images from list are already used
		pizza5 = pizzaList[currentIter + 4]
		pizzaImg_5.setImageResource(pizzaList[currentIter + 4].image)


		//third pos in focus
		currentPizzaInFocus = pizza3.copy()
	}

	/**
	 * This method primary use to imitate views loop when swiping back
	 * Swap between secondPos and thirdPos
	 * ThirdPos always in center
	 */
	private fun backwardChange(motionLayout: MotionLayout) {
		motionLayout.setTransition(R.id.secondToThird).also {
			motionLayout.setTransitionDuration(0)
		}
		motionLayout.transitionToEnd()

		currentIter--
		pizzaImg_5.setImageDrawable(pizzaImg_4.drawable)
		pizza5 = pizza4
		pizzaImg_4.setImageDrawable(pizzaImg_3.drawable)
		pizza4 = pizza3
		pizzaImg_3.setImageDrawable(pizzaImg_2.drawable)
		pizza3 = pizza2
		pizzaImg_2.setImageDrawable(pizzaImg_1.drawable)
		pizza2 = pizza1
		pizzaImg_1.setImageResource(pizzaList[currentIter].image)
		pizza1 = pizzaList[currentIter]


		//third pos in focus
		currentPizzaInFocus = pizza3.copy()
	}


	private fun setSelectedSizeS() {
		btnSize_S.isSelected = true
		btnSize_M.isSelected = false
		btnSize_L.isSelected = false

		btnSize_S.isClickable = false
		btnSize_M.isClickable = true
		btnSize_L.isClickable = true

		sizeSelected = pizzaSizeList[0]
		applySizeToCurrentPizzaAndDisplay(sizeSelected)
	}

	private fun setSelectedSizeM() {
		btnSize_S.isSelected = false
		btnSize_M.isSelected = true
		btnSize_L.isSelected = false

		btnSize_S.isClickable = true
		btnSize_M.isClickable = false
		btnSize_L.isClickable = true

		sizeSelected = pizzaSizeList[1]
		applySizeToCurrentPizzaAndDisplay(sizeSelected)
	}

	private fun setSelectedSizeL() {
		btnSize_S.isSelected = false
		btnSize_M.isSelected = false
		btnSize_L.isSelected = true

		btnSize_S.isClickable = true
		btnSize_M.isClickable = true
		btnSize_L.isClickable = false

		sizeSelected = pizzaSizeList[2]
		applySizeToCurrentPizzaAndDisplay(sizeSelected)
	}

	private fun increaseCurrentPizzaPrice(price: Int) {
		currentPizzaInFocus.price += price
		pizzaPrice.setText("$${currentPizzaInFocus.price}")
	}

	private fun decreaseCurrentPizzaPrice(price: Int) {
		currentPizzaInFocus.price -= price
		pizzaPrice.setText("$${currentPizzaInFocus.price}")
	}

	private fun applySizeToCurrentPizzaAndDisplay(pizzaSizeAndPrice: PizzaSizeAndPrice) {
		if (currentPizzaInFocus.size != pizzaSizeAndPrice){
			currentPizzaInFocus.price -= currentPizzaInFocus.size.price
			currentPizzaInFocus.size = pizzaSizeAndPrice
			currentPizzaInFocus.price += pizzaSizeAndPrice.price
		}
		else currentPizzaInFocus.price += currentPizzaInFocus.size.price

		pizzaPrice.setText("$${currentPizzaInFocus.price}")
	}
}