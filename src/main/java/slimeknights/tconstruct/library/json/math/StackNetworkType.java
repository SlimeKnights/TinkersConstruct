package slimeknights.tconstruct.library.json.math;

/** This enum is used for network syncing of stack operations, since we have a finite set */
enum StackNetworkType {
  // BinaryOperators
  ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER,
  // Values
  VALUE, VARIABLE;

  /** Converts this type to a binary operator, for reading from the network */
  public BinaryOperator toOperator() {
    if (this == VALUE || this == VARIABLE) {
      throw new UnsupportedOperationException("Cannot convert " + this.name() + " to a binary operator");
    }
    return BinaryOperator.values()[ordinal()];
  }
}
