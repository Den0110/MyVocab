package com.myvocab.myvocab.ui.my_word_sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentMyWordSetsBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import kotlinx.android.synthetic.main.fragment_my_word_sets.*

class MyWordSetsFragment: MainNavigationFragment() {

    companion object {
        private const val TAG = "MyWordSetsFragment"
    }

    private lateinit var binding: FragmentMyWordSetsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_word_sets, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_layout.setupWithViewPager(view_pager)
        view_pager.adapter = MyWordSetsPagerAdapter(context!!, childFragmentManager)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                if (position == 0){
                    add_new_word_set_btn.show()
                } else {
                    add_new_word_set_btn.hide()
                }
            }
        })

        add_new_word_set_btn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigation_search)
        }

    }

}