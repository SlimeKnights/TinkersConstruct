package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Streams;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.library.tools.nbt.ToolItemNBT;

import java.util.List;
import java.util.stream.Collectors;

public final class ToolBuildHandler {

  /**
   * Builds an ItemStack of this tool with the given materials from the ItemStacks, if possible.
   *
   * @param stacks Items to build with. Have to be in the correct order and contain material items.
   * @return The built item or null if invalid input.
   */
  public static ItemStack buildItemFromStacks(NonNullList<ItemStack> stacks, ToolCore tool) {
    List<PartMaterialRequirement> requiredComponents = tool.getToolDefinition().getRequiredComponents();

    if (stacks.size() != requiredComponents.size() || !canBeBuiltFromParts(stacks, requiredComponents)) {
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
    StatsNBT stats = tool.buildToolStats(materials);

    ToolData toolData = new ToolData(
      new ToolItemNBT(tool),
      new MaterialNBT(materials),
      stats
    );

    ItemStack output = new ItemStack(tool);
    output.setTag(toolData.serializeToNBT());
    return output;
  }

  /**
   * Checks if the tool can be built from the given input stacks
   *
   * @param stacks the input items
   * @param requiredComponents the required components
   * @return if the given tool can be built from the given inputs
   */
  private static boolean canBeBuiltFromParts(NonNullList<ItemStack> stacks, List<PartMaterialRequirement> requiredComponents) {
    return Streams.zip(requiredComponents.stream(), stacks.stream(), PartMaterialRequirement::isValid).allMatch(Boolean::booleanValue);
  }

  private ToolBuildHandler() {
  }
}
