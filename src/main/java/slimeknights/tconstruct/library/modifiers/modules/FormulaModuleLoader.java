package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.modules.FormulaModuleLoader.FormulaModule;

import java.util.function.BiFunction;

/** Generic loader for a modifier module that accepts a modifier formula and a module condition */
public record FormulaModuleLoader<T extends FormulaModule & ModifierModule>(
  BiFunction<ModifierFormula, ModifierModuleCondition, T> constructor,
  FallbackFormula fallbackFormula,
  String... variables
) implements IGenericLoader<T> {
  @Override
  public T deserialize(JsonObject json) {
    return constructor.apply(ModifierFormula.deserialize(json, variables, fallbackFormula), ModifierModuleCondition.deserializeFrom(json));
  }

  @Override
  public void serialize(T object, JsonObject json) {
    object.condition().serializeInto(json);
    object.formula().serialize(json, variables);
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(ModifierFormula.fromNetwork(buffer, variables.length, fallbackFormula), ModifierModuleCondition.fromNetwork(buffer));
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    object.formula().toNetwork(buffer);
    object.condition().toNetwork(buffer);
  }

  /** Interface to use this loader */
  public interface FormulaModule {
    /** Gets the formula for this module */
    ModifierFormula formula();
    /** Gets the conditon for this module */
    ModifierModuleCondition condition();
  }

  /** Creates a builder instance */
  public Builder builder() {
    return new Builder();
  }

  /** Builder for this module */
  public class Builder extends ModifierFormula.Builder<Builder,T> {
    private Builder() {
      super(variables);
    }

    @Override
    protected T build(ModifierFormula formula) {
      return constructor.apply(formula, condition);
    }
  }
}
