package com.myvocab.myvocab.ui.vocab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentVocabBinding
import com.myvocab.myvocab.ui.BaseFragment
import com.myvocab.myvocab.ui.add_new_word.AddNewWordActivity
import kotlinx.android.synthetic.main.fragment_vocab.*

class VocabFragment : BaseFragment() {

    companion object {
        const val TAG = "VocabFragment"
    }

    private lateinit var binding: FragmentVocabBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vocab, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tab_layout.setupWithViewPager(view_pager)
        view_pager.adapter = VocabPagerAdapter(context!!, childFragmentManager)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                if (position == 0){
                    add_new_word_btn.show()
                } else {
                    add_new_word_btn.hide()
                }
            }
        })

        add_new_word_btn.setOnClickListener {
            startActivity(Intent(context, AddNewWordActivity::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}
