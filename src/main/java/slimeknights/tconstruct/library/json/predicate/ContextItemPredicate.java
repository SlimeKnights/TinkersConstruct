package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.item.Item;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/** Item predicate storing contextual  */
public record ContextItemPredicate(String key) implements ItemPredicate {
  public static final RecordLoadable<ContextItemPredicate> LOADER = RecordLoadable.create(StringLoadable.DEFAULT.requiredField("key", ContextItemPredicate::key), ContextItemPredicate::new);
  /** Context map */
  private static final Map<String, Predicate<Item>> CONTEXT = new ConcurrentHashMap<>();

  @Override
  public boolean matches(Item item) {
    Predicate<Item> predicate = CONTEXT.get(key);
    return predicate != null && predicate.test(item);
  }

  @Override
  public RecordLoadable<? extends IJsonPredicate<Item>> getLoader() {
    return LOADER;
  }

  /** Adds the given context to the map */
  public static void provideContext(String key, Predicate<Item> predicate) {
    CONTEXT.put(key, predicate);
  }

  /** Removes the given context to the map */
  public static void removeContext(String key) {
    CONTEXT.remove(key);
  }
}
