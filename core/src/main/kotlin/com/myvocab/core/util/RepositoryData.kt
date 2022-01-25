package com.myvocab.core.util

data class RepositoryData<T> (
        val data: T,
        val source: Source
)

enum class Source { LOCAL, REMOTE }