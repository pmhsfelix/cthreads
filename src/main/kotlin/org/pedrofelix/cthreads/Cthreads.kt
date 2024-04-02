// To access the *non-public* Continuation API
// ONLY for learning purposes
@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package org.pedrofelix.cthreads

import jdk.internal.vm.Continuation
import jdk.internal.vm.ContinuationScope
import java.util.*

/**
 * A continuation-based thread.
 */
class CThread(
    val continuation: Continuation,
) {
    val joiners = mutableListOf<CThread>()
    var terminated: Boolean = false

    fun join() {
        if (terminated) {
            return
        }
        val currentThread = Kernel.currentThread()
        joiners.add(currentThread)
        Kernel.contextSwitch()
    }
}

/**
 * The continuation-based threading kernel.
 * As a global object to make its usage simpler.
 */
object Kernel {

    // The famous ready queue :)
    private val readyQueue = LinkedList<CThread>()

    // The currently running CThread
    private lateinit var runningThread: CThread

    // Because creating a Continuation requires a scope
    private val scope = ContinuationScope("the-scope")

    /**
     * Creates a new [CThread] in the ready state
     */
    fun createThread(runnable: Runnable): CThread {

        val cthread = CThread(
            Continuation(scope) {
                runnable.run()
                // unblock all joiners
                currentThread().joiners.forEach {
                    readyQueue.addLast(it)
                }
                currentThread().terminated = true
            }
        )
        readyQueue.addLast(cthread)
        return cthread
    }

    /**
     * Returns the running [CThread]
     */
    fun currentThread() = runningThread

    /**
     * Transitions the running [CThread] from the running state into the ready state
     */
    fun yield() {
        readyQueue.addLast(runningThread)
        contextSwitch()
    }

    fun contextSwitch() {
        // suspend current continuation
        Continuation.yield(scope)
    }

    /**
     * Schedules all threads
     */
    fun run() {
        while (true) {
            val thread = readyQueue.poll() ?: break
            runningThread = thread
            thread.continuation.run()
        }
    }
}
