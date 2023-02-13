package slimeknights.tconstruct.library.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.shared.TinkerCommons;

/** Loot table condition to test if a tag has entries */
@RequiredArgsConstructor
public class TagNotEmptyLootCondition<T> implements LootItemCondition {
  private final TagKey<T> tag;

  @Override
  public LootItemConditionType getType() {
    return TinkerCommons.lootTagNotEmptyCondition.get();
  }

  @Override
  public boolean test(LootContext context) {
    Registry<T> registry = RegistryHelper.getRegistry(tag.registry());
    return registry != null && registry.getTagOrEmpty(tag).iterator().hasNext();
  }

  public static class ConditionSerializer implements Serializer<TagNotEmptyLootCondition<?>> {
    /** Helper to deal with generics */
    private static <T> TagKey<T> createKey(JsonObject json) {
      ResourceKey<? extends Registry<T>> registry = ResourceKey.createRegistryKey(JsonHelper.getResourceLocation(json, "registry"));
      return TagKey.create(registry, JsonHelper.getResourceLocation(json, "tag"));
    }

    @Override
    public void serialize(JsonObject json, TagNotEmptyLootCondition<?> value, JsonSerializationContext context) {
      json.addProperty("registry", value.tag.registry().location().toString());
      json.addProperty("tag", value.tag.location().toString());
    }

    @Override
    public TagNotEmptyLootCondition<?> deserialize(JsonObject json, JsonDeserializationContext context) {
      return new TagNotEmptyLootCondition<>(createKey(json));
    }
  }
}
