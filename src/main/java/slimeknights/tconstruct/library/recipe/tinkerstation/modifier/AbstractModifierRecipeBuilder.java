package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Shared logic between normal and incremental modifier recipe builders */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractModifierRecipeBuilder<T extends AbstractModifierRecipeBuilder<T>> extends AbstractRecipeBuilder<T> {
  protected static final Lazy<Ingredient> DEFAULT_TOOL = Lazy.of(() -> Ingredient.fromTag(TinkerTags.Items.MODIFIABLE));

  private final ModifierEntry result;
  private Ingredient tools = Ingredient.EMPTY;
  private ModifierMatch requirements = ModifierMatch.ALWAYS;
  private String requirementsError = null;
  private int maxLevel = 0;
  private int upgradeSlots = 0;
  private int abilitySlots = 0;

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools  Modifier tools list
   * @return  Builder instance
   */
  public T setTools(Ingredient tools) {
    this.tools = tools;
    return (T) this;
  }

  /**
   * Sets the tag for applicable tools
   * @param tag  Tag
   * @return  Builder instance
   */
  public T setTools(ITag<Item> tag) {
    return this.setTools(Ingredient.fromTag(tag));
  }

  /**
   * Sets the modifier requirements for this recipe
   * @param requirements  Modifier requirements
   * @return  Builder instance
   */
  public T setRequirements(ModifierMatch requirements) {
    this.requirements = requirements;
    return (T) this;
  }

  /**
   * Sets the modifier requirements error for when it does not matcH
   * @param requirementsError  Requirements error lang key
   * @return  Builder instance
   */
  public T setRequirementsError(String requirementsError) {
    this.requirementsError = requirementsError;
    return (T) this;
  }

  /**
   * Sets the max level for this modifier
   * @param level  Max level
   * @return  Builder instance
   */
  public T setMaxLevel(int level) {
    if (level < 0) {
      throw new IllegalArgumentException("Level must be non-negative");
    }
    this.maxLevel = level;
    return (T) this;
  }


  /* Slots */

  /**
   * Sets the number of upgrade slots required by this recipe
   * @param slots  Upgrade slot count
   * @return  Builder instance
   */
  public T setUpgradeSlots(int slots) {
    if (slots < 0) {
      throw new IllegalArgumentException("Slots must be positive");
    }
    if (abilitySlots != 0) {
      throw new IllegalStateException("Cannot set both upgrade and ability slots");
    }
    this.upgradeSlots = slots;
    return (T) this;
  }

  /**
   * Sets the number of ability slots required by this recipe
   * @param slots  Ability slot count
   * @return  Builder instance
   */
  public T setAbilitySlots(int slots) {
    if (slots < 0) {
      throw new IllegalArgumentException("Slots must be positive");
    }
    if (upgradeSlots != 0) {
      throw new IllegalStateException("Cannot set both upgrade and ability slots");
    }
    this.abilitySlots = slots;
    return (T) this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, result.getModifier().getId());
  }

  /** Base logic to write all relevant builder fields to JSON */
  protected abstract class ModifierFinishedRecipe extends AbstractFinishedRecipe {
    public ModifierFinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      if (tools == Ingredient.EMPTY) {
        json.add("tools", DEFAULT_TOOL.get().serialize());
      } else {
        json.add("tools", tools.serialize());
      }
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
  }
}
