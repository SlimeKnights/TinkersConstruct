package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;

import java.util.List;

/**
 * Module for a modifier to have a nested modifier as a trait.
 */
public record ModifierTraitModule(ModifierEntry modifier, boolean fixedLevel) implements ModifierTraitHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MODIFIER_TRAITS);

  public ModifierTraitModule(ModifierId id, int level, boolean fixedLevel) {
    this(new ModifierEntry(id, level), fixedLevel);
  }

  @Override
  public void addTraits(ToolRebuildContext context, ModifierEntry self, TraitBuilder builder, boolean firstEncounter) {
    if (fixedLevel) {
      // fixed levels do not need to add again if already added
      if (firstEncounter) {
        builder.addEntry(this.modifier);
      }
    } else {
      // level of the trait is based on the level of the modifier, just multiply the two
      builder.addEntry(this.modifier.withLevel(this.modifier.getLevel() * self.getLevel()));
    }
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierTraitModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ModifierTraitModule> LOADER = new IGenericLoader<>() {
    @Override
    public ModifierTraitModule deserialize(JsonObject json) {
      ModifierEntry modifier = ModifierEntry.fromJson(json);
      boolean fixedLevel = GsonHelper.getAsBoolean(json, "fixed_level");
      return new ModifierTraitModule(modifier, fixedLevel);
    }

    @Override
    public void serialize(ModifierTraitModule object, JsonObject json) {
      object.modifier.toJson(json);
      json.addProperty("fixed_level", object.fixedLevel);
    }

    @Override
    public ModifierTraitModule fromNetwork(FriendlyByteBuf buffer) {
      ModifierEntry modifier = ModifierEntry.read(buffer);
      boolean fixedLevel = buffer.readBoolean();
      return new ModifierTraitModule(modifier, fixedLevel);
    }

    @Override
    public void toNetwork(ModifierTraitModule object, FriendlyByteBuf buffer) {
      object.modifier.write(buffer);
      buffer.writeBoolean(object.fixedLevel);
    }
  };
}
