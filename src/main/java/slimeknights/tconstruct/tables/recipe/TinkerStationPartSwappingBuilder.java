package slimeknights.tconstruct.tables.recipe;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolMaterialSwappingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Builder for {@link TinkerStationPartSwapping} and {@link ToolMaterialSwappingRecipe} */
@RequiredArgsConstructor(staticName = "tools")
public class TinkerStationPartSwappingBuilder extends AbstractRecipeBuilder<TinkerStationPartSwappingBuilder> {
  private final Ingredient tools;
  private boolean fromTool = false;
  @Setter
  @Accessors(fluent = true)
  private int maxStackSize = 16;
  /** Additional requirements beyond the "part" */
  private final List<SizedIngredient> extraRequirements = new ArrayList<>();

  /** Sets the swapping to be from a tool instead of from a part */
  public TinkerStationPartSwappingBuilder fromTool() {
    this.fromTool = true;
    return this;
  }

  /** Adds an extra ingredient requirement */
  public TinkerStationPartSwappingBuilder addExtraRequirement(SizedIngredient ingredient) {
    extraRequirements.add(ingredient);
    return this;
  }

  /** Adds an extra ingredient requirement */
  public TinkerStationPartSwappingBuilder addExtraRequirement(Ingredient ingredient) {
    return addExtraRequirement(SizedIngredient.of(ingredient));
  }

  /** Adds an extra ingredient requirement */
  public TinkerStationPartSwappingBuilder addExtraRequirement(ItemLike... items) {
    return addExtraRequirement(SizedIngredient.fromItems(items));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Loadables.ITEM.getKey(tools.getItems()[0].getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (fromTool) {
      consumer.accept(new LoadableFinishedRecipe<>(new ToolMaterialSwappingRecipe(id, tools, maxStackSize, extraRequirements), ToolMaterialSwappingRecipe.LOADER, null));
    } else {
      consumer.accept(new LoadableFinishedRecipe<>(new TinkerStationPartSwapping(id, tools, maxStackSize, extraRequirements), TinkerStationPartSwapping.LOADER, null));
    }
  }
}
