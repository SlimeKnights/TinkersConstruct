package slimeknights.tconstruct.library.modifiers.modules.behavior;

import com.google.gson.JsonObject;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EffectiveLevelModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.INamespacedNBTView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module that makes a modifier incremental.
 * @param key             Persistent data key containing the incremental data. If null, uses the modifier ID.
 *                        Presently, changing this makes it incompatible with the incremental modifier recipe, this is added for future proofing.
 * @param neededPerLevel  If zero, modifier is incremental with no max set and will fetch if from the recipe.
 *                        If greater than zero, modifier will have a fixed max.
 */
public record IncrementalModule(@Nullable ResourceLocation key, int neededPerLevel) implements EffectiveLevelModifierHook, DisplayNameModifierHook, ModifierRemovalHook, ModifierModule, ModuleWithKey {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.EFFECTIVE_LEVEL, TinkerHooks.DISPLAY_NAME, TinkerHooks.REMOVE);

  /** Recipe controlled incremental modifier with no extra settings */
  public static IncrementalModule RECIPE_CONTROLLED = new IncrementalModule(null, 0);

  /** Just saving a bit of memory as most things use the recipe controlled constant */
  public static IncrementalModule create(@Nullable ResourceLocation key, int neededPerLevel) {
    if (key == null && neededPerLevel == 0) {
      return RECIPE_CONTROLLED;
    }
    return new IncrementalModule(key, neededPerLevel);
  }

  /** Gets the number needed per level */
  private int getNeededPerLevel(ResourceLocation key) {
    if (neededPerLevel > 0) {
      return neededPerLevel;
    }
    return ModifierRecipeLookup.getNeededPerLevel(key);
  }

  /** Gets the amount presently on the tool */
  private int getAmount(IToolContext tool, ResourceLocation key, int neededPerLevel) {
    INamespacedNBTView data = tool.getPersistentData();
    if (data.contains(key, Tag.TAG_ANY_NUMERIC)) {
      return data.getInt(key);
    }
    return neededPerLevel;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, Modifier modifier, int level, Component name) {
    ResourceLocation key = getKey(modifier);
    int neededPerLevel = getNeededPerLevel(key);
    if (neededPerLevel > 0) {
      return IncrementalModifier.addAmountToName(getAmount(tool, key, neededPerLevel), neededPerLevel, name);
    }
    return name;
  }

  @Override
  public float getEffectiveLevel(IToolContext tool, Modifier modifier, float level) {
    if (level <= 0) {
      return 0;
    }
    ResourceLocation key = getKey(modifier);
    int neededPerLevel = getNeededPerLevel(key);
    if (neededPerLevel > 0) {
      return IncrementalModifier.scaleLevel(level, getAmount(tool, key, neededPerLevel), neededPerLevel);
    }
    return level;
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(modifier.getId());
    return null;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<IncrementalModule> LOADER = new IGenericLoader<>() {
    @Override
    public IncrementalModule deserialize(JsonObject json) {
      ResourceLocation key = ModuleWithKey.parseKey(json);
      int neededPerLevel = JsonUtils.getIntMin(json, "needed_per_level", 0);
      return IncrementalModule.create(key, neededPerLevel);
    }

    @Override
    public void serialize(IncrementalModule object, JsonObject json) {
      if (object.key != null) {
        json.addProperty("key", object.key.toString());
      }
      if (object.neededPerLevel > 0) {
        json.addProperty("needed_per_level", object.neededPerLevel);
      }
    }

    @Override
    public IncrementalModule fromNetwork(FriendlyByteBuf buffer) {
      ResourceLocation key = ModuleWithKey.fromNetwork(buffer);
      int neededPerLevel = buffer.readVarInt();
      return IncrementalModule.create(key, neededPerLevel);
    }

    @Override
    public void toNetwork(IncrementalModule object, FriendlyByteBuf buffer) {
      ModuleWithKey.toNetwork(object.key, buffer);
      buffer.writeVarInt(object.neededPerLevel);
    }
  };
}
