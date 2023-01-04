package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for a modifier with a swappable string key */
public class SwappableModifierRecipeBuilder extends ModifierRecipeBuilder {
  private final String value;
  protected SwappableModifierRecipeBuilder(ModifierId modifier, String value) {
    super(new ModifierEntry(modifier, 1));
    this.value = value;
    // most variants do not want this as that will conflict, explicitly set it on the default if desired
    this.allowCrystal = false;
  }

  /** Creates a new builder */
  public static SwappableModifierRecipeBuilder modifier(ModifierId modifier, String value) {
    return new SwappableModifierRecipeBuilder(modifier, value);
  }

  /** Creates a new builder */
  public static SwappableModifierRecipeBuilder modifier(LazyModifier modifier, String value) {
    return modifier(modifier.getId(), value);
  }

  @Override
  public ModifierRecipeBuilder setMaxLevel(int level) {
    throw new UnsupportedOperationException("Max level is always 1 for a swappable modifier recipe");
  }

  /* Building */

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 input");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new Finished(id, advancementId, false));
    if (includeUnarmed) {
      if (requirements != ModifierMatch.ALWAYS) {
        throw new IllegalStateException("Cannot use includeUnarmed with requirements");
      }
      consumer.accept(new Finished(new ResourceLocation(id.getNamespace(), id.getPath() + "_unarmed"), null, true));
    }
  }

  private class Finished extends ModifierRecipeBuilder.FinishedAdding {
    public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID, boolean withUnarmed) {
      super(ID, advancementID, withUnarmed);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.swappableModifierSerializer.get();
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      super.serializeRecipeData(json);
      JsonObject result = json.getAsJsonObject("result");
      result.remove("level");
      result.addProperty("value", value);
    }
  }
}
