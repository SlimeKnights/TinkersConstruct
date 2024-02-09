package slimeknights.tconstruct.library.json.predicate.tool;

import net.minecraft.world.item.Item;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/**
 * Tool predicate that matches by item
 */
public record ItemToolPredicate(IJsonPredicate<Item> predicate) implements ToolContextPredicate {
  public static final IGenericLoader<ItemToolPredicate> LOADER = new NestedLoader<>("item_type", ItemPredicate.LOADER, ItemToolPredicate::new, p -> p.predicate);

  @Override
  public boolean matches(IToolContext tool) {
    return predicate.matches(tool.getItem());
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }
}
