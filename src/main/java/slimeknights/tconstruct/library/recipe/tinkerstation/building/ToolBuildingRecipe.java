package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This recipe is used for crafting a set of parts into a tool
 */
@AllArgsConstructor
public class ToolBuildingRecipe implements ITinkerStationRecipe {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final IModifiable output;
  protected final int outputCount;
  protected final List<Ingredient> ingredients;

  /** @deprecated use {@link #ToolBuildingRecipe(ResourceLocation, String, IModifiable, int, List)} */
  @Deprecated
  public ToolBuildingRecipe(ResourceLocation id, String group, IModifiable output) {
    this(id, group, output, 1, Collections.emptyList());
  }

  /** Gets the additional recipe requirements beyond the tool parts */
  public List<Ingredient> getExtraRequirements() {
    return ingredients;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.toolBuildingRecipeSerializer.get();
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level worldIn) {
    if (!inv.getTinkerableStack().isEmpty()) {
      return false;
    }
    List<PartRequirement> parts = output.getToolDefinition().getData().getParts();
    int requiredInputs = parts.size() + ingredients.size();
    int maxInputs = inv.getInputCount();
    // disallow if we have no inputs, or if we have too few slots
    if (requiredInputs == 0 || requiredInputs > maxInputs) {
      return false;
    }
    // each part must match the given slot
    int i;
    int partSize = parts.size();
    for (i = 0; i < partSize; i++) {
      if (!parts.get(i).matches(inv.getInput(i).getItem())) {
        return false;
      }
    }
    // remaining slots must match extra requirements
    for (; i < maxInputs; i++) {
      Ingredient ingredient = LogicHelper.getOrDefault(ingredients, i - partSize, Ingredient.EMPTY);
      if (!ingredient.test(inv.getInput(i))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack assemble(ITinkerStationContainer inv) {
    // first n slots contain parts
    List<MaterialVariant> materials = IntStream.range(0, output.getToolDefinition().getData().getParts().size())
                                               .mapToObj(i -> MaterialVariant.of(IMaterialItem.getMaterialFromStack(inv.getInput(i))))
                                               .toList();
    return ToolStack.createTool(output.asItem(), output.getToolDefinition(), new MaterialNBT(materials)).createStack(outputCount);
  }

  /** @deprecated Use {@link #assemble(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return new ItemStack(this.output);
  }
}
