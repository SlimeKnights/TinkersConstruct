package slimeknights.tconstruct.library.client.book.content;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.List;

public class AmmoMaterialContent extends AbstractMaterialContent {
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("ammo_material");

  public AmmoMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
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
      case 0 -> StatlessMaterialStats.ARROW_HEAD.getIdentifier();
      case 1 -> StatlessMaterialStats.ARROW_SHAFT.getIdentifier();
      case 2 -> StatlessMaterialStats.FLETCHING.getIdentifier();
      default -> null;
    };
  }

  @Override
  protected String getTextKey(MaterialId material) {
    if (detailed) {
      String primaryKey = String.format("material.%s.%s.ammo", material.getNamespace(), material.getPath());
      if (Util.canTranslate(primaryKey)) {
        return primaryKey;
      }
      return String.format("material.%s.%s.encyclopedia", material.getNamespace(), material.getPath());
    }
    return String.format("material.%s.%s.flavor", material.getNamespace(), material.getPath());
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(StatlessMaterialStats.ARROW_HEAD.getIdentifier())
      || statsId.equals(StatlessMaterialStats.ARROW_SHAFT.getIdentifier())
      || statsId.equals(StatlessMaterialStats.FLETCHING.getIdentifier());
  }

  @Override
  protected void addPrimaryDisplayItems(List<ItemElement> displayTools, MaterialVariantId materialId) {
    // ammo is always craftable, regardless of what the material properties say
    // never castable or compositable
    ItemElement elementItem = new TinkerItemElement(new ItemStack(TinkerTables.partBuilder));
    elementItem.tooltip = PART_BUILDER;
    displayTools.add(elementItem);
  }
}
