package slimeknights.tconstruct.library.tools.definition.module.material;

import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

  /**
   * Adds all repair materials from the given tool to the material list for display in recipe viewers.
   * Note that this hook may be called in recipe viewer logic causing the tool materials list to contain {@link slimeknights.tconstruct.library.tools.helper.ToolBuildHandler#RENDER_MATERIAL}.
   * You will need to filter those out alongside unknown for most accurate results.
   * @param tool       Tool instance
   * @param materials  List of materials being built;
   */
  default void addRepairMaterials(IToolStackView tool, Set<MaterialId> materials) {}


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

  /** Gets all repair materials for the tool instance */
  static Set<MaterialId> getRepairMaterials(IToolStackView tool) {
    Set<MaterialId> materials = new HashSet<>();
    tool.getHook(ToolHooks.MATERIAL_REPAIR).addRepairMaterials(tool, materials);
    for (ModifierEntry entry : tool.getModifiers()) {
      entry.getHook(ModifierHooks.MATERIAL_REPAIR).addRepairMaterials(tool, entry, materials);
    }
    return materials;
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

    @Override
    public void addRepairMaterials(IToolStackView tool, Set<MaterialId> materials) {
      for (MaterialRepairToolHook hook : hooks) {
        hook.addRepairMaterials(tool, materials);
      }
    }
  }
}
