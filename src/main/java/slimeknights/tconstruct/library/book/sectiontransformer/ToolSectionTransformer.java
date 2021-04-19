package slimeknights.tconstruct.library.book.sectiontransformer;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ToolSectionTransformer extends ContentListingSectionTransformer {

  public ToolSectionTransformer() {
    super("tools");
  }

  @Override
  protected void processPage(BookData book, ContentListing listing, PageData page) {
    // only add tool pages if the tool exists
    if (page.content instanceof ContentTool) {
      ResourceLocation toolId = new ResourceLocation(((ContentTool) page.content).toolName);
      if (ForgeRegistries.ITEMS.containsKey(toolId)) {
        Item toolItem = ForgeRegistries.ITEMS.getValue(toolId);
        if (toolItem instanceof ToolCore) {
          listing.addEntry(((ToolCore)toolItem).getLocalizedName().getString(), page);
        }
      }
    } else {
      super.processPage(book, listing, page);
    }
  }
}
