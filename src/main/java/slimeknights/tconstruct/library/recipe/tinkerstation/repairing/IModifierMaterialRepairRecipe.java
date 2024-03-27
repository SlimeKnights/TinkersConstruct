package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

/** Interface for serializing the modifier material repair recipes */
public interface IModifierMaterialRepairRecipe {
  /* Fields */
  LoadableField<ModifierId,IModifierMaterialRepairRecipe> MODIFIER_FIELD = ModifierId.PARSER.requiredField("modifier", IModifierMaterialRepairRecipe::getModifier);
  LoadableField<MaterialId,IModifierMaterialRepairRecipe> REPAIR_MATERIAL_FIELD = MaterialId.PARSER.requiredField("repair_material", IModifierMaterialRepairRecipe::getRepairMaterial);

  /** Gets the modifier required to apply this repair */
  ModifierId getModifier();

  /** Gets the material ID from the recipe */
  MaterialId getRepairMaterial();
}
