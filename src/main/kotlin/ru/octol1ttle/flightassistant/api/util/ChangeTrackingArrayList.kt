package ru.octol1ttle.flightassistant.api.util

class ChangeTrackingArrayList<E> {
    private var backingList: ArrayList<E> = ArrayList()
    private var staleList: ArrayList<E> = ArrayList()

    fun add(element: E): Boolean {
        return backingList.add(element)
    }

    fun isEmpty(): Boolean {
        return backingList.isEmpty()
    }

    fun startTracking() {
        staleList = backingList
        backingList = ArrayList()
    }

    fun hasNewElements(): Boolean {
        if (backingList.size > staleList.size) {
            return true
        }

        return backingList.any { !staleList.contains(it) }
    }
}
