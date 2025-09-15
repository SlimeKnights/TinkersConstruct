package slimeknights.tconstruct.tables.recipe;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import java.util.function.Consumer;

/** Builder for {@link TinkerStationPartSwapping} */
@RequiredArgsConstructor(staticName = "tools")
public class TinkerStationPartSwappingBuilder extends AbstractRecipeBuilder<TinkerStationPartSwappingBuilder> {
  private final Ingredient tools;
  @Setter
  @Accessors(fluent = true)
  private int maxStackSize = 16;


  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Loadables.ITEM.getKey(tools.getItems()[0].getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    consumer.accept(new LoadableFinishedRecipe<>(new TinkerStationPartSwapping(id, tools, maxStackSize), TinkerStationPartSwapping.LOADER, null));
  }
}
