package slimeknights.tconstruct.library.client.book.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Content page for melee/harvest materials
 */
public class MeleeHarvestMaterialContent extends AbstractMaterialContent {
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = getResource("melee_harvest_material");

  public MeleeHarvestMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
    super(materialVariant, detailed);
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Nullable
  @Override
  protected MaterialStatsId getStatType(int index) {
    return switch (index) {
      case 0 -> HeadMaterialStats.ID;
      case 1 -> HandleMaterialStats.ID;
      case 2 -> StatlessMaterialStats.BINDING.getIdentifier();
      default -> null;
    };
  }

  @Override
  protected String getTextKey(MaterialId material) {
    return String.format(detailed ? "material.%s.%s.encyclopedia" : "material.%s.%s.flavor", material.getNamespace(), material.getPath());
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(HeadMaterialStats.ID) || statsId.equals(HandleMaterialStats.ID) || statsId.equals(StatlessMaterialStats.BINDING.getIdentifier());
  }


  /* Categories */

  @Override
  protected void addCategory(List<ItemElement> displayTools, MaterialId material) {
    if (MaterialRegistry.getInstance().isInTag(material, TinkerTags.Materials.GENERAL)) {
      displayTools.add(makeCategoryIcon(TinkerTools.handAxe.get().getRenderTool(), getResource("general")));
    } else if (MaterialRegistry.getInstance().isInTag(material, TinkerTags.Materials.MELEE)) {
      displayTools.add(makeCategoryIcon(TinkerTools.sword.get().getRenderTool(),   getResource("melee")));
    } else if (MaterialRegistry.getInstance().isInTag(material, TinkerTags.Materials.HARVEST)) {
      displayTools.add(makeCategoryIcon(TinkerTools.pickaxe.get().getRenderTool(), getResource("harvest")));
    }
  }
}
