package ru.octol1ttle.flightassistant.api.util

class LimitedFIFOQueue<T>(private val maxSize: Int): Collection<T> {
    private var backingList: MutableList<T> = ArrayList()

    fun add(element: T) {
        backingList.add(0, element)
        if (backingList.size > maxSize) {
            backingList.removeFirst()
        }
    }

    fun clear() {
        backingList.clear()
    }

    override val size: Int
        get() = backingList.size

    override fun isEmpty(): Boolean {
        return backingList.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return backingList.iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return backingList.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return backingList.contains(element)
    }
}
