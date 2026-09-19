package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

/** @deprecated seems this was never used, so no sense keeping it around. Must have been accidentally committed after a different solution was chosen in a refactor. */
@Deprecated(forRemoval = true)
public interface ISpecializedRepairRecipe {
  /* Fields */
  LoadableField<Ingredient,ISpecializedRepairRecipe> TOOL_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("tool", ISpecializedRepairRecipe::getTool);
  LoadableField<MaterialId,ISpecializedRepairRecipe> REPAIR_MATERIAL_FIELD = MaterialId.PARSER.requiredField("repair_material", ISpecializedRepairRecipe::getRepairMaterial);

  /** Gets the tool ingredient from the recipe */
  Ingredient getTool();
  /** Gets the material ID from the recipe */
  MaterialId getRepairMaterial();
}
