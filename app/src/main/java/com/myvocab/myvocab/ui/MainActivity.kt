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
import com.myvocab.myvocab.util.PreferencesManager
import com.myvocab.myvocab.util.setupToolbar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationHost {

    companion object {
        private val TOP_LEVEL_DESTINATIONS = setOf(
                R.id.navigation_learning,
                R.id.navigation_vocab,
                R.id.navigation_search,
                R.id.navigation_settings
        )
    }

    @Inject
    lateinit var wordRepository: WordRepository

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var wordCountDisposable: Disposable? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = nav_host as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_graph)

        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(bottom_navigation, navController)

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
                    R.id.navigation_vocab
                }
                navController.graph = graph
            }, {
                graph.startDestination = R.id.navigation_vocab
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
