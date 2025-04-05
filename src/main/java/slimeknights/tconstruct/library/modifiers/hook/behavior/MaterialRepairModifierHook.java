package slimeknights.tconstruct.library.modifiers.hook.behavior;

import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * Hook allowing a modifier to add an additional repair material to a tool.
 * @see slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook
 */
public interface MaterialRepairModifierHook {
  /**
   * Checks if the given material can be used to repair this tool
   * @param tool      Tool to check
   * @param material  Material to check
   * @return  True if it can be used to repair this tool
   */
  boolean isRepairMaterial(IToolStackView tool, ModifierEntry modifier, MaterialId material);

  /**
   * Gets the amount of durability restored by this material for repair.
   * Important: make sure to filter by your material else you will override how much is repaired by other materials.
   * @param tool      Tool instance
   * @param material  Material used for repair
   * @return  Repair amount
   */
  float getRepairAmount(IToolStackView tool, ModifierEntry modifier, MaterialId material);

  /** Merger that gets the max repair amount across all materials */
  record MaxMerger(Collection<MaterialRepairModifierHook> modules) implements MaterialRepairModifierHook {
    @Override
    public boolean isRepairMaterial(IToolStackView tool, ModifierEntry modifier, MaterialId material) {
      for (MaterialRepairModifierHook module : modules) {
        if (module.isRepairMaterial(tool, modifier, material)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public float getRepairAmount(IToolStackView tool, ModifierEntry modifier, MaterialId material) {
      float maxRepair = 0;
      for (MaterialRepairModifierHook module : modules) {
        float repair = module.getRepairAmount(tool, modifier, material);
        if (repair > maxRepair) {
          maxRepair = repair;
        }
      }
      return maxRepair;
    }
  }
}
