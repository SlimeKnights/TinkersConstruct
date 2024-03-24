package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonObject;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;

import java.util.function.BiFunction;

/** Loadable for a modifier formula */
public record FormulaLoadable(FallbackFormula fallback, String... variables) implements RecordLoadable<ModifierFormula> {
  @Override
  public ModifierFormula deserialize(JsonObject json, TypedMap<Object> context) {
    return ModifierFormula.deserialize(json, variables, fallback);
  }

  @Override
  public void serialize(ModifierFormula object, JsonObject json) {
    object.serialize(json, variables);
  }

  @Override
  public ModifierFormula fromNetwork(FriendlyByteBuf buffer, TypedMap<Object> context) throws DecoderException {
    return ModifierFormula.fromNetwork(buffer, variables.length, fallback);
  }

  @Override
  public void toNetwork(ModifierFormula object, FriendlyByteBuf buffer) throws EncoderException {
    object.toNetwork(buffer);
  }

  /** Creates a builder instance */
  public <T> Builder<T> builder(BiFunction<ModifierFormula,ModifierModuleCondition,T> constructor) {
    return new Builder<>(constructor, variables);
  }

  /** Builder for this module */
  public static class Builder<T> extends ModifierFormula.Builder<Builder<T>,T> {
    private final BiFunction<ModifierFormula,ModifierModuleCondition,T> constructor;
    private Builder(BiFunction<ModifierFormula,ModifierModuleCondition,T> constructor, String[] variableNames) {
      super(variableNames);
      this.constructor = constructor;
    }

    @Override
    protected T build(ModifierFormula formula) {
      return constructor.apply(formula, condition);
    }
  }
}
