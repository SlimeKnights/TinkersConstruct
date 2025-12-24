package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.floats.FloatStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

/** Represents 2 argument stack operations */
@SuppressWarnings("SuspiciousNameCombination")
@RequiredArgsConstructor
public enum PostFixOperator implements StackOperation {
  // basic math
  ADD("+", Float::sum),
  SUBTRACT("-", (left, right) -> left - right),
  SUBTRACT_FLIPPED("!-", (left, right) -> right - left),
  MULTIPLY("*", (left, right) -> left * right),
  NEGATE("negate") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      stack.push(-stack.popFloat());
    }
  },
  DIVIDE("/", (left, right) -> {
    if (right == 0) {
      return 0;
    }
    return left / right;
  }),
  DIVIDE_FLIPPED("!/", (left, right) -> {
    if (left == 0) {
      return 0;
    }
    return right / left;
  }),
  POWER("^", (left, right) -> (float)Math.pow(left, right)),
  POWER_FLIPPED("!^", (left, right) -> (float)Math.pow(right, left)),

  // logical operators
  // for all of them, if the result is true you get 1, otherwise 0
  EQUAL             ("==", (left, right) -> left == right ? 1 : 0),
  NOT_EQUAL         ("!=", (left, right) -> left != right ? 1 : 0),
  LESS_THAN         ("<",  (left, right) -> left <  right ? 1 : 0),
  LESS_THAN_EQUAL   ("<=", (left, right) -> left <= right ? 1 : 0),
  GREATER_THAN      (">",  (left, right) -> left >  right ? 1 : 0),
  GREATER_THAN_EQUAL(">=", (left, right) -> left >= right ? 1 : 0),
  EQUAL_EPS    ("~~", (left, right) -> Mth.equal(left, right) ? 1 : 0),
  NOT_EQUAL_EPS("!~", (left, right) -> Mth.equal(left, right) ? 0 : 1),

  // piecewise
  MIN("min", Math::min),
  MAX("max", Math::max),
  /** Makes the top value on the stack 0 if its negative */
  NON_NEGATIVE("non-negative") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      float value = stack.topFloat();
      if (value < 0) {
        stack.popFloat();
        stack.push(0);
      }
    }
  },
  /** Clamps the value between 0 and 1 */
  PERCENT_CLAMP("percent_clamp") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      // could do a 1 liner, but not pushing/popping when unchanged seems more efficient
      float value = stack.topFloat();
      if (value < 0) {
        stack.popFloat();
        stack.push(0);
      } else if (value > 1) {
        stack.popFloat();
        stack.push(1);
      }
    }
  },
  ABS("abs") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      float value = stack.topFloat();
      if (value < 0) {
        stack.popFloat();
        stack.push(-value);
      }
    }
  },
  FLOOR("floor") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      stack.push(Mth.floor(stack.popFloat()));
    }
  },
  CEIL("ceil") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      stack.push(Mth.ceil(stack.popFloat()));
    }
  },
  SQRT("sqrt") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      stack.push(Mth.sqrt(stack.popFloat()));
    }
  },

  // stack operations
  /** Swaps the top two elements */
  SWAP("swap") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      float first = stack.popFloat();
      float second = stack.popFloat();
      stack.push(first);
      stack.push(second);
    }
  },
  /** Copies the top element on the stack */
  DUPLICATE("duplicate") {
    @Override
    public void perform(FloatStack stack, float[] variables) {
      stack.push(stack.topFloat());
    }
  };

  /** Index used for serializing value to the network */
  public static final int VALUE_INDEX = values().length;
  /** Index used for serializing a variable to the network */
  public static final int VARIABLE_INDEX = VALUE_INDEX + 1;

  /** Name of this operator when serialized into JSON */
  private final String serialized;
  /** Binary function to run, used for most operators */
  private final BinaryOperator binary;

  PostFixOperator(String serialized) {
    this(serialized, BinaryOperator.ZERO);
  }

  @Override
  public void perform(FloatStack stack, float[] variables) {
    // this may throw, but that is okay as we will run this formula during parsing to make sure its valid
    // the way formulas are setup, if it does not throw during parsing, it cannot throw ever
    float right = stack.popFloat();
    float left = stack.popFloat();
    stack.push(binary.apply(left, right));
  }


  /* JSON and network */

  /** Deserializes the operator from a character */
  public static PostFixOperator deserialize(String name) {
    for (PostFixOperator operator : PostFixOperator.values()) {
      if (operator.serialized.equals(name)) {
        return operator;
      }
    }
    throw new JsonSyntaxException("Unknown post fix operator '" + name + "'");
  }

  @Override
  public JsonPrimitive serialize(String[] variableNames) {
    return new JsonPrimitive(serialized);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer) {
    // comment on buffer internals: the indices of this enum and StackNetworkType match up until divide,
    // so writing our ordinal allows us to read an ordinal for the other enum
    buffer.writeEnum(this);
  }

  /** Interface for common operators */
  private interface BinaryOperator {
    BinaryOperator ZERO = (left, right) -> 0;

    /** Applies this operator to the given values */
    float apply(float left, float right);
  }
}
