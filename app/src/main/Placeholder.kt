/**
 * Immutable ADT for representing a placeholder and the values that it should generate.
 * @param placeholder a string that should be substituted
 * @param start initial value that the placeholder should get
 * @param end final value that the placeholder might get
 * @param step difference btw two consecutive values to be assigned to the placeholder
 * @param paddingSize minimal length of the placeholder after being substituted with a numeric value
 * @param paddingChar a char by which the placeholder is to be padded
 */
class Placeholder(val placeholder: String, val start: Int, val end: Int, val step: Int, val paddingSize: Int, val paddingChar: Char) {
    /**
     * This ADT is completely immutable: all fields are of primitive types and declared as constants.
     *
     * Representation consistency:
     * 1. the placeholder must be a non-empty string
     * 2. if start > end, then step must be positive
     * 3. if start < end, then step must be negative
     * 4. paddingSize must be non-negative integer
     */

    private fun checkRep(){
        if (placeholder.isEmpty()) throw Exception("Placeholder can not be empty")
        if (start > end  && step <= 0) throw Exception("Step value must be positive")
        if (start < end  && step >= 0) throw Exception("Step value must be negative")
        if (paddingSize < 0) throw Exception("Padding size must be non-negative")
    }

    init {
        checkRep()
    }
}