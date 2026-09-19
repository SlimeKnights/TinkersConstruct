package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.function.Consumer;

/** Builder for {@link ItemPartRecipe} */
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "item")
public class ItemPartRecipeBuilder extends AbstractRecipeBuilder<ItemPartRecipeBuilder> {
  private final Pattern pattern;
  private final ItemOutput result;
  @Setter
  private Ingredient patternItem = IPartBuilderRecipe.DEFAULT_PATTERNS;
  private MaterialId materialId = MaterialId.UNKNOWN;
  private int cost = 0;
  @Setter
  private ResourceLocation titleKey = null;

  /** Creates a builder for the given pattern ID */
  public static ItemPartRecipeBuilder item(ResourceLocation pattern, ItemOutput result) {
    return item(new Pattern(pattern), result);
  }

  /** Sets the material Id and cost */
  public ItemPartRecipeBuilder material(MaterialId material, int cost) {
    this.materialId = material;
    this.cost = cost;
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Loadables.ITEM.getKey(result.get().getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "parts");
    consumer.accept(new LoadableFinishedRecipe<>(new ItemPartRecipe(id, materialId, pattern, patternItem, cost, result, titleKey), ItemPartRecipe.LOADER, advancementId));
  }
}
