package org.perudevteam.type.base;

/**
 * Out of the box Operator Types for Base Types.
 * Again, this is just preset code for out of the box use.
 */
public enum BaseOperator {
    // Arithmetic Operators.
    PLUS,       // (Plus also used for string and list concatenation)
    MINUS,
    TIMES,
    OVER,
    POWER,      // i.e. Exponents.

    // Boolean Operators.
    AND,
    OR,
    NOT,

    // Comparison Operators.
    LT,
    GT,
    LT_EQ,
    GT_EQ,
    EQ,

    // Function Operators.
    COMPOSE,
    ANDTHEN
}
