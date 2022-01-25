package com.myvocab.navigation

sealed class NavigationFlow {
    object Learning : NavigationFlow()
    object MyWords : NavigationFlow()
    object WordLists : NavigationFlow()
    object Settings : NavigationFlow()
}