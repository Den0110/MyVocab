package com.myvocab.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelProviderFactory
@Inject
constructor(
        private val creators: Map<Class<out ViewModel>,
        @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) { // if the viewmodel has not been created
            for ((key, value) in creators) { // loop through the allowable keys (aka allowed classes with the @ViewModelKey)
                if (modelClass.isAssignableFrom(key)) { // if it's allowed, set the Provider<ViewModel>
                    creator = value
                    break
                }
            }
        }

        requireNotNull(creator) { "unknown model class $modelClass" } // if this is not one of the allowed keys, throw exception

        // return the Provider
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

}
