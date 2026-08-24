package slimeknights.tconstruct.library.client.book.content.material;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.util.html.HtmlSerializable;
import slimeknights.tconstruct.library.client.book.content.AbstractMaterialContent;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import javax.annotation.Nullable;

/** Base class for material types supporting just a single material */
public abstract class SingleMaterialStatContent extends AbstractMaterialContent {
  public SingleMaterialStatContent(MaterialVariantId materialVariant, boolean detailed) {
    super(materialVariant, detailed);
  }

  /** Gets the single supported stat type. */
  protected abstract MaterialStatsId getStatType();

  /** Return true if this stat type is expected to have a tool part. */
  protected abstract boolean hasPart();

  @Override
  protected abstract String translationSuffix();

  @Nullable
  @Override
  protected MaterialStatsId getStatType(int index) {
    return index == 0 ? getStatType() : null;
  }

  @Override
  protected int getStatRows() {
    return 1;
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(getStatType());
  }

  @Override
  protected HtmlSerializable makeStatsHtml(BookData data) {
    return makeStatHtml(getStatType(), true, hasPart());
  }
}
