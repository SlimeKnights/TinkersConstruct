package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Datagen for dynamic modifiers */
public abstract class AbstractModifierProvider extends GenericDataProvider {
  private final Map<ModifierId,Result> allModifiers = new HashMap<>();

  public AbstractModifierProvider(DataGenerator generator) {
    super(generator, PackType.SERVER_DATA, ModifierManager.FOLDER, ModifierManager.GSON);
  }

  /**
   * Function to add all relevant modifiers
   */
  protected abstract void addModifiers();

  /** Adds a modifier to be saved */
  protected void addModifier(ModifierId id, @Nullable ICondition condition, @Nullable Modifier result, JsonRedirect... redirects) {
    if (result == null && redirects.length == 0) {
      throw new IllegalArgumentException("Must hae either a modifier or a redirect");
    }
    if (redirects.length == 0) {
      redirects = null;
    }

    Result previous = allModifiers.putIfAbsent(id, new Result(result, condition, redirects));
    if (previous != null) {
      throw new IllegalArgumentException("Duplicate modifier " + id);
    }
  }

  /** Adds a modifier to be saved */
  protected void addModifier(ModifierId id, @Nullable Modifier result, JsonRedirect... redirects) {
    addModifier(id, null, result, redirects);
  }

  /** Adds a modifier to be saved */
  protected void addModifier(DynamicModifier<?> id, @Nullable ICondition condition, @Nullable Modifier result, JsonRedirect... redirects) {
    addModifier(id.getId(), condition, result, redirects);
  }

  /** Adds a modifier to be saved */
  protected void addModifier(DynamicModifier<?> id, @Nullable Modifier result, JsonRedirect... redirects) {
    addModifier(id, null, result, redirects);
  }

  /** Adds a modifier redirect */
  protected void addRedirect(ModifierId id, JsonRedirect... redirects) {
    addModifier(id, null, null, redirects);
  }


  /* Redirect helpers */

  /** Makes a conditional redirect to the given ID */
  protected JsonRedirect conditionalRedirect(ModifierId id, @Nullable ICondition condition) {
    return new JsonRedirect(id, condition);
  }

  /** Makes an unconditional redirect to the given ID */
  protected JsonRedirect redirect(ModifierId id) {
    return conditionalRedirect(id, null);
  }

  @Override
  public void run(HashCache cache) throws IOException {
    addModifiers();
    allModifiers.forEach((id, data) -> saveThing(cache, id, convert(data)));
  }

  /** Converts the given object to json */
  private static JsonObject convert(Result result) {
    JsonObject json;
    if (result.modifier != null) {
      json = ModifierManager.MODIFIER_LOADERS.serialize(result.modifier).getAsJsonObject();
    } else {
      json = new JsonObject();
    }
    if (result.redirects != null) {
      JsonArray array = new JsonArray();
      for (JsonRedirect redirect : result.redirects) {
        array.add(redirect.toJson());
      }
      json.add("redirects", array);
    }
    if (result.condition != null) {
      json.add("condition", CraftingHelper.serialize(result.condition));
    }
    return json;
  }

  /** Result record, as its nicer than a pair */
  private record Result(@Nullable Modifier modifier, @Nullable ICondition condition, JsonRedirect[] redirects) {}
}
