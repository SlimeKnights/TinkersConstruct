package slimeknights.tconstruct.library.recipe.partbuilder.recycle;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder for custom part builder tool recycling recipes for general damageable items.
 * Note {@link PartBuilderToolRecycleBuilder} is generally better for tools with parts, this is for tools that recycle into standard items.
 */
@RequiredArgsConstructor(staticName = "tool")
@Accessors(fluent = true)
public class PartBuilderRecycleBuilder extends AbstractRecipeBuilder<PartBuilderRecycleBuilder> {
  private final Ingredient tool;
  @Setter
  private Ingredient pattern = Ingredient.of(TinkerFluids.venomBottle);
  private final Map<Pattern, ItemOutput> results = new HashMap<>();

  /** Creates a builder for the given tool */
  public static PartBuilderRecycleBuilder tool(ItemLike... tools) {
    return tool(Ingredient.of(tools));
  }

  /** Adds a result to the builder */
  public PartBuilderRecycleBuilder result(Pattern pattern, ItemOutput output) {
    ItemOutput existing = results.put(pattern, output);
    if (existing != null) {
      throw new IllegalStateException("Duplicate result for " + pattern + ": " + existing);
    }
    return this;
  }

  /** Adds a result to the builder */
  public PartBuilderRecycleBuilder result(Pattern pattern, ItemLike item, int count) {
    return result(pattern, ItemOutput.fromItem(item, count));
  }

  /** Adds a result to the builder */
  public PartBuilderRecycleBuilder result(Pattern pattern, TagKey<Item> tag, int count) {
    return result(pattern, ItemOutput.fromTag(tag, count));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Loadables.ITEM.getKey(tool.getItems()[0].getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "parts");
    consumer.accept(new LoadableFinishedRecipe<>(new PartBuilderRecycle(id, tool, pattern, results), PartBuilderRecycle.LOADER, advancementId));
  }
}
