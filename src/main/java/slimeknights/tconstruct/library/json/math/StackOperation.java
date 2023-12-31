package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.floats.FloatStack;
import net.minecraft.network.FriendlyByteBuf;

/** Interface representing a simple operation performed on the stack */
public interface StackOperation {
  /** Performs this operand */
  void perform(FloatStack stack, float[] variables);

  /** Serializes this operation to JSON */
  JsonPrimitive serialize(String[] variableNames);

  /** Reads an operation from JSON */
  static StackOperation deserialize(JsonPrimitive element, String[] variableNames) {
    // strings are either binary operators or are variables
    if (element.isString()) {
      String str = element.getAsString();
      if (str.length() == 1) {
        return BinaryOperator.deserialize(str.charAt(0));
      }
      for (int i = 0; i < variableNames.length; i++) {
        if (str.equals(variableNames[i])) {
          return new PushVariableOperation(i);
        }
      }
      throw new JsonSyntaxException("Unknown variable '" + str + "'");
    }
    // numbers are constants
    if (element.isNumber()) {
      return new PushConstantOperation(element.getAsFloat());
    }
    throw new JsonSyntaxException("Expected stack operation to be a string or number, got boolean");
  }

  /** Serializes this operation to the network */
  void toNetwork(FriendlyByteBuf buffer);

  /** Reads an operation from the network */
  static StackOperation fromNetwork(FriendlyByteBuf buffer) {
    int type = buffer.readVarInt();
    if (type == BinaryOperator.VALUE_INDEX) {
      return new PushConstantOperation(buffer.readFloat());
    }
    if (type == BinaryOperator.VARIABLE_INDEX) {
      return new PushVariableOperation(buffer.readVarInt());
    }
    return BinaryOperator.values()[type];
  }
}
