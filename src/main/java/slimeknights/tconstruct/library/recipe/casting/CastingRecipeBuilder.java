package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.recipe.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CastingRecipeBuilder extends AbstractRecipeBuilder<CastingRecipeBuilder> {
  private final Item result;
  private boolean castConsumed = false;
  private boolean switchSlots = false;
  private FluidStack fluidStack = FluidStack.EMPTY;
  private Ingredient cast = Ingredient.EMPTY;
  private int coolingTime = 0;
  private String group;
  private final CastingRecipeSerializer<?> recipeSerializer;

  private CastingRecipeBuilder(IItemProvider resultIn, CastingRecipeSerializer<?> serializer) {
    this.result = resultIn.asItem();
    this.recipeSerializer = serializer;
  }

  public static CastingRecipeBuilder castingRecipe(IItemProvider resultIn, CastingRecipeSerializer<?> serializer) {
    return new CastingRecipeBuilder(resultIn, serializer);
  }

  public static CastingRecipeBuilder basinRecipe(IItemProvider resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.basinRecipeSerializer.get());
  }

  public static CastingRecipeBuilder tableRecipe(IItemProvider resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.tableRecipeSerializer.get());
  }

  public CastingRecipeBuilder setFluid(FluidStack fluidStack) {
    this.fluidStack = fluidStack;
    return this;
  }

  public CastingRecipeBuilder setGroup(String groupIn) {
    this.group = groupIn;
    return this;
  }

  public CastingRecipeBuilder setCast(Tag<Item> tagIn, boolean consumed) {
    return this.setCast(Ingredient.fromTag(tagIn), consumed);
  }

  public CastingRecipeBuilder setCast(IItemProvider itemIn, boolean consumed) {
    return this.setCast(Ingredient.fromItems(itemIn), consumed);
  }

  public CastingRecipeBuilder setCast(Ingredient ingredient, boolean consumed) {
    this.cast = ingredient;
    this.castConsumed = consumed;
    return this;
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for 'casts'
   */
  public CastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, this.result.getRegistryName());
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
    ResourceLocation resultKey = this.result.getRegistryName();
    ResourceLocation saveKey = new ResourceLocation(save);
    if (saveKey.equals(resultKey)) {
      throw new IllegalStateException("Recipe " + saveKey + " should remove its 'save' argument");
    } else {
      this.build(consumerIn, saveKey);
    }
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    this.validate(id);
    ResourceLocation advancementId = this.buildAdvancement(id, "casting");
    consumerIn.accept(new CastingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.castConsumed, this.switchSlots, this.fluidStack, this.cast, this.result, this.coolingTime, this.advancementBuilder, advancementId, this.recipeSerializer));
  }

  /**
   * Makes sure that this is obtainable
   */
  private void validate(ResourceLocation id) {
    if (this.fluidStack.isEmpty()) {
      throw new IllegalStateException("Casting recipes require a FluidStack");
    }
  }

  public static class Result implements IFinishedRecipe {
    @Getter
    protected final ResourceLocation ID;
    private final String group;
    private final boolean castConsumed;
    private final boolean switchSlots;
    private final FluidStack fluidStack;
    private final Ingredient cast;
    private final Item result;
    private final int coolingTime;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;
    @Getter
    private final IRecipeSerializer<? extends AbstractCastingRecipe> serializer;

    public Result(ResourceLocation idIn, String groupIn, boolean castConsumed, boolean switchSlots, FluidStack fluidStackIn, @Nullable Ingredient cast, Item resultIn, int coolingTime, Advancement.Builder advancementBuilder, ResourceLocation advancementId, IRecipeSerializer<? extends AbstractCastingRecipe> serializer) {
      this.ID = idIn;
      this.group = groupIn;
      this.castConsumed = castConsumed;
      this.switchSlots = switchSlots;
      this.fluidStack = fluidStackIn;
      this.cast = cast;
      this.result = resultIn;
      this.coolingTime = coolingTime;
      this.advancementBuilder = advancementBuilder;
      this.advancementID = advancementId;
      this.serializer = serializer;
    }

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
      JsonObject fluidStack = RecipeUtil.serializeFluidStack(this.fluidStack);
      json.add("fluidstack", fluidStack);
      json.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
      if (this.coolingTime != 0) {
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
