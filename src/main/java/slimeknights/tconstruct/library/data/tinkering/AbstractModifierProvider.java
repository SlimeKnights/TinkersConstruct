package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Datagen for dynamic modifiers */
@SuppressWarnings("SameParameterValue")
public abstract class AbstractModifierProvider extends GenericDataProvider {
  private final Map<ModifierId,Result> allModifiers = new HashMap<>();
  private final Map<ModifierId,Composable> composableModifiers = new HashMap<>();

  public AbstractModifierProvider(DataGenerator generator) {
    super(generator, PackType.SERVER_DATA, ModifierManager.FOLDER, ModifierManager.GSON);
  }

  /**
   * Function to add all relevant modifiers
   */
  protected abstract void addModifiers();

  /** @deprecated use {@link #buildModifier(ModifierId, ICondition, JsonRedirect...)} */
  @Deprecated
  protected void addModifier(ModifierId id, @Nullable ICondition condition, @Nullable Modifier result, JsonRedirect... redirects) {
    if (result == null && redirects.length == 0) {
      throw new IllegalArgumentException("Must have either a modifier or a redirect");
    }
    Result previous = allModifiers.putIfAbsent(id, new Result(result, condition, redirects));
    if (previous != null || composableModifiers.containsKey(id)) {
      throw new IllegalArgumentException("Duplicate modifier " + id);
    }
  }

  /** @deprecated use {@link #buildModifier(ModifierId, JsonRedirect...)} */
  @Deprecated
  protected void addModifier(ModifierId id, @Nullable Modifier result, JsonRedirect... redirects) {
    addModifier(id, null, result, redirects);
  }

  /** @deprecated use {@link #buildModifier(DynamicModifier, ICondition, JsonRedirect...)} */
  @Deprecated
  protected void addModifier(DynamicModifier<?> id, @Nullable ICondition condition, @Nullable Modifier result, JsonRedirect... redirects) {
    addModifier(id.getId(), condition, result, redirects);
  }

  /** @deprecated use {@link #buildModifier(DynamicModifier, JsonRedirect...)} */
  @Deprecated
  protected void addModifier(DynamicModifier<?> id, @Nullable Modifier result, JsonRedirect... redirects) {
    addModifier(id, null, result, redirects);
  }


  /* Composable helpers */

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(ModifierId id, @Nullable ICondition condition, JsonRedirect... redirects) {
    ComposableModifier.Builder builder = ComposableModifier.builder();
    Composable previous = composableModifiers.putIfAbsent(id, new Composable(builder, condition, redirects));
    if (previous != null || allModifiers.containsKey(id)) {
      throw new IllegalArgumentException("Duplicate modifier " + id);
    }
    return builder;
  }

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(ModifierId id, JsonRedirect... redirects) {
    return buildModifier(id, null, redirects);
  }

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(DynamicModifier<?> modifier, @Nullable ICondition condition, JsonRedirect... redirects) {
    return buildModifier(modifier.getId(), condition, redirects);
  }

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(DynamicModifier<?> modifier, JsonRedirect... redirects) {
    return buildModifier(modifier, null, redirects);
  }


  /* Redirect helpers */

  /** Adds a modifier redirect */
  protected void addRedirect(ModifierId id, JsonRedirect... redirects) {
    addModifier(id, null, null, redirects);
  }

  /** Makes a conditional redirect to the given ID */
  protected JsonRedirect conditionalRedirect(ModifierId id, @Nullable ICondition condition) {
    return new JsonRedirect(id, condition);
  }

  /** Makes an unconditional redirect to the given ID */
  protected JsonRedirect redirect(ModifierId id) {
    return conditionalRedirect(id, null);
  }

  @Override
  public void run(CachedOutput cache) throws IOException {
    addModifiers();
    allModifiers.forEach((id, data) -> saveJson(cache, id, data.serialize()));
    composableModifiers.forEach((id, data) -> saveJson(cache, id, data.serialize()));
  }

  /** Serializes the given modifier with its condition and redirects */
  private static JsonObject serializeModifier(@Nullable Modifier modifier, @Nullable ICondition condition, JsonRedirect[] redirects) {
    JsonObject json;
    if (modifier != null) {
      json = ModifierManager.MODIFIER_LOADERS.serialize(modifier).getAsJsonObject();
    } else {
      json = new JsonObject();
    }
    if (redirects.length != 0) {
      JsonArray array = new JsonArray();
      for (JsonRedirect redirect : redirects) {
        array.add(redirect.toJson());
      }
      json.add("redirects", array);
    }
    if (condition != null) {
      json.add("condition", CraftingHelper.serialize(condition));
    }
    return json;
  }

  /** Result record, as its nicer than a pair */
  private record Result(@Nullable Modifier modifier, @Nullable ICondition condition, JsonRedirect[] redirects) {
    /** Writes this result to JSON */
    public JsonObject serialize() {
      return serializeModifier(modifier, condition, redirects);
    }
  }

  /** Result for composable too */
  private record Composable(ComposableModifier.Builder builder, @Nullable ICondition condition, JsonRedirect[] redirects) {
    /** Writes this result to JSON */
    public JsonObject serialize() {
      return serializeModifier(builder.build(), condition, redirects);
    }
  }
}
