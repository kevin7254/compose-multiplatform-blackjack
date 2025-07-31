package domain.model

enum class Rank(val value: UByte, val displayValue: Int) {
    ACE(1u, 1),
    TWO(2u, 2),
    THREE(3u, 3),
    FOUR(4u, 4),
    FIVE(5u, 5),
    SIX(6u, 6),
    SEVEN(7u, 7),
    EIGHT(8u, 8),
    NINE(9u, 9),
    TEN(10u, 10),
    JACK(10u, 11),
    QUEEN(10u, 12),
    KING(10u, 13);
}

