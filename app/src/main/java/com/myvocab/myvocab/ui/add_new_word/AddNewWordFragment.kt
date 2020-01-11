package com.myvocab.myvocab.ui.add_new_word

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentAddNewWordBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.findNavController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_add_new_word.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject

class AddNewWordFragment : MainNavigationFragment() {

    companion object {
        private const val CHOOSE_FILE_RESULT_CODE = 1
        private const val PERMISSION_REQUEST_CODE = 2
    }

    private lateinit var binding: FragmentAddNewWordBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: AddNewWordViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_word, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(AddNewWordViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        toolbar.inflateMenu(R.menu.add_new_word)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.import_csv -> {
                    if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        chooseFile()
                    } else {
                        ActivityCompat.requestPermissions(activity!!, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ), PERMISSION_REQUEST_CODE)
                    }
                }
            }
            true
        }

        add_btn.setOnClickListener {
            if (allDataCompleted()) {
                compositeDisposable.clear()
                compositeDisposable.add(
                        viewModel.addWord().subscribe({
                            findNavController().navigateUp()
                        }, { e ->
                            Timber.e(e)
                            Snackbar.make(it, "Error, word wasn't added", Snackbar.LENGTH_SHORT).show()
                        })
                )
            }
        }

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
                            findNavController().navigateUp()
                        }, {
                            Snackbar.make(container, "Error, words weren't added", Snackbar.LENGTH_SHORT).show()
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
