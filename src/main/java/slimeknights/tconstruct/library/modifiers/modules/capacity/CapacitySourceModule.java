package slimeknights.tconstruct.library.modifiers.modules.capacity;

import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Helper for modules that provide capacity to be able to specify a target */
public interface CapacitySourceModule {
  /** Owner field instance */
  LoadableField<ModifierId,CapacitySourceModule> OWNER_FIELD = ModifierId.PARSER.nullableField("owner", CapacitySourceModule::owner);

  /** Bar owner. If null, uses the modifier itself */
  @Nullable
  ModifierId owner();

  /** Gets the bar for this module */
  default ModifierEntry barModifier(IToolStackView tool, ModifierEntry entry) {
    ModifierId owner = owner();
    if (owner != null) {
      return tool.getModifier(owner);
    }
    return entry;
  }

  /**
   * Applies the given amount and multiplier
   * @param tool       Tool to apply
   * @param modifier   Bar modifier instance
   * @param amount     Amount from the source
   * @param grant      Multiplier for the amount. If positive, adds the amount. If negative, removes it. If 0, resets the bar to 0.
   */
  static void apply(IToolStackView tool, ModifierEntry modifier, int amount, int grant) {
    CapacityBarHook bar = modifier.getHook(ModifierHooks.CAPACITY_BAR);
    if (grant == 0) {
      bar.setAmount(tool, modifier, 0);
    } else if (grant > 0) {
      bar.addAmount(tool, modifier, amount * grant);
    } else {
      bar.removeAmount(tool, modifier, -amount * grant);
    }
  }

  /** Builder handling the owner */
  @Setter
  @Accessors(fluent = true)
  class Builder<B extends Builder<B>> extends ModuleBuilder.Stack<B> {
    @Nullable
    protected ModifierId owner = null;
  }
}
