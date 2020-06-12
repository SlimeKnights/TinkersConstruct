package slimeknights.tconstruct.common.data;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
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
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.AbstractCastingRecipe;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CastingRecipeBuilder {
  private final Item result;
  private boolean castConsumed = false;
  private boolean switchSlots = false;
  private FluidStack fluidStack = FluidStack.EMPTY;
  private Ingredient cast = Ingredient.EMPTY;
  private int coolingTime = 0;
  private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
  private String group;
  private final AbstractCastingRecipe.Serializer<?> recipeSerializer;

  private CastingRecipeBuilder(IItemProvider resultIn, AbstractCastingRecipe.Serializer<?> serializer) {
    this.result = resultIn.asItem();
    this.recipeSerializer = serializer;
  }

  public static CastingRecipeBuilder castingRecipe(IItemProvider resultIn, AbstractCastingRecipe.Serializer<?> serializer) {
    return new CastingRecipeBuilder(resultIn, serializer);
  }

  public static CastingRecipeBuilder basinRecipe(IItemProvider resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.basinRecipeSerializer.get());
  }

  public static CastingRecipeBuilder tableRecipe(IItemProvider resultIn) {
    return castingRecipe(resultIn, TinkerSmeltery.tableRecipeSerializer.get());
  }

  public CastingRecipeBuilder addCriterion(String name, ICriterionInstance criterionInstance) {
    this.advancementBuilder.withCriterion(name, criterionInstance);
    return this;
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

  public CastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.result));
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
    ResourceLocation resultKey = ForgeRegistries.ITEMS.getKey(this.result);
    ResourceLocation saveKey = new ResourceLocation(save);
    if (saveKey.equals(resultKey)) {
      throw new IllegalStateException("Recipe " + saveKey + " should remove its 'save' argument");
    } else {
      this.build(consumerIn, saveKey);
    }
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    this.validate(id);
    this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
    consumerIn.accept(new CastingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.castConsumed, this.switchSlots, this.fluidStack, this.cast, this.result, this.coolingTime, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath()), this.recipeSerializer));
  }

  /*
   * Makes sure that this is obtainable
   */
  private void validate(ResourceLocation id) {
    if (this.fluidStack.isEmpty()) {
      throw new IllegalStateException("Casting recipes require a FluidStack");
    }
    if (this.advancementBuilder.getCriteria().isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
  }

  public static class Result implements IFinishedRecipe {
    private final ResourceLocation id;
    private final String group;
    private final boolean castConsumed;
    private final boolean switchSlots;
    private final FluidStack fluidStack;
    private final Ingredient cast;
    private final Item result;
    private final int coolingTime;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;
    private final IRecipeSerializer<? extends AbstractCastingRecipe> serializer;

    public Result(ResourceLocation idIn, String groupIn, boolean castConsumed, boolean switchSlots, FluidStack fluidStackIn, @Nullable Ingredient cast, Item resultIn, int coolingTime, Advancement.Builder advancementBuilder, ResourceLocation advancementId, IRecipeSerializer<? extends AbstractCastingRecipe> serializer) {
      this.id = idIn;
      this.group = groupIn;
      this.castConsumed = castConsumed;
      this.switchSlots = switchSlots;
      this.fluidStack = fluidStackIn;
      this.cast = cast;
      this.result = resultIn;
      this.coolingTime = coolingTime;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
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
          json.addProperty("castconsumed", castConsumed);
        }
      }
      if (switchSlots) {
        json.addProperty("switchslots", switchSlots);
      }
      JsonObject fluidStack = new JsonObject();
      fluidStack.addProperty("fluid", this.fluidStack.getFluid().getRegistryName().toString());
      fluidStack.addProperty("amount", this.fluidStack.getAmount());
      json.add("fluidstack", fluidStack);
      json.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
      if (this.coolingTime != 0) {
        json.addProperty("coolingtime", this.coolingTime);
      }
    }

    @Override
    public ResourceLocation getID() {
      return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return this.serializer;
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return this.advancementId;
    }
  }
}
