package slimeknights.tconstruct.library.modifiers.modules.behavior;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for multiplying tool repair */
public record RepairModule(LevelingValue amount) implements RepairFactorModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.REPAIR_FACTOR);

  @Override
  public float getRepairFactor(IToolStackView tool, ModifierEntry entry, float factor) {
    factor *= (1 + amount.compute(tool, entry));
    return factor;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<RepairModule> LOADER = new IGenericLoader<>() {
    @Override
    public RepairModule deserialize(JsonObject json) {
      return new RepairModule(LevelingValue.deserialize(json));
    }

    @Override
    public void serialize(RepairModule object, JsonObject json) {
      object.amount.serialize(json);
    }

    @Override
    public RepairModule fromNetwork(FriendlyByteBuf buffer) {
      return new RepairModule(LevelingValue.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(RepairModule object, FriendlyByteBuf buffer) {
      object.amount.toNetwork(buffer);
    }
  };


  /* Helpers */

  public static RepairModule flat(float value) {
    return new RepairModule(LevelingValue.flat(value));
  }

  public static RepairModule eachLevel(float leveling) {
    return new RepairModule(LevelingValue.eachLevel(leveling));
  }
}
