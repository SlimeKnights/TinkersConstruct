package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.casting.material.ToolCastingRecipe.CastPurpose;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "WeakerAccess"})
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "castingRecipe")
public class MaterialCastingRecipeBuilder extends AbstractRecipeBuilder<MaterialCastingRecipeBuilder> {
  @Nullable
  private final IMaterialItem result;
  @Nullable
  private final IModifiable resultTool;
  private final TypeAwareRecipeSerializer<? extends AbstractMaterialCastingRecipe> recipeSerializer;
  private Ingredient cast = Ingredient.EMPTY;
  @Setter
  private int itemCost = 0;
  private CastPurpose castPurpose = CastPurpose.CATALYST;
  private boolean switchSlots = false;
  @Setter
  private IJsonPredicate<MaterialVariantId> allowedMaterials = MaterialPredicate.ANY;
  /** Extra materials for tool casting. Has no impact on part casting. */
  private final List<MaterialVariantId> extraMaterials = new ArrayList<>();

  /**
   * Creates a new material casting recipe for an basin recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder basinRecipe(IMaterialItem result) {
    return castingRecipe(result, null, TinkerSmeltery.basinMaterialSerializer.get());
  }

  /**
   * Creates a new material casting recipe for an table recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder tableRecipe(IMaterialItem result) {
    return castingRecipe(result, null, TinkerSmeltery.tableMaterialSerializer.get());
  }

  /**
   * Creates a new material casting recipe for an basin recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder basinRecipe(IModifiable result) {
    return castingRecipe(null, result, TinkerSmeltery.basinToolSerializer.get());
  }

  /**
   * Creates a new material casting recipe for an table recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder tableRecipe(IModifiable result) {
    return castingRecipe(null, result, TinkerSmeltery.tableToolSerializer.get());
  }

  /**
   * Set the cast to the given ingredient
   * @param cast      Ingredient
   * @param purpose   Function the cast performs. For tool casting, see {@link #setCast(Ingredient, boolean)}.
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(Ingredient cast, CastPurpose purpose) {
    this.cast = cast;
    this.castPurpose = purpose;
    return this;
  }

  /**
   * Set the cast to the given ingredient
   * @param cast      Ingredient
   * @param consumed  If true, cast is consumed. For tool casting, see {@link #setCast(Ingredient, CastPurpose)}.
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(Ingredient cast, boolean consumed) {
    // TODO 1.21: switch MAYBE_MATERIAL to CONSUMED
    return setCast(cast, consumed ? CastPurpose.MAYBE_MATERIAL : CastPurpose.CATALYST);
  }

  /**
   * Sets the cast to the given item.
   * @param item      Cast item
   * @param purpose   Function the cast performs. For part casting, see {@link #setCast(ItemLike, boolean)}.
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(ItemLike item, CastPurpose purpose) {
    return this.setCast(Ingredient.of(item), purpose);
  }

  /**
   * Sets the cast to the given item
   * @param item      Cast item
   * @param consumed  If true, cast is consumed. For tool casting, see {@link #setCast(ItemLike, CastPurpose)}.
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(ItemLike item, boolean consumed) {
    return this.setCast(Ingredient.of(item), consumed);
  }

  /**
   * Sets the cast to the given tag
   * @param tag       Cast tag
   * @param consumed  If true, cast is consumed. For tool casting, see {@link #setCast(Ingredient, CastPurpose)}.
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(TagKey<Item> tag, boolean consumed) {
    return this.setCast(Ingredient.of(tag), consumed);
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for cast creation
   */
  public MaterialCastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  /** Adds a material to set after the end of the parts list */
  public MaterialCastingRecipeBuilder addExtraMaterial(MaterialVariantId material) {
    extraMaterials.add(material);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    this.save(consumer, BuiltInRegistries.ITEM.getKey(Objects.requireNonNull(this.result).asItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (this.itemCost <= 0) {
      throw new IllegalStateException("Material casting recipes require a positive amount of fluid");
    }
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "casting");
    if (result != null) {
      consumer.accept(new LoadableFinishedRecipe<>(new MaterialCastingRecipe(recipeSerializer, id, group, cast, itemCost, result, allowedMaterials, castPurpose != CastPurpose.CATALYST, switchSlots), MaterialCastingRecipe.LOADER, advancementId));
    } else if (resultTool != null) {
      consumer.accept(new LoadableFinishedRecipe<>(new ToolCastingRecipe(recipeSerializer, id, group, cast, itemCost, castPurpose, resultTool, allowedMaterials, extraMaterials), ToolCastingRecipe.LOADER, advancementId));
    } else {
      throw new IllegalArgumentException("Must have either result or result tool");
    }
  }
}
