package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Logic to help in creating new tools
 */
public final class ToolBuildHandler {
  private ToolBuildHandler() {}

  private static final MaterialId RENDER_MATERIAL = new MaterialId(TConstruct.MOD_ID, "ui_render");

  /** Fully random material instance. */
  public static final RandomMaterial RANDOM = RandomMaterial.random().allowHidden().build();
  static {
    RecipeCacheInvalidator.addReloadListener(client -> {
      RANDOM.clearCache();
      RandomMaterial.ancient().clearCache();
    });
  }

  /** Materials for use in multipart tool rendering */
  private static final List<MaterialVariantId> RENDER_MATERIALS = Arrays.asList(
    MaterialVariantId.create(RENDER_MATERIAL, "head"),
    MaterialVariantId.create(RENDER_MATERIAL, "handle"),
    MaterialVariantId.create(RENDER_MATERIAL, "extra"),
    MaterialVariantId.create(RENDER_MATERIAL, "large"),
    MaterialVariantId.create(RENDER_MATERIAL, "extra_large"));

  /**
   * Builds a tool stack from a material list and a given tool definition
   * @param tool       Tool instance
   * @param materials  Material list
   * @return  Item stack with materials
   */
  public static ItemStack buildItemFromMaterials(IModifiable tool, MaterialNBT materials) {
    return ToolStack.createTool(tool.asItem(), tool.getToolDefinition(), materials).createStack();
  }

  /** Method to build an ancient tool with random materials */
  public static ToolStack buildToolRandomMaterials(IModifiable tool, RandomMaterial material, RandomSource randomSource) {
    ToolDefinition definition = tool.getToolDefinition();
    List<MaterialStatsId> stats = ToolMaterialHook.stats(definition);
    return ToolStack.createTool(tool.asItem(), definition, RandomMaterial.build(stats, Collections.nCopies(stats.size(), material), randomSource));
  }

  /** Method to build an ancient tool with random materials */
  public static ToolStack buildToolRandomMaterials(IModifiable tool, RandomSource randomSource) {
    return buildToolRandomMaterials(tool, RANDOM, randomSource);
  }

  /** Method to build an ancient tool with random materials */
  public static ItemStack buildItemRandomMaterials(IModifiable tool, RandomSource randomSource) {
    return buildToolRandomMaterials(tool, randomSource).createStack();
  }

  /**
   * Gets the render material for the given index
   * @param index  Index
   * @return  Render material
   */
  public static MaterialVariantId getRenderMaterial(int index) {
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
    // during datagen we have no idea if we will or won't have materials, so just add them regardless, won't hurt anything
    if (!definition.isDataLoaded() || definition.hasMaterials()) {
		  // use all 5 render materials for display stacks, having too many materials is not a problem and its easier than making this reload sensitive
      stack = new MaterialIdNBT(RENDER_MATERIALS).updateStack(stack);
    }
    stack.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
    return stack;
  }


  /* Item groups */

  /**
   * Adds all sub items to a tool
   * @param tab    Tab being filled
   * @param item   item being created
   */
  public static void addVariants(Consumer<ItemStack> tab, IModifiable item, String showOnlyMaterial) {
    ToolDefinition definition = item.getToolDefinition();
    boolean hasMaterials = definition.hasMaterials();
    if (!definition.isDataLoaded() || (hasMaterials && !MaterialRegistry.isFullyLoaded())) {
      // not loaded? cannot properly build it
      tab.accept(new ItemStack(item));
    } else if (!hasMaterials) {
      // no parts? just add this item
      tab.accept(buildItemFromMaterials(item, MaterialNBT.EMPTY));
    } else {
      // if a specific material is set, show just that in search
      boolean added = false;
      if (!showOnlyMaterial.isEmpty()) {
        MaterialId materialId = MaterialId.tryParse(showOnlyMaterial);
        if (materialId != null) {
          IMaterial material = MaterialRegistry.getMaterial(materialId);
          if (material != IMaterial.UNKNOWN) {
            ItemStack tool = createSingleMaterial(item, MaterialVariant.of(material));
            if (!tool.isEmpty()) {
              tab.accept(tool);
              added = true;
            }
          }
        }
      }
      // add all materials to the parent, conditionally to search
      if (!added) {
        for (IMaterial material : MaterialRegistry.getInstance().getVisibleMaterials()) {
          // if we added it and we want a single material, we are done
          ItemStack tool = createSingleMaterial(item, MaterialVariant.of(material));
          if (!tool.isEmpty()) {
            tab.accept(tool);
            // if filter is set we wanted just the 1 item
            if (!showOnlyMaterial.isEmpty()) {
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Makes a tool with a single material.
   * @param item      Tool to create
   * @param material  Material to be used for applicable parts. Any parts that disallow the material will be set to first of their type
   * @return Built tool stack, or empty if no part allowed this material
   */
  public static ItemStack createSingleMaterial(IModifiable item, MaterialVariant material) {
    List<MaterialStatsId> required = ToolMaterialHook.stats(item.getToolDefinition());
    MaterialNBT.Builder materials = MaterialNBT.builder();
    boolean useMaterial = false;
    for (MaterialStatsId requirement : required) {
      // try to use requested material
      if (requirement.canUseMaterial(material.getId())) {
        materials.add(material);
        useMaterial = true;
      } else {
        // fallback to first that works
        materials.add(MaterialRegistry.firstWithStatType(requirement));
      }
    }
    // only report success if we actually used the material somewhere
    if (useMaterial) {
      return buildItemFromMaterials(item, materials.build());
    }
    return ItemStack.EMPTY;
  }

  /**
   * Gets display tool part
   * @param toolPart tool part
   * @param i determines material color
   * @return ItemStack of part for display
   */
  public static ItemStack getDisplayPart(IToolPart toolPart, int i) {
    // mark the part as display to suppress the invalid material tooltip
    ItemStack item = toolPart.withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(i));
    item.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
    return item;
  }

}
