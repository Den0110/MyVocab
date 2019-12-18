package com.myvocab.myvocab.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.myvocab.myvocab.R
import com.myvocab.myvocab.common.fasttranslation.FastTranslationServiceStarter
import com.myvocab.myvocab.data.source.local.Database
import com.myvocab.myvocab.util.getFastTranslationState
import com.myvocab.myvocab.util.setupToolbar
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationHost {

    companion object {
        private val TOP_LEVEL_DESTINATIONS = setOf(
                R.id.navigation_learning,
                R.id.navigation_vocab,
                R.id.navigation_search,
                R.id.navigation_settings
        )
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(this, R.id.nav_host)

        NavigationUI.setupWithNavController(bottom_navigation, navController)

        if (getFastTranslationState(this)) {
            FastTranslationServiceStarter.start(this)
        }

    }

    override fun onSupportNavigateUp() =
            navController.navigateUp()

    override fun registerToolbarWithNavigation(toolbar: Toolbar) =
            setupToolbar(toolbar, navController, TOP_LEVEL_DESTINATIONS)

}
