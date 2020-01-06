package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Streams;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
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
    List<PartMaterialType> requiredComponents = tool.getToolDefinition().getRequiredComponents();

    if (stacks.size() != requiredComponents.size() || !canBeBuiltFromParts(stacks, requiredComponents)) {
      return ItemStack.EMPTY;
    }

    List<IMaterial> materials = stacks.stream()
      .filter(stack -> !stack.isEmpty())
      .map(MaterialItem::getMaterialFromStack)
      .collect(Collectors.toList());

    ToolData toolData = new ToolData(
      new ToolItemNBT(tool),
      new MaterialNBT(materials)
    );

    ItemStack output = new ItemStack(tool);
    output.setTag(toolData.serializeToNBT());
    return output;
  }

  private static boolean canBeBuiltFromParts(NonNullList<ItemStack> stacks, List<PartMaterialType> requiredComponents) {
    return Streams.zip(requiredComponents.stream(), stacks.stream(), PartMaterialType::isValid).allMatch(Boolean::booleanValue);
  }

  private ToolBuildHandler() {
  }
}
