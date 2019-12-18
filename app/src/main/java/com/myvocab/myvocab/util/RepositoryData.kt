package com.myvocab.myvocab.util

data class RepositoryData<T> (
        val data: T,
        val source: Source
)

enum class Source { LOCAL, REMOTE }