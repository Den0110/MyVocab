package com.myvocab.myvocab.ui.add_new_word

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.databinding.AddWordExampleBinding
import com.myvocab.myvocab.databinding.FragmentAddNewWordBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.Resource
import com.myvocab.myvocab.util.enableSelectItemBg
import com.myvocab.myvocab.util.findNavController
import io.reactivex.disposables.CompositeDisposable
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

    private lateinit var examplesAdapter: ExamplesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_word, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(AddNewWordViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        arguments?.let {
            viewModel.initWith(AddNewWordFragmentArgs.fromBundle(it).wordToEdit)
        }

        viewModel.suggestedWord.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.LOADING -> binding.wordSuggestionContainer.visibility = View.GONE
                Resource.Status.SUCCESS -> {
                    val w = it.data!!
                    var wordText = "${w.word} [${w.transcription}] - ${w.translation}"
                    if (!w.synonyms.isNullOrEmpty())
                        wordText += w.synonyms.joinToString(", ", prefix = ", ", limit = 2)
                    binding.wordSuggestion.text = wordText

                    enableSelectItemBg(binding.wordSuggestionContainer)
                    binding.wordSuggestionContainer.visibility = View.VISIBLE

                    binding.wordSuggestionContainer.setOnClickListener {
                        viewModel.fillFieldsWithSuggestedWord()
                        binding.wordSuggestionContainer.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR -> binding.wordSuggestionContainer.visibility = View.GONE
            }
        })

        viewModel.examples.observe(viewLifecycleOwner, {
            examplesAdapter.examples = it
            examplesAdapter.notifyDataSetChanged()
        })

        examplesAdapter = ExamplesAdapter(object : ExampleItemCallback {
            override fun onDelete(pos: Int) {
                examplesAdapter.examples.removeAt(pos)
                examplesAdapter.notifyItemRemoved(pos)
                examplesAdapter.notifyItemRangeChanged(pos, examplesAdapter.itemCount - pos)
            }
        })

        binding.examplesRecycler.apply {
            adapter = examplesAdapter
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
        }

        binding.container.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.includeToolbar.toolbar.inflateMenu(R.menu.add_new_word)
        binding.includeToolbar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    if (allDataCompleted()) {
                        compositeDisposable.clear()
                        compositeDisposable.add(
                            viewModel.commitWord().subscribe({
                                findNavController().navigateUp()

                                // log adding new word
                                FirebaseAnalytics.getInstance(requireContext())
                                    .logEvent("add_new_word", Bundle().apply {
                                        putString("text", viewModel.newWord.value)
                                        putInt("length", viewModel.newWord.value?.length ?: 0)
                                    })
                            }, { e ->
                                if (e is SQLiteConstraintException) {
                                    Snackbar.make(
                                        view,
                                        "This word have already added to your vocab",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Timber.e(e)
                                    Snackbar.make(
                                        view,
                                        "Error, word wasn't added",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        )
                    }
                }
            }
            true
        }

        binding.addExample.setOnClickListener {
            if (examplesAdapter.itemCount < 5) {
                examplesAdapter.examples.add(examplesAdapter.itemCount, Word.Example())
                examplesAdapter.notifyItemInserted(examplesAdapter.itemCount - 1)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.max_examples_number, Word.MAX_EXAMPLES_NUMBER),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
                        Snackbar.make(
                            binding.container,
                            "Error, words weren't added",
                            Snackbar.LENGTH_SHORT
                        ).show()
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
            binding.translationTil.error = getString(R.string.enter_translation)
            binding.translationEdt.requestFocus()
            ifAllDataCompleted = false
        } else {
            binding.translationTil.isErrorEnabled = false
        }
        if (viewModel.newWord.value.isNullOrBlank()) {
            binding.newWordTil.error = getString(R.string.enter_new_word)
            binding.newWordEdt.requestFocus()
            ifAllDataCompleted = false
        } else {
            binding.newWordTil.isErrorEnabled = false
        }
        return ifAllDataCompleted
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    class ExamplesAdapter(val callback: ExampleItemCallback) :
        RecyclerView.Adapter<ExamplesAdapter.ExampleVH>() {

        var examples = mutableListOf<Word.Example>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ExampleVH(
                AddWordExampleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                ExampleEditTextListener { example, s -> example.copy(text = s) },
                ExampleEditTextListener { example, s -> example.copy(translation = s) }
            )

        override fun onBindViewHolder(holder: ExampleVH, position: Int) {
            holder.bind(examples[position], callback)
        }

        override fun getItemCount() = examples.size

        inner class ExampleVH(
            private var binding: AddWordExampleBinding,
            private val textListener: ExampleEditTextListener,
            private val translationListener: ExampleEditTextListener
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                with(binding) {
                    exampleEdt.addTextChangedListener(textListener)
                    exampleTranslationEdt.addTextChangedListener(translationListener)
                }
            }

            fun bind(example: Word.Example, callback: ExampleItemCallback) {
                textListener.position = bindingAdapterPosition
                translationListener.position = bindingAdapterPosition
                with(binding) {
                    exampleEdt.setText(example.text)
                    exampleTranslationEdt.setText(example.translation)

                    exampleTil.hint =
                        root.context.getString(R.string.example, bindingAdapterPosition + 1)
                    exampleTranslationTil.hint =
                        root.context.getString(
                            R.string.example_translation,
                            bindingAdapterPosition + 1
                        )

                    deleteBtn.setOnClickListener {
                        callback.onDelete(bindingAdapterPosition)
                    }
                }
            }
        }

        inner class ExampleEditTextListener(val transform: (e: Word.Example, s: String) -> Word.Example) :
            TextWatcher {
            var position = 0

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                examples[position] = transform(examples[position], s.toString())
            }
        }
    }

    interface ExampleItemCallback {
        fun onDelete(pos: Int)
    }

}
