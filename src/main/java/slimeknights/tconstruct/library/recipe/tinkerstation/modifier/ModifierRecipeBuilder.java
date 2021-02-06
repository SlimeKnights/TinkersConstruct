package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "modifier")
public class ModifierRecipeBuilder extends AbstractRecipeBuilder<ModifierRecipeBuilder> {
  private final List<SizedIngredient> inputs = new ArrayList<>();
  private final ModifierEntry result;
  @Setter @Accessors(chain = true)
  private ModifierMatch requirements = ModifierMatch.ALWAYS;
  @Setter @Accessors(chain = true)
  private String requirementsError;
  private int maxLevel = 0;
  private int upgradeSlots = 0;
  private int abilitySlots = 0;

  /**
   * Creates a new recipe for 1 level of a modifier
   * @param modifier  Modifier
   * @return  Recipe for 1 level of the modifier
   */
  public static ModifierRecipeBuilder modifier(Modifier modifier) {
    return modifier(new ModifierEntry(modifier, 1));
  }


  /* Inputs */

  /**
   * Adds an input to the recipe
   * @param ingredient  Input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(SizedIngredient ingredient) {
    this.inputs.add(ingredient);
    return this;
  }

  /**
   * Adds an input to the recipe
   * @param item    Item input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(IItemProvider item, int amount) {
    return addInput(SizedIngredient.fromItems(amount, item));
  }

  /**
   * Adds an input to the recipe
   * @param item    Item input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(IItemProvider item) {
    return addInput(item, 1);
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ITag<Item> tag, int amount) {
    return addInput(SizedIngredient.fromTag(tag, amount));
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @return  Builder instance
   */
  public ModifierRecipeBuilder addInput(ITag<Item> tag) {
    return addInput(tag, 1);
  }


  /* Slots */

  /**
   * Sets the number of upgrade slots required by this recipe
   * @param slots  Upgrade slot count
   * @return  Builder instance
   */
  public ModifierRecipeBuilder setUpgradeSlots(int slots) {
    if (slots < 0) {
      throw new IllegalArgumentException("Slots must be positive");
    }
    if (abilitySlots != 0) {
      throw new IllegalStateException("Cannot set both upgrade and ability slots");
    }
    this.upgradeSlots = slots;
    return this;
  }

  /**
   * Sets the number of ability slots required by this recipe
   * @param slots  Ability slot count
   * @return  Builder instance
   */
  public ModifierRecipeBuilder setAbilitySlots(int slots) {
    if (slots < 0) {
      throw new IllegalArgumentException("Slots must be positive");
    }
    if (upgradeSlots != 0) {
      throw new IllegalStateException("Cannot set both upgrade and ability slots");
    }
    this.abilitySlots = slots;
    return this;
  }

  /* Other setters */

  /**
   * Sets the max level for this modifier
   * @param level  Max level
   * @return  Builder instance
   */
  public ModifierRecipeBuilder setMaxLevel(int level) {
    if (level < 0) {
      throw new IllegalArgumentException("Level must be non-negative");
    }
    this.maxLevel = level;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, result.getModifier().getId());
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 input");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      JsonArray array = new JsonArray();
      for (SizedIngredient ingredient : inputs) {
        array.add(ingredient.serialize());
      }
      json.add("inputs", array);
      if (requirements != ModifierMatch.ALWAYS) {
        JsonObject reqJson = requirements.serialize();
        reqJson.addProperty("error", requirementsError);
        json.add("requirements", reqJson);
      }
      json.add("result", result.toJson());
      if (maxLevel != 0) {
        json.addProperty("max_level", maxLevel);
      }
      if (upgradeSlots != 0) {
        json.addProperty("upgrade_slots", upgradeSlots);
      }
      if (abilitySlots != 0) {
        json.addProperty("ability_slots", abilitySlots);
      }
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerModifiers.modifierSerializer.get();
    }
  }
}
