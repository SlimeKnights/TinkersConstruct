package slimeknights.tconstruct.library.book.sectiontransformer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import java.util.Optional;

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
      String toolName = ((ContentTool) page.content).toolName;
      Optional<ToolCore> tool = Registry.ITEM.stream()
        .filter((item) -> toolName.equals(Registry.ITEM.getId(item).toString()) && item instanceof ToolCore)
        .map(item -> (ToolCore) item)
        .findFirst();

      tool.ifPresent(toolCore -> listing.addEntry(toolCore.getLocalizedName().getString(), page));
    } else {
      super.processPage(book, listing, page);
    }
  }
}
