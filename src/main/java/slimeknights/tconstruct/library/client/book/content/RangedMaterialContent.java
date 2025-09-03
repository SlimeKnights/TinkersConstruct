package slimeknights.tconstruct.library.client.book.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.TConstruct.getResource;

public class RangedMaterialContent extends AbstractMaterialContent {
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("ranged_material");

  public RangedMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
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
      case 0 -> LimbMaterialStats.ID;
      case 1 -> GripMaterialStats.ID;
      case 2 -> StatlessMaterialStats.BOWSTRING.getIdentifier();
      default -> null;
    };
  }

  @Override
  protected String getTextKey(MaterialId material) {
    if (detailed) {
      String primaryKey = String.format("material.%s.%s.ranged", material.getNamespace(), material.getPath());
      if (Util.canTranslate(primaryKey)) {
        return primaryKey;
      }
      return String.format("material.%s.%s.encyclopedia", material.getNamespace(), material.getPath());
    }
    return String.format("material.%s.%s.flavor", material.getNamespace(), material.getPath());
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(LimbMaterialStats.ID) || statsId.equals(GripMaterialStats.ID) || statsId.equals(StatlessMaterialStats.BOWSTRING.getIdentifier());
  }


  /* Categories */

  @Override
  protected void addCategory(List<ItemElement> displayTools, MaterialId material) {
    if (MaterialRegistry.getInstance().isInTag(material, TinkerTags.Materials.BALANCED)) {
      displayTools.add(makeCategoryIcon(TinkerTools.fishingRod.get().getRenderTool(), getResource("balanced")));
    } else if (MaterialRegistry.getInstance().isInTag(material, TinkerTags.Materials.LIGHT)) {
      displayTools.add(makeCategoryIcon(TinkerTools.crossbow.get().getRenderTool(), getResource("light")));
    } else if (MaterialRegistry.getInstance().isInTag(material, TinkerTags.Materials.HEAVY)) {
      displayTools.add(makeCategoryIcon(TinkerTools.longbow.get().getRenderTool(), getResource("heavy")));
    }
  }
}
