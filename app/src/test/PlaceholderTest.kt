import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
* Tests for the placeholder class methods.
 */
internal class PlaceholderTest {
    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
    }

    /**
     * Test suit for the expand method.
     * Partition the input as follows
     * 1. # of time the placeholder is found: 0, 1, > 1
     * 2. range length: 0, 1, > 1
     */
    /// Cover:
    /// 1. #placeholder occurrences: 0
    /// 2. range length: 10
    @Test
    fun expandNoOccurrenceRange10() {
        val placeholder = Placeholder("XXX", 0, 9, 1, 2, '-')
        val result = placeholder.expand("no-placeholder")
        assertEquals(0, result.size)
    }

    /// Cover:
    /// 1. #placeholder occurrences: 1
    /// 2. range length: 1
    @Test
    fun expandSingleOccurrenceOneOutput() {
        val placeholder = Placeholder("#N#", 0, 0, 1, 2, '@')
        val result = placeholder.expand("init-#N#")
        assertEquals(setOf("init-@0"), result)
    }

    /// Cover:
    /// 1. #placeholder occurrences: 1
    /// 2. range length: 3
    @Test
    fun expandSingleOccurrenceThreeOutput() {
        val placeholder = Placeholder("N", 5, 9, 2, 2, '-')
        val result = placeholder.expand("initN")
        assertEquals(setOf("init-5", "init-7", "init-9"), result)
    }

    /// Cover:
    /// 1. #placeholder occurrences: 2
    /// 2. range length: 2
    @Test
    fun expandDoubleOccurrenceRangeTwo() {
        val placeholder = Placeholder("%", 11, 12, 1, 5, '.')
        val result = placeholder.expand("init%-%")
        assertEquals(setOf("init...11-...11", "init...12-...12"), result)
    }

}