import org.pedrofelix.cthreads.Kernel
import org.slf4j.LoggerFactory
import kotlin.test.Test

class ExampleTests {

    @Test
    fun first() {

        fun threadFunction(label: String) {
            repeat(5) { ix ->
                logger.info("{}: iteration {}", label, ix)
                Kernel.yield()
            }
        }

        Kernel.createThread {
            logger.info("main: creating threads")
            val threads = List(4) {
                Kernel.createThread {
                    threadFunction(it.toString())
                }
            }
            logger.info("main: waiting for threads to terminate")
            threads.forEach { it.join() }
            logger.info("main: done")
        }
        Kernel.run()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExampleTests::class.java)
    }

}