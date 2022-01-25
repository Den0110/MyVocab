package com.myvocab.domain.common

sealed class Resource<out T>(open val data: T?, open val error: Throwable?) {

    class Success<T>(override val data: T) : Resource<T>(data, null)

    class Error<T>(override val error: Throwable, data: T? = null) : Resource<T>(data, error)

    class Loading<T>(data: T? = null) : Resource<T>(data, null)

    fun <T> withNewData(newData: T): Resource<T> = when (this) {
        is Loading -> Loading(newData)
        is Success -> Success(newData)
        is Error -> Error(error)
    }

}

fun <E> Resource<MutableList<E>?>.withUpdatedList(block: MutableList<E>.() -> Unit): Resource<MutableList<E>?> {
    return data?.toMutableList()?.let {
        block(it)
        withNewData(it)
    } ?: this
}