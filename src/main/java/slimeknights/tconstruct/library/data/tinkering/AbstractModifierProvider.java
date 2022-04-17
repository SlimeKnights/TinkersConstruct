package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.GenericDataProvider;
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
  protected void addModifier(ModifierId id, @Nullable ICondition condition, Modifier result) {
    Result previous = allModifiers.putIfAbsent(id, new Result(result, condition));
    if (previous != null) {
      throw new IllegalArgumentException("Duplicate modifier " + id);
    }
  }

  /** Adds a modifier to be saved */
  protected void addModifier(ModifierId id, Modifier result) {
    addModifier(id, null, result);
  }

  /** Adds a modifier to be saved */
  protected void addModifier(DynamicModifier<?> id, @Nullable ICondition condition, Modifier result) {
    addModifier(id.getId(), condition, result);
  }

  /** Adds a modifier to be saved */
  protected void addModifier(DynamicModifier<?> id, Modifier result) {
    addModifier(id, null, result);
  }

  @Override
  public void run(HashCache cache) throws IOException {
    addModifiers();
    allModifiers.forEach((id, data) -> saveThing(cache, id, convert(data)));
  }

  /** Converts the given object to json */
  private static JsonObject convert(Result result) {
    JsonObject json = ModifierManager.MODIFIER_LOADERS.serialize(result.modifier()).getAsJsonObject();
    if (result.condition != null) {
      json.add("condition", CraftingHelper.serialize(result.condition));
    }
    return json;
  }

  /** Result record, as its nicer than a pair */
  private record Result(Modifier modifier, @Nullable ICondition condition) {}
}
