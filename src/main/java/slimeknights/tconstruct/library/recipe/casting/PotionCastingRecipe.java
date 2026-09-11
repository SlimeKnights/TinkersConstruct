package slimeknights.tconstruct.library.recipe.casting;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Recipe for casting a fluid onto an item, copying the fluid NBT to the item.
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.recipe.casting.potion}
 */
public class PotionCastingRecipe implements ICastingRecipe, IMultiRecipe<IDisplayableCastingRecipe> {
  protected static final LoadableField<FluidIngredient, PotionCastingRecipe> FLUID_FIELD = FluidIngredient.LOADABLE.requiredField("fluid", r -> r.fluid);
  protected static final LoadableField<Integer, PotionCastingRecipe> COOLING_TIME_FIELD = IntLoadable.FROM_ONE.defaultField("cooling_time", 5, r -> r.coolingTime);
  public static final RecordLoadable<PotionCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP,
    IngredientLoadable.DISALLOW_EMPTY.requiredField("bottle", r -> r.bottle),
    FLUID_FIELD,
    Loadables.ITEM.requiredField("result", r -> r.result),
    COOLING_TIME_FIELD,
    PotionCastingRecipe::new);

  @Getter
  protected final TypeAwareRecipeSerializer<?> serializer;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  /** Input on the casting table, always consumed */
  protected final Ingredient bottle;
  /** Potion ingredient, typically just the potion tag */
  protected final FluidIngredient fluid;
  /** Potion item result, will be given the proper NBT */
  protected final Item result;
  /** Cooling time for this recipe, used for tipped arrows */
  protected final int coolingTime;

  public PotionCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient bottle, FluidIngredient fluid, Item result, int coolingTime) {
    this.serializer = serializer;
    this.id = id;
    this.group = group;
    this.bottle = bottle;
    this.fluid = fluid;
    this.result = result;
    this.coolingTime = coolingTime;
    CastingRecipeLookup.registerCastable(result);
  }

  @Override
  public RecipeType<?> getType() {
    return serializer.getType();
  }

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


  /* JEI */
  // TODO 1.21: consider making this a display recipe instead of a multirecipe
  protected List<IDisplayableCastingRecipe> displayRecipes = null;

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      Collection<Potion> potions = ForgeRegistries.POTIONS.getValues();
      List<ItemStack> results = new ArrayList<>(potions.size());
      // first, make all the potion items
      for (Potion potion : potions) {
        if (potion == Potions.EMPTY) continue;
        results.add(PotionUtils.setPotion(new ItemStack(this.result), potion));
      }
      // next, it's time to do the fluids
      // we want an order of Mod 1 Potion 1, Mod 1 Potion 2, ..., Mod 2 Potion 1, ...
      // this is potentially displaying multiple fluids from the fluid tag
      List<FluidStack> potionFluids = this.fluid.getFluids();
      int totalSize = results.size() * potionFluids.size();
      List<ItemStack> displayResults = new ArrayList<>(totalSize);
      List<FluidStack> displayFluids = new ArrayList<>(totalSize);
      for (FluidStack fluid : potionFluids) {
        displayResults.addAll(results);
        for (ItemStack result : results) {
          displayFluids.add(new FluidStack(fluid.getFluid(), fluid.getAmount(), result.getTag()));
        }
      }
      this.displayRecipes = List.of(DisplayCastingRecipe.from(this)
        .cast(bottle).consumed()
        .fluids(List.copyOf(displayFluids))
        .results(List.copyOf(displayResults)).linkFluidsToOutput()
        .coolingTime(coolingTime)
        .build());
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


  /* JEI helpers */

  /** Gets a list of all potion IDs ready for usage in building NBT */
  @SuppressWarnings("deprecation")
  public static List<String> getPotionIds() {
    return BuiltInRegistries.POTION.holders()
      .filter(holder -> !holder.is(Potions.EMPTY_ID))
      .map(holder -> holder.key().location().toString())
      .toList();
  }
}
