package ru.octol1ttle.flightassistant.api.util

/**
 * A class which can track changes to a list. Useful when a list is fully reconstructed each time it is updated.
 * To use, call [startTracking], which will clear this list. Call [hasNewElements] to check if the list has changed after adding elements to it
 */
class ChangeTrackingArrayList<E> : Collection<E> {
    private var backingList: ArrayList<E> = ArrayList()
    private var staleList: ArrayList<E> = ArrayList()

    fun add(element: E): Boolean {
        return backingList.add(element)
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

    override val size: Int
        get() = backingList.size

    override fun isEmpty(): Boolean {
        return backingList.isEmpty()
    }

    override fun iterator(): Iterator<E> {
        return backingList.iterator()
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return backingList.containsAll(elements)
    }

    override fun contains(element: E): Boolean {
        return backingList.contains(element)
    }
}
