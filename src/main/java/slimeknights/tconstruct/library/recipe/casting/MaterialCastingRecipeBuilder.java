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
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.recipe.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class MaterialCastingRecipeBuilder extends AbstractRecipeBuilder<MaterialCastingRecipeBuilder> {
  private final MaterialCastingRecipeSerializer<?> recipeSerializer;
  private String group;
  private Ingredient cast = Ingredient.EMPTY;
  private int fluidAmount = 0;
  private final Item result;
  private boolean consumed = false;
  private boolean switchSlots = false;

  private MaterialCastingRecipeBuilder(IMaterialItem result, MaterialCastingRecipeSerializer<?> recipeSerializer) {
    this.result = result.asItem();
    this.recipeSerializer = recipeSerializer;
  }

  public static MaterialCastingRecipeBuilder castingRecipe(IMaterialItem result, MaterialCastingRecipeSerializer<?> recipeSerializer) {
    return new MaterialCastingRecipeBuilder(result, recipeSerializer);
  }

  public static MaterialCastingRecipeBuilder basinRecipe(IMaterialItem result) {
    return castingRecipe(result, TinkerSmeltery.basinMaterialSerializer.get());
  }

  public static MaterialCastingRecipeBuilder tableRecipe(IMaterialItem result) {
    return castingRecipe(result, TinkerSmeltery.tableMaterialSerializer.get());
  }

  public MaterialCastingRecipeBuilder setGroup(String group) {
    this.group = group;
    return this;
  }

  public MaterialCastingRecipeBuilder setCast(Tag<Item> tag, boolean consumed) {
    return this.setCast(Ingredient.fromTag(tag), consumed);
  }

  public MaterialCastingRecipeBuilder setCast(IItemProvider item, boolean consumed) {
    return this.setCast(Ingredient.fromItems(item), consumed);
  }

  public MaterialCastingRecipeBuilder setCast(Ingredient cast, boolean consumed) {
    this.cast = cast;
    this.consumed = consumed;
    return this;
  }

  public MaterialCastingRecipeBuilder setFluidAmount(int fluidAmount) {
    this.fluidAmount = fluidAmount;
    return this;
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for 'casts'
   */
  public MaterialCastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  public void build(Consumer<IFinishedRecipe> consumer) {
    this.build(consumer, Objects.requireNonNull(this.result.getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    this.validate(id);
    ResourceLocation advancementId = this.buildAdvancement(id, "casting");
    consumer.accept(new MaterialCastingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.consumed, this.switchSlots, this.fluidAmount, this.cast, this.result, this.advancementBuilder, advancementId, this.recipeSerializer));
  }

  /**
   * Makes sure that this is valid
   */
  private void validate(ResourceLocation id) {
    if (this.fluidAmount <= 0) {
      throw new IllegalStateException("Material casting recipes require a positive amount of fluid");
    }
  }

  @AllArgsConstructor
  public static class Result implements IFinishedRecipe {
    @Getter
    protected final ResourceLocation ID;
    private final String group;
    private final boolean consumed;
    private final boolean switchSlots;
    private final int fluidAmount;
    private final Ingredient cast;
    private final Item result;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;
    @Getter
    private final IRecipeSerializer<? extends MaterialCastingRecipe> serializer;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      if (cast != Ingredient.EMPTY) {
        json.add("cast", this.cast.serialize());
        if (this.consumed) {
          json.addProperty("cast_consumed", true);
        }
      }
      if (this.switchSlots) {
        json.addProperty("switch_slots", true);
      }
      json.addProperty("fluid_amount", this.fluidAmount);
      json.addProperty("result", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.result.asItem())).toString());
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
