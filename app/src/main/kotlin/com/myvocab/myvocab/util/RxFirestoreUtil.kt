package com.myvocab.myvocab.util

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Maybe

fun Query.getMaybe(): Maybe<QuerySnapshot?> {
    return Maybe.create { emitter ->
        get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result?.documents.isNullOrEmpty()) {
                    if (!emitter.isDisposed)
                        emitter.onError(RuntimeException("Failed to load", task.exception))
                } else {
                    emitter.onSuccess(task.result!!)
                }
            } else {
                if (!emitter.isDisposed)
                    emitter.onError(task.exception!!)
            }
        }
    }
}