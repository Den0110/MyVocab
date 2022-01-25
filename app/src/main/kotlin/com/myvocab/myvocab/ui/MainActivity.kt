package com.myvocab.myvocab.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.myvocab.commonui.NavigationHost
import com.myvocab.core.util.PreferencesManager
import com.myvocab.core.util.setupToolbar
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.myvocab.MyVocabApp
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.ActivityMainBinding
import com.myvocab.navigation.Navigator
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationHost {

    companion object {
        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_learning,
            R.id.navigation_my_words,
            R.id.navigation_wordlists,
            R.id.navigation_settings
        )
    }

    @Inject
    lateinit var wordRepository: WordRepository

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var navigator: Navigator

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_graph)

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        navigator.navController = navController

        if (!preferencesManager.fastTranslationGuideShowed) {
            graph.startDestination = R.id.navigation_settings
            navController.graph = graph
            return
        }

        if (!(application as MyVocabApp).started) {
            lifecycleScope.launchWhenCreated {
                try {
                    val wordsInLearning = wordRepository.getInLearningWordsCount()

                    graph.startDestination = if (wordsInLearning > 0) {
                        R.id.learning_flow
                    } else {
                        R.id.mywords_flow
                    }
                    navController.graph = graph
                    (application as MyVocabApp).started = true
                } catch (e: Exception) {
                    graph.startDestination = R.id.mywords_flow
                    navController.graph = graph
                }
            }
        } else {
            navController.graph = graph
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            FirebaseAnalytics.getInstance(this).logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW, bundleOf(
                    FirebaseAnalytics.Param.SCREEN_NAME to destination.label.toString()
                )
            )
        }

        Firebase.remoteConfig.apply {
            setDefaultsAsync(R.xml.remote_config_defaults)
        }.fetchAndActivate()

    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id != navController.graph.startDestination) {
            navController.navigateUp()
        } else {
            finish()
        }
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) =
        setupToolbar(toolbar, navController, TOP_LEVEL_DESTINATIONS)

}
