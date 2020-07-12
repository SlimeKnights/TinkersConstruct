package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class CastingRecipeBuilder extends AbstractRecipeBuilder<CastingRecipeBuilder> {
  private final Item result;
  private boolean castConsumed = false;
  private boolean switchSlots = false;
  private FluidIngredient fluid = FluidIngredient.EMPTY;
  private Ingredient cast = Ingredient.EMPTY;
  private int coolingTime = -1;
  private String group;
  private final CastingRecipeSerializer<?> recipeSerializer;

  private CastingRecipeBuilder(IItemProvider resultIn, CastingRecipeSerializer<?> serializer) {
    this.result = resultIn.asItem();
    this.recipeSerializer = serializer;
  }

  /**
   * Creates a new builder instance
   * @param resultIn    Result
   * @param serializer  Serializer type
   * @return  Recipe instance
   */
  public static CastingRecipeBuilder castingRecipe(IItemProvider resultIn, CastingRecipeSerializer<?> serializer) {
    return new CastingRecipeBuilder(resultIn, serializer);
  }

  /**
   * Creates a new casting basin recipe
   * @param resultIn  Recipe result
   * @return  Recipe instance
   */
  public static CastingRecipeBuilder basinRecipe(IItemProvider resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.basinRecipeSerializer.get());
  }

  /**
   * Creates a new casting table recipe
   * @param resultIn  Recipe result
   * @return  Recipe instance
   */
  public static CastingRecipeBuilder tableRecipe(IItemProvider resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.tableRecipeSerializer.get());
  }


  /* Fluids */

  /**
   * Sets the fluid ingredient
   * @param fluid  Fluid ingredient instance
   * @return  Builder instance
   */
  public CastingRecipeBuilder setFluid(FluidIngredient fluid) {
    this.fluid = fluid;
    return this;
  }

  /**
   * Sets the fluid for this recipe, and sets the cooling time based on that fluid
   * @param fluidStack  Fluid input
   * @return  Builder instance
   */
  public CastingRecipeBuilder setFluidAndTime(FluidStack fluidStack) {
    this.fluid = FluidIngredient.of(fluidStack);
    if (this.coolingTime == -1) {
      this.coolingTime = AbstractCastingRecipe.calcCoolingTime(fluidStack);
    }
    return this;
  }

  /**
   * Sets the cooling time for this recipe
   * @param time  Cooling time
   * @return  Builder instance
   */
  public CastingRecipeBuilder setCoolingTime(int time) {
    this.coolingTime = time;
    return this;
  }

  /**
   * Sets the recipe group
   * @param groupIn  Recipe group
   * @return  Builder instance
   */
  public CastingRecipeBuilder setGroup(String groupIn) {
    this.group = groupIn;
    return this;
  }


  /* Cast */

  /**
   * Sets the cast from a tag
   * @param tagIn     Cast tag
   * @param consumed  If true, the cast is consumed
   * @return  Builder instance
   */
  public CastingRecipeBuilder setCast(Tag<Item> tagIn, boolean consumed) {
    return this.setCast(Ingredient.fromTag(tagIn), consumed);
  }

  /**
   * Sets the cast from a tag
   * @param itemIn    Cast item
   * @param consumed  If true, the cast is consumed
   * @return  Builder instance
   */
  public CastingRecipeBuilder setCast(IItemProvider itemIn, boolean consumed) {
    return this.setCast(Ingredient.fromItems(itemIn), consumed);
  }

  /**
   * Sets the cast from an ingredient
   * @param ingredient  Cast ingredient
   * @param consumed    If true, the cast is consumed
   * @return  Builder instance
   */
  public CastingRecipeBuilder setCast(Ingredient ingredient, boolean consumed) {
    this.cast = ingredient;
    this.castConsumed = consumed;
    return this;
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for cast creation
   * @return  Builder instance
   */
  public CastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  /**
   * Builds a recipe using the registry name as the recipe name
   * @param consumerIn  Recipe consumer
   */
  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, Objects.requireNonNull(this.result.getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    if (this.fluid == FluidIngredient.EMPTY) {
      throw new IllegalStateException("Casting recipes require a fluid input");
    }
    ResourceLocation advancementId = this.buildAdvancement(id, "casting");
    consumerIn.accept(new CastingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.castConsumed, this.switchSlots, this.fluid, this.cast, this.result, this.coolingTime, this.advancementBuilder, advancementId, this.recipeSerializer));
  }

  @AllArgsConstructor
  public static class Result implements IFinishedRecipe {
    @Getter
    protected final ResourceLocation ID;
    private final String group;
    private final boolean castConsumed;
    private final boolean switchSlots;
    private final FluidIngredient fluid;
    private final Ingredient cast;
    private final Item result;
    private final int coolingTime;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;
    @Getter
    private final IRecipeSerializer<? extends AbstractCastingRecipe> serializer;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      if (cast != Ingredient.EMPTY) {
        json.add("cast", this.cast.serialize());
        if (castConsumed) {
          json.addProperty("cast_consumed", castConsumed);
        }
      }
      if (switchSlots) {
        json.addProperty("switch_slots", switchSlots);
      }
      json.add("fluid", this.fluid.serialize());
      json.addProperty("result", Objects.requireNonNull(this.result.getRegistryName()).toString());
      if (this.coolingTime != -1) {
        json.addProperty("cooling_time", this.coolingTime);
      }
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
