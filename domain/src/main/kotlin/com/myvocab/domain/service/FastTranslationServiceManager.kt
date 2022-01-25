package com.myvocab.domain.service

interface FastTranslationServiceManager {
    fun start()
    fun startIfEnabled()
    fun isTranslationEnabled(): Boolean
    fun cancel()
}