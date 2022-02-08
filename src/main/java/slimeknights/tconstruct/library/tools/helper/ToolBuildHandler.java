package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
  public static ItemStack buildItemFromMaterials(IModifiable tool, MaterialNBT materials) {
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
    // if no parts, just return the item directly with the display tag
    ItemStack stack = new ItemStack(item);
    if (definition.isMultipart()) {
		// use all 5 render materials for display stacks, having too many materials is not a problem and its easier than making this reload sensitive
      stack = new MaterialIdNBT(RENDER_MATERIALS).updateStack(stack);
    }
    stack.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
    return stack;
  }

  /**
   * Gets a list of random materials consistent with the given tool definition data
   * @param data         Data, primarily used for part requirements
   * @param maxTier      Max tier of material allowed
   * @param allowHidden  If true, hidden materials may be used
   * @return  List of random materials
   */
  public static MaterialNBT randomMaterials(ToolDefinitionData data, int maxTier, boolean allowHidden) {
    // start by getting a list of materials for each stat type we need
    List<PartRequirement> requirements = data.getParts();
    // figure out which stat types we need
    Map<MaterialStatsId,List<IMaterial>> materialChoices = requirements.stream()
      .map(PartRequirement::getStatType)
      .distinct()
      .collect(Collectors.toMap(Function.identity(), t -> new ArrayList<>()));
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    registry.getAllMaterials().stream()
            .filter(mat -> (allowHidden || !mat.isHidden()) && mat.getTier() <= maxTier)
            .forEach(mat -> {
              for (IMaterialStats stats : registry.getAllStats(mat.getIdentifier())) {
                List<IMaterial> list = materialChoices.get(stats.getIdentifier());
                if (list != null) {
                  list.add(mat);
                }
              }
            });

    // then randomly choose a material from the lists for each part
    MaterialNBT.Builder builder = MaterialNBT.builder();
    for (PartRequirement requirement : requirements) {
      // if the list has no materials for some reason, skip, null should be impossible but might as well be safe
      List<IMaterial> choices = materialChoices.get(requirement.getStatType());
      if (choices == null || choices.isEmpty()) {
        builder.add(MaterialVariant.UNKNOWN);
        TConstruct.LOG.error("Failed to find a {} material of type {} below tier {}", allowHidden ? "non-hidden " : "", requirement.getStatType(), maxTier);
      } else {
        builder.add(choices.get(TConstruct.RANDOM.nextInt(choices.size())));
      }
    }
    return builder.build();
  }


  /* Item groups */

  /**
   * Adds all sub items to a tool
   * @param item             item being created
   * @param itemList         List to fill with items
   * @param fixedMaterials   Materials that should be forced
   */
  public static void addDefaultSubItems(IModifiable item, List<ItemStack> itemList, MaterialVariantId... fixedMaterials) {
    ToolDefinition definition = item.getToolDefinition();
    boolean isMultipart = definition.isMultipart();
    if (!definition.isDataLoaded() || (isMultipart && !MaterialRegistry.isFullyLoaded())) {
      // not loaded? cannot properly build it
      itemList.add(new ItemStack(item));
    } else if (!isMultipart) {
      // no parts? just add this item
      itemList.add(buildItemFromMaterials(item, MaterialNBT.EMPTY));
    } else {
      // if a specific material is set, show just that
      String showOnlyId = Config.COMMON.showOnlyToolMaterial.get();
      boolean added = false;
      if (!showOnlyId.isEmpty()) {
        MaterialId materialId = MaterialId.tryParse(showOnlyId);
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
  private static boolean addSubItem(IModifiable item, List<ItemStack> items, IMaterial material, MaterialVariantId[] fixedMaterials) {
    List<PartRequirement> required = item.getToolDefinition().getData().getParts();
    MaterialNBT.Builder materials = MaterialNBT.builder();
    for (int i = 0; i < required.size(); i++) {
      if (fixedMaterials.length > i && fixedMaterials[i] != null && required.get(i).canUseMaterial(fixedMaterials[i])) {
        materials.add(fixedMaterials[i]);
      }
      else if (required.get(i).canUseMaterial(material.getIdentifier())) {
        materials.add(material);
      } else {
        return false;
      }
    }
    items.add(buildItemFromMaterials(item, materials.build()));
    return true;
  }
}
