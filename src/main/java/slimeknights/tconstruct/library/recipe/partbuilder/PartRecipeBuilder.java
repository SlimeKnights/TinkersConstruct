package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.function.Consumer;

/**
 * Builder for a material item part crafting recipe
 */
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "partRecipe")
public class PartRecipeBuilder extends AbstractRecipeBuilder<PartRecipeBuilder> {
  private final IMaterialItem output;
  private final int outputAmount;
  @Setter
  private int cost = 1;
  @Setter
  private ResourceLocation pattern = null;
  @Setter
  private Ingredient patternItem = IPartBuilderRecipe.DEFAULT_PATTERNS;

  /**
   * Creates a new part recipe that outputs a single item
   * @param output  Output item
   * @return  Builder instance
   */
  public static PartRecipeBuilder partRecipe(IMaterialItem output) {
    return partRecipe(output, 1);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    this.save(consumerIn, BuiltInRegistries.ITEM.getKey(this.output.asItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
    if (this.outputAmount <= 0) {
      throw new IllegalStateException("recipe " + id + " must output at least 1");
    }
    if (this.cost <= 0) {
      throw new IllegalStateException("recipe " + id + " has no cost associated with it");
    }
    if (this.pattern == null) {
      throw new IllegalStateException("recipe " + id + " has no pattern associated with it");
    }
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new LoadableFinishedRecipe<>(new PartRecipe(id, group, new Pattern(pattern), patternItem, cost, output, outputAmount), PartRecipe.LOADER, advancementId));
  }
}
