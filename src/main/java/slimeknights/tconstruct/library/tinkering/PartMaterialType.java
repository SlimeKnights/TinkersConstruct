package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableSet;

import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;

public class PartMaterialType {

  // ANY of these has to match
  private final Set<IToolPart> neededPart = new HashSet<>();
  // ALL of the material stats have to be there
  private final String[] neededTypes;

  public PartMaterialType(IToolPart part, String... statIDs) {
    neededPart.add(part);
    neededTypes = statIDs;
  }

  public boolean isValid(ItemStack stack) {
    if(stack == null || stack.getItem() == null) {
      return false;
    }

    if(!(stack.getItem() instanceof IToolPart)) {
      return false;
    }

    IToolPart toolPart = (IToolPart) stack.getItem();
    return isValid(toolPart, toolPart.getMaterial(stack));
  }

  public boolean isValid(IToolPart part, Material material) {
    // wrong part
    if(!neededPart.contains(part)) {
      return false;
    }

    // not all needed materials present
    for(String type : neededTypes) {
      if(!material.hasStats(type)) {
        return false;
      }
    }

    return true;
  }

  public Set<IToolPart> getPossibleParts() {
    return ImmutableSet.copyOf(neededPart);
  }

  public static class ToolPartType extends PartMaterialType {

    public ToolPartType(IToolPart part) {
      super(part, ToolMaterialStats.TYPE);
    }
  }
}
