package com.myvocab.myvocab.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.myvocab.myvocab.MyVocabApp
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.databinding.ActivityMainBinding
import com.myvocab.myvocab.util.PreferencesManager
import com.myvocab.myvocab.util.setupToolbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
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

    private lateinit var binding: ActivityMainBinding

    private var wordCountDisposable: Disposable? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_graph)

        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        if(!preferencesManager.fastTranslationGuideShowed) {
            graph.startDestination = R.id.navigation_settings
            navController.graph = graph
            return
        }

        if (!(application as MyVocabApp).started) {
            wordCountDisposable = wordRepository.getInLearningWordsCount().subscribe({
                graph.startDestination = if (it > 0) {
                    R.id.navigation_learning
                } else {
                    R.id.navigation_my_words
                }
                navController.graph = graph
            }, {
                graph.startDestination = R.id.navigation_my_words
                navController.graph = graph
            })
            (application as MyVocabApp).started = true
        } else {
            navController.graph = graph
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // log screen
            FirebaseAnalytics.getInstance(this).setCurrentScreen(this, destination.label.toString(), null)
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

    override fun onDestroy() {
        super.onDestroy()
        wordCountDisposable?.dispose()
    }

}
