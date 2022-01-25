package com.myvocab.domain.service

interface ReminderScheduler {
    fun schedule()
    fun schedule(remindingTime: Long)
    fun scheduleIfEnabled()
    fun scheduleIfEnabled(remindingTime: Long)
    fun cancel()
    fun isReminderEnabled(): Boolean
    fun isRemindOnlyWordsToLearn(): Boolean
    fun setRemindOnlyWordsToLearn(state: Boolean)
    fun getRemindingTime(): Long
}