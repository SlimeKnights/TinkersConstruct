package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.floats.FloatStack;
import net.minecraft.network.FriendlyByteBuf;

/** Stack operation which pushes a constant float value */
record PushConstantOperation(float value) implements StackOperation {
  @Override
  public void perform(FloatStack stack, float[] variables) {
    stack.push(value);
  }

  @Override
  public JsonPrimitive serialize(String[] variableNames) {
    return new JsonPrimitive(value);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeVarInt(PostFixOperator.VALUE_INDEX);
    buffer.writeFloat(value);
  }
}
