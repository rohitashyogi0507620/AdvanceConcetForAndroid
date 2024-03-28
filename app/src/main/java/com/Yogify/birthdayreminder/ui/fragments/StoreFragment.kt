package com.Yogify.birthdayreminder.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.FragmentHomeBinding
import com.Yogify.birthdayreminder.databinding.FragmentStoreBinding
import com.Yogify.birthdayreminder.model.SliderItem
import com.Yogify.birthdayreminder.ui.adapters.SliderAdapter
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations

class StoreFragment : BaseFragment() {

    lateinit var binding: FragmentStoreBinding
    override fun getBundle() {
        super.getBundle()
    }

    override fun initViews() {
        super.initViews()

        var adapter=SliderAdapter(requireContext())
        adapter.addItem(SliderItem("https://www.fnp.com/images/pr/l/v20230321114904/sip-of-love-birthday-mug-hand-delivery_1.jpg","Sip of Love Birthday Mug- Hand Delivery"))
        adapter.addItem(SliderItem("https://www.fnp.com/images/pr/l/v20240215162207/personalised-sweet-birthday-combo_1.jpg","Personalised Sweet Birthday Combo"))
        adapter.addItem(SliderItem("https://www.fnp.com/images/pr/l/v20220706181839/timeless-love-red-roses-bouquet-chocolate-cake_1.jpg","Timeless Love Red Roses Bouquet & Chocolate Cake"))
        adapter.addItem(SliderItem("https://www.fnp.com/images/pr/l/v20230321114904/sip-of-love-birthday-mug-hand-delivery_1.jpg","Sip of Love Birthday Mug- Hand Delivery"))
        adapter.addItem(SliderItem("https://www.fnp.com/images/pr/l/v20240215162207/personalised-sweet-birthday-combo_1.jpg","Personalised Sweet Birthday Combo"))
        adapter.addItem(SliderItem("https://www.fnp.com/images/pr/l/v20220706181839/timeless-love-red-roses-bouquet-chocolate-cake_1.jpg","Timeless Love Red Roses Bouquet & Chocolate Cake"))

        binding.imageSlider.setSliderAdapter(adapter)

        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)

    }

    override fun observers() {
        super.observers()
    }

    override fun onClickListener() {
        super.onClickListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(layoutInflater)
        return binding.root
    }
    
}