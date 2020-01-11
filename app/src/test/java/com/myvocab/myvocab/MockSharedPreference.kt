package com.myvocab.myvocab

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import java.util.*

/**
 * Mock implementation of shared preference, which just saves data in memory using map.
 */
class MockSharedPreference : SharedPreferences {

    private val preferenceMap: HashMap<String, Any?> = HashMap()
    private val preferenceEditor: MockSharedPreferenceEditor

    override fun getAll(): Map<String, *> {
        return preferenceMap
    }

    override fun getString(s: String, s1: String?): String? {
        return preferenceMap[s] as String?
    }

    override fun getStringSet(s: String, set: Set<String>?): Set<String>? {
        return preferenceMap[s] as Set<String>?
    }

    override fun getInt(s: String, i: Int): Int {
        return preferenceMap[s] as Int? ?: i
    }

    override fun getLong(s: String, l: Long): Long {
        return preferenceMap[s] as Long? ?: l
    }

    override fun getFloat(s: String, v: Float): Float {
        return preferenceMap[s] as Float? ?: v
    }

    override fun getBoolean(s: String, b: Boolean): Boolean {
        return preferenceMap[s] as Boolean? ?: b
    }

    override fun contains(s: String): Boolean {
        return preferenceMap.containsKey(s)
    }

    override fun edit(): SharedPreferences.Editor {
        return preferenceEditor
    }

    override fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) {}
    override fun unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) {}
    class MockSharedPreferenceEditor(private val preferenceMap: HashMap<String, Any?>) : SharedPreferences.Editor {
        override fun putString(s: String, s1: String?): SharedPreferences.Editor {
            preferenceMap[s] = s1
            return this
        }

        override fun putStringSet(s: String, set: Set<String>?): SharedPreferences.Editor {
            preferenceMap[s] = set
            return this
        }

        override fun putInt(s: String, i: Int): SharedPreferences.Editor {
            preferenceMap[s] = i
            return this
        }

        override fun putLong(s: String, l: Long): SharedPreferences.Editor {
            preferenceMap[s] = l
            return this
        }

        override fun putFloat(s: String, v: Float): SharedPreferences.Editor {
            preferenceMap[s] = v
            return this
        }

        override fun putBoolean(s: String, b: Boolean): SharedPreferences.Editor {
            preferenceMap[s] = b
            return this
        }

        override fun remove(s: String): SharedPreferences.Editor {
            preferenceMap.remove(s)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            preferenceMap.clear()
            return this
        }

        override fun commit(): Boolean {
            return true
        }

        override fun apply() { // Nothing to do, everything is saved in memory.
        }

    }

    init {
        preferenceEditor = MockSharedPreferenceEditor(preferenceMap)
    }
}