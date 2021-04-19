package slimeknights.tconstruct.library.book.sectiontransformer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.tools.item.ToolCore;

@Environment(EnvType.CLIENT)
public class ToolSectionTransformer extends ContentListingSectionTransformer {

  public ToolSectionTransformer() {
    super("tools");
  }

  @Override
  protected void processPage(BookData book, ContentListing listing, PageData page) {
    // only add tool pages if the tool exists
    if (page.content instanceof ContentTool) {
      Identifier toolId = new Identifier(((ContentTool) page.content).toolName);
      if (Registry.ITEM.containsId(toolId)) {
        Item toolItem = Registry.ITEM.get(toolId);
        if (toolItem instanceof ToolCore) {
          listing.addEntry(((ToolCore)toolItem).getLocalizedName().getString(), page);
        }
      }
    } else {
      super.processPage(book, listing, page);
    }
  }
}
