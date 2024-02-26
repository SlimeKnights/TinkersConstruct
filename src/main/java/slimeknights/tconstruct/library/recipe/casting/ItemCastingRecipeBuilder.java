package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for an item casting recipe. Takes a fluid and optional cast to create an item
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
@RequiredArgsConstructor(staticName = "castingRecipe")
public class ItemCastingRecipeBuilder extends AbstractRecipeBuilder<ItemCastingRecipeBuilder> {
  private final ItemOutput result;
  private final ItemCastingRecipe.Serializer<?> recipeSerializer;
  private Ingredient cast = Ingredient.EMPTY;
  private FluidIngredient fluid = FluidIngredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int coolingTime = -1;
  private boolean consumed = false;
  private boolean switchSlots = false;

  /**
   * Creates a new casting basin recipe
   * @param result  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder basinRecipe(ItemOutput result) {
    return castingRecipe(result, TinkerSmeltery.basinRecipeSerializer.get());
  }

  /**
   * Creates a new casting basin recipe
   * @param result  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder retexturedBasinRecipe(ItemOutput result) {
    return castingRecipe(result, TinkerSmeltery.retexturedBasinRecipeSerializer.get());
  }

  /**
   * Creates a new casting basin recipe
   * @param resultIn  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder basinRecipe(ItemLike resultIn) {
    return basinRecipe(ItemOutput.fromItem(resultIn));
  }

  /**
   * Creates a new casting basin recipe
   * @param result  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder basinRecipe(TagKey<Item> result) {
    return basinRecipe(ItemOutput.fromTag(result, 1));
  }

  /**
   * Creates a new casting table recipe
   * @param resultIn  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder tableRecipe(ItemOutput resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.tableRecipeSerializer.get());
  }

  /**
   * Creates a new casting table recipe
   * @param resultIn  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder retexturedTableRecipe(ItemOutput resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.retexturedTableRecipeSerializer.get());
  }

  /**
   * Creates a new casting table recipe
   * @param resultIn  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder tableRecipe(ItemLike resultIn) {
    return tableRecipe(ItemOutput.fromItem(resultIn));
  }

  /**
   * Creates a new casting table recipe
   * @param result  Recipe result
   * @return  Builder instance
   */
  public static ItemCastingRecipeBuilder tableRecipe(TagKey<Item> result) {
    return tableRecipe(ItemOutput.fromTag(result, 1));
  }


  /* Fluids */

  /**
   * Sets the fluid for this recipe
   * @param fluid   Fluid instance
   * @param amount  amount of fluid
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setFluid(Fluid fluid, int amount) {
    return this.setFluid(FluidIngredient.of(fluid, amount));
  }

  /**
   * Sets the fluid for this recipe
   * @param tagIn   Tag<Fluid> instance
   * @param amount  amount of fluid
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setFluid(TagKey<Fluid> tagIn, int amount) {
    return this.setFluid(FluidIngredient.of(tagIn, amount));
  }

  /**
   * Sets the fluid ingredient
   * @param fluid  Fluid ingredient instance
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setFluid(FluidIngredient fluid) {
    this.fluid = fluid;
    return this;
  }

  /**
   * Sets the recipe cooling time
   * @param temperature  Recipe temperature
   * @param amount       Recipe amount
   */
  public ItemCastingRecipeBuilder setCoolingTime(int temperature, int amount) {
    return setCoolingTime(ICastingRecipe.calcCoolingTime(temperature, amount));
  }

  /**
   * Sets the fluid for this recipe, and cooling time if unset.
   * @param fluidStack  Fluid input
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setFluidAndTime(FluidStack fluidStack) {
    this.fluid = FluidIngredient.of(fluidStack);
    if (this.coolingTime == -1) {
      this.coolingTime = ICastingRecipe.calcCoolingTime(fluidStack);
    }
    return this;
  }

  /**
   * Sets the fluid for this recipe, and cooling time
   * @param tagIn        Tag<Fluid> instance
   * @param temperature  fluid temperature
   * @param amount       amount of fluid
   */
  public ItemCastingRecipeBuilder setFluidAndTime(int temperature, TagKey<Fluid> tagIn, int amount) {
    setFluid(tagIn, amount);
    setCoolingTime(temperature, amount);
    return this;
  }

  /**
   * Sets the fluid for this recipe, and cooling time
   * @param fluid        Fluid for time calculations
   * @param tag          Tag<Fluid> instance
   * @param amount       amount of fluid
   */
  public ItemCastingRecipeBuilder setFluidAndTime(Fluid fluid, TagKey<Fluid> tag, int amount) {
    return setFluidAndTime(fluid.getAttributes().getTemperature() - 300, tag, amount);
  }

  /**
   * Sets the fluid for this recipe, and cooling time
   * @param fluid      Fluid object instance
   * @param forgeTag   If true, uses the forge tag
   * @param amount     amount of fluid
   */
  public ItemCastingRecipeBuilder setFluidAndTime(FluidObject<?> fluid, boolean forgeTag, int amount) {
    return setFluidAndTime(fluid.get(), forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), amount);
  }

  /**
   * Sets the fluid for this recipe using the forge tag, and cooling time
   * @param fluid    Fluid object instance
   * @param amount   amount of fluid
   */
  public ItemCastingRecipeBuilder setFluidAndTime(FluidObject<?> fluid, int amount) {
    return setFluidAndTime(fluid, false, amount);
  }

  /* Cast */

  /**
   * Sets the cast from a tag
   * @param tagIn     Cast tag
   * @param consumed  If true, the cast is consumed
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setCast(TagKey<Item> tagIn, boolean consumed) {
    return this.setCast(Ingredient.of(tagIn), consumed);
  }

  /**
   * Sets the cast from a tag
   * @param itemIn    Cast item
   * @param consumed  If true, the cast is consumed
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setCast(ItemLike itemIn, boolean consumed) {
    return this.setCast(Ingredient.of(itemIn), consumed);
  }

  /**
   * Sets the cast from an ingredient
   * @param ingredient  Cast ingredient
   * @param consumed    If true, the cast is consumed
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setCast(Ingredient ingredient, boolean consumed) {
    this.cast = ingredient;
    this.consumed = consumed;
    return this;
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for cast creation
   * @return  Builder instance
   */
  public ItemCastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  /**
   * Builds a recipe using the registry name as the recipe name
   * @param consumerIn  Recipe consumer
   */
  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    this.save(consumerIn, Objects.requireNonNull(this.result.get().getItem().getRegistryName()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (this.fluid == FluidIngredient.EMPTY) {
      throw new IllegalStateException("Casting recipes require a fluid input");
    }
    if (this.coolingTime < 0) {
      throw new IllegalStateException("Cooling time is too low, must be at least 0");
    }
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "casting");
    consumer.accept(new ItemCastingRecipeBuilder.Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return recipeSerializer;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      if (cast != Ingredient.EMPTY) {
        json.add("cast", cast.toJson());
        if (consumed) {
          json.addProperty("cast_consumed", true);
        }
      }
      if (switchSlots) {
        json.addProperty("switch_slots", true);
      }
      json.add("fluid", fluid.serialize());
      json.add("result", result.serialize());
      json.addProperty("cooling_time", coolingTime);
    }
  }
}
