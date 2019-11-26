package com.myvocab.myvocab.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.myvocab.myvocab.R
import com.myvocab.myvocab.common.fasttranslation.FastTranslationServiceStarter
import com.myvocab.myvocab.util.getFastTranslationState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(this, R.id.nav_host)

        NavigationUI.setupWithNavController(toolbar, navController, AppBarConfiguration.Builder(
                    setOf(R.id.learningFragment, R.id.vocabFragment, R.id.settingsFragment
                )).build())
        NavigationUI.setupWithNavController(bottom_navigation, navController)

        if(getFastTranslationState(this)){
            FastTranslationServiceStarter.start(this)
        }

    }

    override fun onSupportNavigateUp() =
            findNavController(this, R.id.nav_host).navigateUp()

}
