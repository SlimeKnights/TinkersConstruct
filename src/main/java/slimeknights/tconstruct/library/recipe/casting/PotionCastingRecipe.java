package slimeknights.tconstruct.library.recipe.casting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.util.List;

/**
 * Recipe for casting a fluid onto an item, copying the fluid NBT to the item
 */
@RequiredArgsConstructor
public class PotionCastingRecipe implements ICastingRecipe, IMultiRecipe<DisplayCastingRecipe> {
  public static final RecordLoadable<PotionCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP,
    IngredientLoadable.DISALLOW_EMPTY.requiredField("bottle", r -> r.bottle),
    FluidIngredient.LOADABLE.requiredField("fluid", r -> r.fluid),
    Loadables.ITEM.requiredField("result", r -> r.result),
    IntLoadable.FROM_ONE.defaultField("cooling_time", 5, r -> r.coolingTime),
    PotionCastingRecipe::new);

  @Getter
  private final TypeAwareRecipeSerializer<?> serializer;
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  /** Input on the casting table, always consumed */
  private final Ingredient bottle;
  /** Potion ingredient, typically just the potion tag */
  private final FluidIngredient fluid;
  /** Potion item result, will be given the proper NBT */
  private final Item result;
  /** Cooling time for this recipe, used for tipped arrows */
  private final int coolingTime;

  @Override
  public RecipeType<?> getType() {
    return serializer.getType();
  }

  private List<DisplayCastingRecipe> displayRecipes = null;

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    return bottle.test(inv.getStack()) && fluid.test(inv.getFluid());
  }

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    return fluid.getAmount(inv.getFluid());
  }

  @Override
  public boolean isConsumed() {
    return true;
  }

  @Override
  public boolean switchSlots() {
    return false;
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    return coolingTime;
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    ItemStack result = new ItemStack(this.result);
    result.setTag(inv.getFluidTag());
    return result;
  }

  @Override
  public List<DisplayCastingRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      // create a subrecipe for every potion variant
      List<ItemStack> bottles = List.of(bottle.getItems());
      displayRecipes = ForgeRegistries.POTIONS.getValues().stream()
        .map(potion -> {
          ItemStack result = PotionUtils.setPotion(new ItemStack(this.result), potion);
          return new DisplayCastingRecipe(getType(), bottles, fluid.getFluids().stream()
                                                              .map(fluid -> new FluidStack(fluid.getFluid(), fluid.getAmount(), result.getTag()))
                                                              .toList(),
                                          result, coolingTime, true);
        }).toList();
    }
    return displayRecipes;
  }


  /* Recipe interface methods */

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, bottle);
  }

  /** @deprecated use {@link #assemble(Container, RegistryAccess)} */
  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(this.result);
  }
}
