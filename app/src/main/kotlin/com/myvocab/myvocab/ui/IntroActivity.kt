package com.myvocab.myvocab.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.myvocab.myvocab.R
import com.myvocab.myvocab.ui.intro.IntroEnableFastTranslationFragment
import com.myvocab.myvocab.ui.intro.IntroWordListsFragment
import com.myvocab.myvocab.ui.intro.SplashFragment
import com.myvocab.myvocab.util.PreferencesManager
import dagger.android.AndroidInjection
import javax.inject.Inject

class IntroActivity : AppIntro() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if (preferencesManager.introShowed) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        addSlide(SplashFragment())

        addSlide(IntroWordListsFragment())

        addSlide(IntroEnableFastTranslationFragment())

//        addSlide(IntroFastTranslationFragment())

        val primaryTextColor = ContextCompat.getColor(this, R.color.primaryTextColor)
        val secondaryTextColor = ContextCompat.getColor(this, R.color.secondaryTextColor)

        setSeparatorColor(ContextCompat.getColor(this, android.R.color.transparent))
        setIndicatorColor(primaryTextColor, secondaryTextColor)
        setNextArrowColor(primaryTextColor)
        setColorDoneText(primaryTextColor)
        setColorSkipButton(ContextCompat.getColor(this, R.color.captionTextColor))

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        exit()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        exit()
    }

//    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
//        super.onSlideChanged(oldFragment, newFragment)
//        if(newFragment is IntroFastTranslationFragment){
//            newFragment.showPrompt()
//        }
//    }

    fun exit(){
        preferencesManager.introShowed = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}