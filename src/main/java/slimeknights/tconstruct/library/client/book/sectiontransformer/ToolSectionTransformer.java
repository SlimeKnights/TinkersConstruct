package slimeknights.tconstruct.library.client.book.sectiontransformer;

import net.minecraft.world.item.Items;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.content.ContentListing;
import slimeknights.mantle.client.book.transformer.ContentListingSectionTransformer;
import slimeknights.tconstruct.library.client.book.content.ContentTool;

/** Section transformer to generate an index with tool names */
public class ToolSectionTransformer extends ContentListingSectionTransformer {
  public static final ToolSectionTransformer INSTANCE = new ToolSectionTransformer("tools");

  public ToolSectionTransformer(String name, boolean largeTitle, boolean centerTitle) {
    super(name, largeTitle, centerTitle);
  }

  public ToolSectionTransformer(String name) {
    super(name, null, null);
  }

  @Override
  protected boolean processPage(BookData book, ContentListing listing, PageData page) {
    // only add tool pages if the tool exists, barrier is the fallback item for missing
    if (!(page.content instanceof ContentTool tool && tool.getTool().asItem() == Items.BARRIER)) {
      super.processPage(book, listing, page);
    }
    return true;
  }
}
