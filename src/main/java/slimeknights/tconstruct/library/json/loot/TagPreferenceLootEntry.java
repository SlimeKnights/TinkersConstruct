package slimeknights.tconstruct.library.json.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.recipe.helper.TagPreference;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.function.Consumer;

/** Loot entry that returns an item from a tag */
public class TagPreferenceLootEntry extends LootPoolSingletonContainer {
  private final TagKey<Item> tag;
  protected TagPreferenceLootEntry(TagKey<Item> tag, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
    super(weight, quality, conditions, functions);
    this.tag = tag;
  }

  @Override
  public LootPoolEntryType getType() {
    return TinkerCommons.lootTagPreference.get();
  }

  @Override
  protected void createItemStack(Consumer<ItemStack> consumer, LootContext context) {
    TagPreference.getPreference(tag).ifPresent(item -> consumer.accept(new ItemStack(item)));
  }

  /** Creates a new builder */
  public static LootPoolSingletonContainer.Builder<?> tagPreference(TagKey<Item> tag) {
    return simpleBuilder((weight, quality, conditions, functions) -> new TagPreferenceLootEntry(tag, weight, quality, conditions, functions));
  }

  public static class Serializer extends LootPoolSingletonContainer.Serializer<TagPreferenceLootEntry> {
    @Override
    public void serializeCustom(JsonObject json, TagPreferenceLootEntry object, JsonSerializationContext conditions) {
      super.serializeCustom(json, object, conditions);
      json.addProperty("tag", object.tag.location().toString());
    }

    @Override
    protected TagPreferenceLootEntry deserialize(JsonObject json, JsonDeserializationContext context, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
      TagKey<Item> tag = TagKey.create(Registries.ITEM, JsonHelper.getResourceLocation(json, "tag"));
      return new TagPreferenceLootEntry(tag, weight, quality, conditions, functions);
    }
  }
}
