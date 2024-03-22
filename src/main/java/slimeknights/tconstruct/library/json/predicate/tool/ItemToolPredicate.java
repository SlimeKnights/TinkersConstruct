package slimeknights.tconstruct.library.json.predicate.tool;

import net.minecraft.world.item.Item;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/**
 * Tool predicate that matches by item
 */
public record ItemToolPredicate(IJsonPredicate<Item> predicate) implements ToolContextPredicate {
  public static final RecordLoadable<ItemToolPredicate> LOADER = RecordLoadable.create(ItemPredicate.LOADER.directField("item_type", ItemToolPredicate::predicate), ItemToolPredicate::new);

  @Override
  public boolean matches(IToolContext tool) {
    return predicate.matches(tool.getItem());
  }

  @Override
  public IGenericLoader<? extends ToolContextPredicate> getLoader() {
    return LOADER;
  }
}
