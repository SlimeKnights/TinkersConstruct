package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for a modifier with a swappable string key */
public class SwappableModifierRecipeBuilder extends ModifierRecipeBuilder {
  private final String value;
  protected SwappableModifierRecipeBuilder(Modifier modifier, String value) {
    super(new ModifierEntry(modifier, 1));
    this.value = value;
  }

  /** Creates a new builder */
  public static SwappableModifierRecipeBuilder modifier(Modifier modifier, String value) {
    return new SwappableModifierRecipeBuilder(modifier, value);
  }

  @Override
  public ModifierRecipeBuilder setMaxLevel(int level) {
    throw new UnsupportedOperationException("Max level is always 1 for a swappable modifier recipe");
  }

  /* Building */

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
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

  private class Finished extends ModifierRecipeBuilder.FinishedRecipe {
    public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID, boolean withUnarmed) {
      super(ID, advancementID, withUnarmed);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerModifiers.swappableModifierSerializer.get();
    }

    @Override
    public void serialize(JsonObject json) {
      super.serialize(json);
      JsonObject result = json.getAsJsonObject("result");
      result.remove("level");
      result.addProperty("value", value);
    }
  }
}
