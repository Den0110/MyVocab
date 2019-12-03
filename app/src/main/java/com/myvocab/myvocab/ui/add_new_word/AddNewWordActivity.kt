package com.myvocab.myvocab.ui.add_new_word

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.databinding.ActivityAddNewWordBinding
import com.myvocab.myvocab.ui.add_new_word.AddNewWordViewModel.Companion.TEST_WORDS
import com.myvocab.myvocab.ui.search.WordSetListAdapter
import com.myvocab.myvocab.util.Resource
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_add_new_word.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import javax.inject.Inject


class AddNewWordActivity : DaggerAppCompatActivity() {

    companion object {
        const val TAG = "AddNewWordFragment"
        const val CHOOSE_FILE_RESULT_CODE = 1
        const val PERMISSION_REQUEST_CODE = 2
    }

    private lateinit var binding: ActivityAddNewWordBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: AddNewWordViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_word)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this, viewModelFactory).get(AddNewWordViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (BuildConfig.DEBUG) {
            add_test_words.visibility = View.VISIBLE
            add_test_words.setOnClickListener {
                viewModel.addWords(TEST_WORDS).subscribe({
                    finish()
                }, { e ->
                    Log.e(TAG, e.message)
                    Snackbar.make(it, "Error, words haven't added", Snackbar.LENGTH_SHORT).show()
                })
            }
        }

        add_btn.setOnClickListener {
            if (allDataCompleted()) {
                compositeDisposable.clear()
                compositeDisposable.add(
                        viewModel.addWord().subscribe({
                            finish()
                        }, { e ->
                            Log.e(TAG, e.message)
                            Snackbar.make(it, "Error, word haven't added", Snackbar.LENGTH_SHORT).show()
                        })
                )
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_new_word, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.import_csv -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    chooseFile()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PERMISSION_REQUEST_CODE)
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.size == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseFile()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_FILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val uri = data.data!!
                compositeDisposable.clear()
                compositeDisposable.add(
                        viewModel.addWordsFromFile(uri).subscribe({
                            finish()
                        }, {
                            Snackbar.make(container, "Error, words haven't added", Snackbar.LENGTH_SHORT).show()
                        })
                )
            }
        }
    }

    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "file/*"
        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE)
    }

    private fun allDataCompleted(): Boolean {
        var ifAllDataCompleted = true
        if (viewModel.translation.value.isNullOrBlank()) {
            translation_til.error = getString(R.string.enter_translation)
            translation_edt.requestFocus()
            ifAllDataCompleted = false
        } else {
            translation_til.isErrorEnabled = false
        }
        if (viewModel.newWord.value.isNullOrBlank()) {
            new_word_til.error = getString(R.string.enter_new_word)
            new_word_edt.requestFocus()
            ifAllDataCompleted = false
        } else {
            new_word_til.isErrorEnabled = false
        }
        return ifAllDataCompleted
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
