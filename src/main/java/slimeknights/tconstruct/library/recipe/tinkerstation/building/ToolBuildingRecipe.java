package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * This recipe is used for crafting a set of parts into a tool
 */
@AllArgsConstructor
public class ToolBuildingRecipe implements ITinkerStationRecipe {
  public static final RecordLoadable<ToolBuildingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.output),
    IntLoadable.FROM_ONE.defaultField("result_count", 1, true, r -> r.outputCount),
    IngredientLoadable.DISALLOW_EMPTY.list(0).defaultField("extra_requirements", List.of(), r -> r.ingredients),
    ToolBuildingRecipe::new).comapFlatMap((recipe, error) -> {
    if (!recipe.output.getToolDefinition().isMultipart() && recipe.ingredients.isEmpty()) {
      throw error.create("Modifiable item must have tool parts or extra requirements to use tool building recipes in " + recipe.id);
    }
    return recipe;
  }, Function.identity());

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final IModifiable output;
  protected final int outputCount;
  protected final List<Ingredient> ingredients;

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
