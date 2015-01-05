package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

import tconstruct.library.tools.materials.IMaterialStats;

public class PartMaterialWrapper {

  // ANY of these has to match
  private final Set<IToolPart> neededPart = new HashSet<>();
  // ALL of the material stats have to be there
  private final String[] neededMaterials;

  public PartMaterialWrapper(IToolPart part, String... materials) {
    neededPart.add(part);
    neededMaterials = materials;
  }

  public boolean isValid(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return false;
    }

    if (!(stack.getItem() instanceof IToolPart)) {
      return false;
    }

    IToolPart toolPart = (IToolPart) stack.getItem();
    return isValid(toolPart, toolPart.getMaterial(stack));
  }

  public boolean isValid(IToolPart part, Material material) {
    // wrong part
    if (!neededPart.contains(part)) {
      return false;
    }

    // not all needed materials present
    for (String type : neededMaterials) {
      if (!material.hasStats(type)) {
        return false;
      }
    }

    return true;
  }
}
