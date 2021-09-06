package slimeknights.tconstruct.library.recipe.modifiers.spilling;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffectLoader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling a recipe cache for fluid spilling recipes, since any given fluid has one recipe
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpillingRecipeLookup {
  private static final Map<Fluid,SpillingRecipe> CACHE = new HashMap<>();
  /** Map of registered spilling effect loaders */
  private static final BiMap<ResourceLocation,ISpillingEffectLoader<?>> EFFECT_LOADERS = HashBiMap.create();
  static {
    RecipeCacheInvalidator.addReloadListener(client -> CACHE.clear());
  }

  /**
   * Gets the recipe for the given fluid
   * @param manager  Recipe manager
   * @param fluid    Fluid
   * @return  Recipe, or null if no recipe for this type
   */
  @Nullable
  public static SpillingRecipe findRecipe(RecipeManager manager, Fluid fluid) {
    if (CACHE.containsKey(fluid)) {
      return CACHE.get(fluid);
    }

    // find all severing recipes for the entity
    for (SpillingRecipe recipe : RecipeHelper.getRecipes(manager, RecipeTypes.SPILLING, SpillingRecipe.class)) {
      if (recipe.matches(fluid)) {
        CACHE.put(fluid, recipe);
        return recipe;
      }
    }
    // cache null if nothing
    CACHE.put(fluid, null);
    return null;
  }


  /* Spilling effects */

  /** Registers a spilling effect loader */
  public static void registerEffect(ResourceLocation name, ISpillingEffectLoader<?> loader) {
    EFFECT_LOADERS.putIfAbsent(name, loader);
  }

  /** Deserializes an effect from JSON */
  public static ISpillingEffect deserializeEffect(JsonObject json) {
    ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
    ISpillingEffectLoader<?> loader = EFFECT_LOADERS.get(type);
    if (loader == null) {
      throw new JsonSyntaxException("Unknown spilling effect type " + type);
    }
    return loader.deserialize(json);
  }

  /** Reads an effect from the packet buffer */
  public static ISpillingEffect readEffect(PacketBuffer buffer) {
    ResourceLocation type = buffer.readResourceLocation();
    ISpillingEffectLoader<?> loader = EFFECT_LOADERS.get(type);
    if (loader == null) {
      throw new DecoderException("Unknown spilling effect type" + type);
    }
    return loader.read(buffer);
  }

  /** Serializes the given effect to JSON */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static JsonObject serializeEffect(ISpillingEffect effect) {
    ISpillingEffectLoader loader = effect.getLoader();
    JsonObject json = new JsonObject();
    ResourceLocation id = EFFECT_LOADERS.inverse().get(loader);
    if (id == null) {
      throw new IllegalStateException("Attempted to serialize an unregistered loader " + loader);
    }
    json.addProperty("type", id.toString());
    loader.serialize(effect, json);
    return json;
  }

  /** Writes the given effect to the packet buffer */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void writeEffect(ISpillingEffect effect, PacketBuffer buffer) {
    ISpillingEffectLoader loader = effect.getLoader();
    ResourceLocation id = EFFECT_LOADERS.inverse().get(loader);
    if (id == null) {
      throw new EncoderException("Attempted to serialize an unregistered loader " + loader);
    }
    buffer.writeResourceLocation(id);
    loader.write(effect, buffer);
  }
}
