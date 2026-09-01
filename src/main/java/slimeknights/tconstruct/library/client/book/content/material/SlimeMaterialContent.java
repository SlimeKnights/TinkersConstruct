package slimeknights.tconstruct.library.client.book.content.material;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.SlimeStats;

/** Content page for slimesuit slime, showing stats and traits. */
public class SlimeMaterialContent extends SingleMaterialStatContent {
  public static final ResourceLocation ID = TConstruct.getResource("slime_material");

  public SlimeMaterialContent(MaterialVariantId materialVariant, boolean detailed) {
    super(materialVariant, detailed);
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected MaterialStatsId getStatType() {
    return SlimeStats.ID;
  }

  @Override
  protected boolean hasPart() {
    return false;
  }

  @Override
  protected String translationSuffix() {
    return "slime";
  }

  @Override
  protected boolean allowPartBuilder() {
    return false;
  }
}
