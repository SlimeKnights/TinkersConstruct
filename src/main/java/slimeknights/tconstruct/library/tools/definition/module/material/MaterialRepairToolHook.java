package slimeknights.tconstruct.library.tools.definition.module.material;

import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook for repairing a tool via tool materials */
public interface MaterialRepairToolHook {
  /**
   * Checks if the given material can be used to repair this tool
   * @param tool      Tool to check
   * @param material  Material to check
   * @return  True if it can be used to repair this tool
   */
  boolean isRepairMaterial(IToolStackView tool, MaterialId material);

  /**
   * Gets the amount of durability restored by this material for repair.
   * Important: make sure to filter by your material else you will override how much is repaired by other materials.
   * @param tool      Tool instance
   * @param material  Material used for repair
   * @return  Repair amount
   */
  float getRepairAmount(IToolStackView tool, MaterialId material);


  /** Gets the repair stat for the given tool */
  static boolean canRepairWith(IToolStackView tool, MaterialId material) {
    // if material repair can do it, stop here as that's the fastest check
    if (tool.getHook(ToolHooks.MATERIAL_REPAIR).isRepairMaterial(tool, material)) {
      return true;
    }
    for (ModifierEntry entry : tool.getModifiers()) {
      if (entry.getHook(ModifierHooks.MATERIAL_REPAIR).isRepairMaterial(tool, entry, material)) {
        return true;
      }
    }
    return false;
  }

  /** Gets the repair stat for the given tool */
  static float repairAmount(IToolStackView tool, MaterialId material) {
    float maxRepair = tool.getHook(ToolHooks.MATERIAL_REPAIR).getRepairAmount(tool, material);
    for (ModifierEntry entry : tool.getModifiers()) {
      float repair = entry.getHook(ModifierHooks.MATERIAL_REPAIR).getRepairAmount(tool, entry, material);
      if (repair > maxRepair) {
        maxRepair = repair;
      }
    }
    return maxRepair;
  }

  /** Merger that takes the largest option from all nested modules */
  record MaxMerger(Collection<MaterialRepairToolHook> hooks) implements MaterialRepairToolHook {
    @Override
    public boolean isRepairMaterial(IToolStackView tool, MaterialId material) {
      for (MaterialRepairToolHook hook : hooks) {
        if (hook.isRepairMaterial(tool, material)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public float getRepairAmount(IToolStackView tool, MaterialId material) {
      float maxRepair = 0;
      for (MaterialRepairToolHook hook : hooks) {
        float repair = hook.getRepairAmount(tool, material);
        if (repair > maxRepair) {
          maxRepair = repair;
        }
      }
      return maxRepair;
    }
  }
}
