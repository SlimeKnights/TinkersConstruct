package slimeknights.tconstruct.library.recipe.casting;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;

import javax.annotation.Nonnull;

/** Shared logic between item and material casting */
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  /* Common fields */
  protected static final LoadableField<Ingredient,AbstractCastingRecipe> CAST_FIELD = IngredientLoadable.ALLOW_EMPTY.defaultField("cast", Ingredient.EMPTY, AbstractCastingRecipe::getCast);
  protected static final LoadableField<Boolean,AbstractCastingRecipe> CAST_CONSUMED_FIELD = BooleanLoadable.INSTANCE.defaultField("cast_consumed", false, false, AbstractCastingRecipe::isConsumed);
  protected static final LoadableField<Boolean,AbstractCastingRecipe> SWITCH_SLOTS_FIELD = BooleanLoadable.INSTANCE.defaultField("switch_slots", false, false, AbstractCastingRecipe::switchSlots);

  @Getter @Nonnull
  private final RecipeType<?> type;
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  @Getter
  private final Ingredient cast;
  @Getter
  private final boolean consumed;
  @Getter @Accessors(fluent = true)
  private final boolean switchSlots;

  protected AbstractCastingRecipe(RecipeType<?> type, ResourceLocation id, String group, Ingredient cast, boolean consumed, boolean switchSlots) {
    this.type = type;
    this.id = id;
    this.group = group;
    this.cast = cast;
    this.consumed = cast != Ingredient.EMPTY && consumed;
    this.switchSlots = switchSlots;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, this.cast);
  }
}
