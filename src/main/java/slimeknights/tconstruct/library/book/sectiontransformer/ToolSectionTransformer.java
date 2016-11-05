package slimeknights.tconstruct.library.book.sectiontransformer;

import java.util.Optional;

import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentTool;
import slimeknights.tconstruct.library.tools.ToolCore;

public class ToolSectionTransformer extends ContentListingSectionTransformer {

  public ToolSectionTransformer() {
    super("tools");
  }

  @Override
  protected void processPage(ContentListing listing, PageData page) {
    if(page.content instanceof ContentTool) {
      String toolName = ((ContentTool) page.content).toolName;
      Optional<ToolCore> tool = TinkerRegistry.getTools().stream()
                                              .filter(toolCore -> toolName.equals(toolCore.getIdentifier()))
                                              .findFirst();

      tool.ifPresent(toolCore -> listing.addEntry(toolCore.getLocalizedName(), page));
    }
  }
}
