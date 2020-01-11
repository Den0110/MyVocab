package com.myvocab.myvocab.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.source.WordRepository
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

    private var wordCountDisposable: Disposable? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = nav_host as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_graph)


        wordCountDisposable = wordRepository.getInLearningWordsCount().subscribe({
            graph.startDestination = if (it > 0) {
                R.id.navigation_learning
            } else {
                R.id.navigation_vocab
            }
            navHostFragment.navController.graph = graph
        }, {
            graph.startDestination = R.id.navigation_vocab
            navHostFragment.navController.graph = graph
        })

        navController = findNavController(this, R.id.nav_host)

        NavigationUI.setupWithNavController(bottom_navigation, navController)

    }

    override fun onSupportNavigateUp() =
            navController.navigateUp()

    override fun registerToolbarWithNavigation(toolbar: Toolbar) =
            setupToolbar(toolbar, navController, TOP_LEVEL_DESTINATIONS)

    override fun onDestroy() {
        super.onDestroy()
        wordCountDisposable?.dispose()
    }

}
