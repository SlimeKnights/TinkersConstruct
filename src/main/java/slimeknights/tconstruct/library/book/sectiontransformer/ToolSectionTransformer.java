package slimeknights.tconstruct.library.book.sectiontransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Optional;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.tools.ToolCore;

@SideOnly(Side.CLIENT)
public class ToolSectionTransformer extends ContentListingSectionTransformer {

  public ToolSectionTransformer() {
    super("tools");
  }

  @Override
  protected void processPage(BookData book, ContentListing listing, PageData page) {
    // only add tool pages if the tool exists
    if(page.content instanceof ContentTool) {
      String toolName = ((ContentTool) page.content).toolName;
      Optional<ToolCore> tool = TinkerRegistry.getTools().stream()
                                              .filter(toolCore -> toolName.equals(toolCore.getIdentifier()))
                                              .findFirst();

      tool.ifPresent(toolCore -> listing.addEntry(toolCore.getLocalizedName(), page));
    }
    else {
      super.processPage(book, listing, page);
    }
  }
}
