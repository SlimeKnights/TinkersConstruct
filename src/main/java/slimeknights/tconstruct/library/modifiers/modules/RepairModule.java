package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.RepairFactorModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for multiplying tool repair */
public record RepairModule(float flat, float leveling) implements RepairFactorModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.REPAIR_FACTOR);

  @Override
  public float getRepairFactor(IToolStackView tool, ModifierEntry entry, float factor) {
    factor *= (1 + (entry.getEffectiveLevel(tool) * leveling) + flat);
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
      float flat = GsonHelper.getAsFloat(json, "flat", 0);
      float leveling = GsonHelper.getAsFloat(json, "leveling", 0);
      return new RepairModule(flat, leveling);
    }

    @Override
    public void serialize(RepairModule object, JsonObject json) {
      if (object.flat != 0) {
        json.addProperty("flat", object.flat);
      }
      if (object.leveling != 0) {
        json.addProperty("leveling", object.leveling);
      }
    }

    @Override
    public RepairModule fromNetwork(FriendlyByteBuf buffer) {
      float flat = buffer.readFloat();
      float leveling = buffer.readFloat();
      return new RepairModule(flat, leveling);
    }

    @Override
    public void toNetwork(RepairModule object, FriendlyByteBuf buffer) {
      buffer.writeFloat(object.flat);
      buffer.writeFloat(object.leveling);
    }
  };


  /* Helpers */

  public static RepairModule flat(float value) {
    return new RepairModule(value, 0);
  }

  public static RepairModule leveling(float leveling) {
    return new RepairModule(0, leveling);
  }
}
