package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Logic to help in creating new tools
 */
public final class ToolBuildHandler {
  private ToolBuildHandler() {}

  /** Materials for use in multipart tool rendering */
  private static final List<MaterialId> RENDER_MATERIALS = Arrays.asList(
    new MaterialId(TConstruct.MOD_ID, "ui_render_head"),
    new MaterialId(TConstruct.MOD_ID, "ui_render_handle"),
    new MaterialId(TConstruct.MOD_ID, "ui_render_extra"),
    new MaterialId(TConstruct.MOD_ID, "ui_render_large"),
    new MaterialId(TConstruct.MOD_ID, "ui_render_extra_large"));

  /**
   * Builds a tool stack from a material list and a given tool definition
   * @param tool       Tool instance
   * @param materials  Material list
   * @return  Item stack with materials
   */
  public static ItemStack buildItemFromMaterials(IModifiable tool, List<IMaterial> materials) {
    return ToolStack.createTool(tool.asItem(), tool.getToolDefinition(), materials).createStack();
  }

  /**
   * Gets the render material for the given index
   * @param index  Index
   * @return  Render material
   */
  public static MaterialId getRenderMaterial(int index) {
    return RENDER_MATERIALS.get(index % RENDER_MATERIALS.size());
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
    // if no parts, just return the item directly with the display tag
    ItemStack stack;
    if (size == 0) {
      stack = new ItemStack(item);
    } else {
      List<MaterialId> toolMaterials = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        toolMaterials.add(i, getRenderMaterial(i));
      }
      stack = new MaterialIdNBT(toolMaterials).updateStack(new ItemStack(item));
    }
    stack.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
    return stack;
  }


  /* Item groups */

  /**
   * Adds all sub items to a tool
   * @param item             item being created
   * @param itemList         List to fill with items
   * @param fixedMaterials   Materials that should be forced
   */
  public static void addDefaultSubItems(IModifiable item, List<ItemStack> itemList, IMaterial... fixedMaterials) {
    // no parts? just add this item
    if (!item.getToolDefinition().isMultipart()) {
      itemList.add(buildItemFromMaterials(item, Collections.emptyList()));
    } else if (MaterialRegistry.isFullyLoaded()) {
      // if a specific material is set, show just that
      String showOnlyId = Config.COMMON.showOnlyToolMaterial.get();
      boolean added = false;
      if (!showOnlyId.isEmpty()) {
        MaterialId materialId = MaterialId.tryCreate(showOnlyId);
        if (materialId != null) {
          IMaterial material = MaterialRegistry.getMaterial(materialId);
          if (material != IMaterial.UNKNOWN) {
            if (addSubItem(item, itemList, material, fixedMaterials)) {
              added = true;
            }
          }
        }
      }
      // if the material was not applicable or we do not have a filter set, search the rest
      if (!added) {
        for (IMaterial material : MaterialRegistry.getInstance().getVisibleMaterials()) {
          // if we added it and we want a single material, we are done
          if (addSubItem(item, itemList, material, fixedMaterials) && !showOnlyId.isEmpty()) {
            break;
          }
        }
      }
    }
  }

  /** Makes a single sub item for the given materials */
  private static boolean addSubItem(IModifiable item, List<ItemStack> items, IMaterial material, IMaterial[] fixedMaterials) {
    List<IToolPart> required = item.getToolDefinition().getRequiredComponents();
    List<IMaterial> materials = new ArrayList<>(required.size());
    for (int i = 0; i < required.size(); i++) {
      if (fixedMaterials.length > i && fixedMaterials[i] != null && required.get(i).canUseMaterial(fixedMaterials[i])) {
        materials.add(fixedMaterials[i]);
      }
      else if (required.get(i).canUseMaterial(material)) {
        materials.add(material);
      } else {
        return false;
      }
    }
    items.add(buildItemFromMaterials(item, materials));
    return true;
  }
}
