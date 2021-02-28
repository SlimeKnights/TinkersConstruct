package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Streams;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ToolBuildHandler {
  public static final String KEY_DISPLAY_TOOL = "tic_display_tool";
  private static final List<MaterialId> RENDER_MATERIALS = Arrays.asList(
    new MaterialId(TConstruct.modID, "ui_render_head"),
    new MaterialId(TConstruct.modID, "ui_render_handle"),
    new MaterialId(TConstruct.modID, "ui_render_extra"),
    new MaterialId(TConstruct.modID, "ui_render_large"),
    new MaterialId(TConstruct.modID, "ui_render_extra_large"));

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
   * Builds a too stack from a material list and a given tool definition
   * @param tool       Tool instance
   * @param materials  Material list
   * @return  Item stack with materials
   */
  public static ItemStack buildItemFromMaterials(ToolCore tool, List<IMaterial> materials) {
    return ToolStack.createTool(tool, tool.getToolDefinition(), materials).createStack();
  }

  /**
   * Builds a tool using the render materials for the sake of display in UIs
   * @param item        Tool item
   * @param definition  Tool definition
   * @return  Tool for rendering
   */
  public static ItemStack buildToolForRendering(Item item, ToolDefinition definition) {
    List<IToolPart> requirements = definition.getRequiredComponents();
    int size = requirements.size();
    List<MaterialId> toolMaterials = new ArrayList<>(size);
    for (int i = 0; i < requirements.size(); i++) {
      toolMaterials.add(i, RENDER_MATERIALS.get(i % RENDER_MATERIALS.size()));
    }
    ItemStack stack = new MaterialIdNBT(toolMaterials).updateStack(new ItemStack(item));
    stack.getOrCreateTag().putBoolean(KEY_DISPLAY_TOOL, true);
    return stack;
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
