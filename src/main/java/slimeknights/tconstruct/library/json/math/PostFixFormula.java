package slimeknights.tconstruct.library.json.math;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.floats.AbstractFloatList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.List;

/** Performs a math formula using a post fix calculator */
public record PostFixFormula(List<StackOperation> operations, int numArguments) implements ModifierFormula {
  @Override
  public float apply(float... values) {
    // must have the right number of values to evaluate
    if (values.length != numArguments) {
      throw new IllegalArgumentException("Expected " + numArguments + " arguments, but received " + values.length);
    }
    AbstractFloatList stack = new FloatArrayList(5);
    for (StackOperation operation : operations) {
      operation.perform(stack, values);
    }
    if (stack.size() != 1) {
      throw new IllegalStateException("Expected 1 value on the stack after evaluation, received " + stack.size());
    }
    return stack.popFloat();
  }

  @Override
  public float processLevel(ModifierEntry modifier) {
    return modifier.getEffectiveLevel();
  }

  /**
   * Runs the formula with dummy arguments to ensure it is computationally valid
   * @throws RuntimeException  if something is invalid in the formula
   */
  public void validateFormula() {
    apply(new float[numArguments]);
  }

  /** Deserializes a formula from JSON */
  public static PostFixFormula deserialize(JsonObject json, String[] variableNames) {
    // TODO: string formulas using Shunting yard algorithm
    return new PostFixFormula(JsonHelper.parseList(json, "formula", (element, key) -> {
      if (element.isJsonPrimitive()) {
        return StackOperation.deserialize(element.getAsJsonPrimitive(), variableNames);
      }
      throw new JsonSyntaxException("Expected " + key + " to be a string or number, was " + GsonHelper.getType(element));
    }), variableNames.length);
  }

  /** Serializes this object to JSON */
  @Override
  public JsonObject serialize(JsonObject json, String[] variableNames) {
    JsonArray array = new JsonArray();
    for (StackOperation operation : operations) {
      array.add(operation.serialize(variableNames));
    }
    json.add("formula", array);
    return json;
  }

  /** Reads a formula from the network */
  public static PostFixFormula fromNetwork(FriendlyByteBuf buffer, int numArguments) {
    return fromNetwork(buffer, buffer.readShort(), numArguments);
  }

  /** Common logic between {@link #fromNetwork(FriendlyByteBuf, int)} and {@link ModifierFormula#fromNetwork(FriendlyByteBuf, int, FallbackFormula)} */
  static PostFixFormula fromNetwork(FriendlyByteBuf buffer, short size, int numArguments) {
    ImmutableList.Builder<StackOperation> builder = ImmutableList.builder();
    for (int i = 0; i < size; i++) {
      builder.add(StackOperation.fromNetwork(buffer));
    }
    return new PostFixFormula(builder.build(), numArguments);
  }

  /** Writes this formula to the network */
  @Override
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeShort(operations.size());
    for (StackOperation operation : operations) {
      operation.toNetwork(buffer);
    }
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder<?> builder(String[] variableNames) {
    return new Builder<>(variableNames);
  }

  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Builder<T extends Builder<T>> {
    private final String[] variableNames;
    private final ImmutableList.Builder<StackOperation> operations = ImmutableList.builder();

    /** Adds the given operation to the builder */
    @SuppressWarnings("unchecked")
    public T operation(StackOperation operation) {
      this.operations.add(operation);
      return (T) this;
    }

    /** Pushes a constant value into the formula */
    public T constant(float value) {
      return operation(new PushConstantOperation(value));
    }

    /** Pushes a variable value into the formula */
    public T variable(int index) {
      if (index < 0 || index >= variableNames.length) {
        throw new IllegalArgumentException("Invalid variable index " + index);
      }
      return operation(new PushVariableOperation(index));
    }

    /** Pushes an add operation into the builder */
    public T add() {
      return operation(PostFixOperator.ADD);
    }

    /** Pushes a subtract operation into the builder */
    public T subtract() {
      return operation(PostFixOperator.SUBTRACT);
    }

    /** Pushes a subtract flipped operation into the builder */
    public T subtractFlipped() {
      return operation(PostFixOperator.SUBTRACT_FLIPPED);
    }

    /** Pushes a multiply operation into the builder */
    public T multiply() {
      return operation(PostFixOperator.MULTIPLY);
    }

    /** Pushes a negate operation into the builder */
    public T negate() {
      return operation(PostFixOperator.NEGATE);
    }

    /** Pushes a divide operation into the builder */
    public T divide() {
      return operation(PostFixOperator.DIVIDE);
    }

    /** Pushes a divide flipped operation into the builder */
    public T divideFlipped() {
      return operation(PostFixOperator.DIVIDE_FLIPPED);
    }

    /** Pushes a power operation into the builder */
    public T power() {
      return operation(PostFixOperator.POWER);
    }

    /** Pushes a square root operation into the builder */
    public T sqrt() {
      return operation(PostFixOperator.SQRT);
    }

    /** Pushes a power flipped operation into the builder */
    public T powerFlipped() {
      return operation(PostFixOperator.POWER_FLIPPED);
    }

    /** Pushes a min operation into the builder */
    public T min() {
      return operation(PostFixOperator.MIN);
    }

    /** Pushes a max operation into the builder */
    public T max() {
      return operation(PostFixOperator.MAX);
    }

    /** Pushes a percent clamp operation into the builder */
    public T nonNegative() {
      return operation(PostFixOperator.NON_NEGATIVE);
    }

    /** Pushes a percent clamp operation into the builder */
    public T percentClamp() {
      return operation(PostFixOperator.PERCENT_CLAMP);
    }

    /** Pushes a abs operation into the builder */
    public T abs() {
      return operation(PostFixOperator.ABS);
    }

    /** Pushes a floor operation into the builder */
    public T floor() {
      return operation(PostFixOperator.FLOOR);
    }

    /** Pushes a ceil operation into the builder */
    public T ceil() {
      return operation(PostFixOperator.CEIL);
    }

    /** Pushes a swap operation into the builder */
    public T swap() {
      return operation(PostFixOperator.SWAP);
    }

    /** Pushes a duplicate operation into the builder */
    public T duplicate() {
      return operation(PostFixOperator.DUPLICATE);
    }

    // logical operators
    public T equal() {
      return operation(PostFixOperator.EQUAL);
    }
    public T notEqual() {
      return operation(PostFixOperator.NOT_EQUAL);
    }
    public T greaterThan() {
      return operation(PostFixOperator.GREATER_THAN);
    }
    public T greaterThanOrEqual() {
      return operation(PostFixOperator.GREATER_THAN_EQUAL);
    }
    public T lessThan() {
      return operation(PostFixOperator.LESS_THAN);
    }
    public T lessThanOrEqual() {
      return operation(PostFixOperator.LESS_THAN_EQUAL);
    }
    public T equalEpsilon() {
      return operation(PostFixOperator.EQUAL_EPS);
    }
    public T notEqualEpsilon() {
      return operation(PostFixOperator.EQUAL_EPS);
    }

    /** Validates and builds the formula */
    public PostFixFormula buildFormula() {
      PostFixFormula formula = new PostFixFormula(operations.build(), variableNames.length);
      formula.validateFormula();
      return formula;
    }
  }
}
