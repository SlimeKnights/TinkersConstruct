package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Streams;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.stream.Collectors;

public final class ToolBuildHandler {

  /**
   * Builds an ItemStack of this tool with the given materials from the ItemStacks, if possible.
   * TODO: do we still need this?
   *
   * @param stacks Items to build with. Have to be in the correct order and contain material items.
   * @return The built item or null if invalid input.
   */
  public static ItemStack buildItemFromStacks(NonNullList<ItemStack> stacks, ToolCore tool) {
    if (!canToolBeBuilt(stacks, tool)) {
      return ItemStack.EMPTY;
    }

    List<IMaterial> materials = stacks.stream()
      .filter(stack -> !stack.isEmpty())
      .map(IMaterialItem::getMaterialFromStack)
      .collect(Collectors.toList());

    return buildItemFromMaterials(tool, materials);
  }

  /**
   * Builds a too stack from a material list
   * @param tool       Tool instance
   * @param materials  Material list
   * @return  Item stack with materials
   */
  public static ItemStack buildItemFromMaterials(ToolCore tool, List<IMaterial> materials) {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition());
    toolStack.setMaterials(materials);
    return toolStack.createStack();
  }

  /**
   * Checks if the tool can be built from the given items
   * TODO: do we still need this?
   *
   * @param stacks the input items
   * @param tool the tool
   * @return if the given tool can be built from the items
   */
  public static boolean canToolBeBuilt(NonNullList<ItemStack> stacks, ToolCore tool) {
    List<IToolPart> requiredComponents = tool.getToolDefinition().getRequiredComponents();
    return stacks.size() == requiredComponents.size() && canBeBuiltFromParts(stacks, requiredComponents);
  }

  /**
   * Checks if the tool can be built from the given parts
   *
   * @param stacks the input items
   * @param requiredComponents the required components
   * @return if the given tool can be built from the given parts
   */
  private static boolean canBeBuiltFromParts(NonNullList<ItemStack> stacks, List<IToolPart> requiredComponents) {
    return Streams.zip(requiredComponents.stream(), stacks.stream(), (part, stack) -> part.asItem() == stack.getItem() && part.getMaterial(stack) != IMaterial.UNKNOWN).allMatch(Boolean::booleanValue);
  }

  private ToolBuildHandler() {
  }
}
