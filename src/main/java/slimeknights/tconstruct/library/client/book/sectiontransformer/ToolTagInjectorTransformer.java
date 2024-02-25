package slimeknights.tconstruct.library.client.book.sectiontransformer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.ContentTool;

import java.util.Objects;

/** Injects tools into a section based on a tag */
public class ToolTagInjectorTransformer extends AbstractTagInjectingTransformer<Item> {
  public static final ToolTagInjectorTransformer INSTANCE = new ToolTagInjectorTransformer();

  private ToolTagInjectorTransformer() {
    super(Registry.ITEM_REGISTRY, TConstruct.getResource("load_tools"), ContentTool.ID);
  }

  @Override
  protected ResourceLocation getId(Item item) {
    return Objects.requireNonNull(item.getRegistryName());
  }

  @Override
  protected PageContent createFallback(Item item) {
    return new ContentTool(item);
  }
}
