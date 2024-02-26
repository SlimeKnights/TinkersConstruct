package slimeknights.tconstruct.library.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;

/**
 * Loot table condition to test if a tag has entries.
 * TODO: is this redundant to {@link slimeknights.mantle.recipe.helper.TagEmptyCondition}? Or worth keeping both?
 * TODO 1.19: move to {@code slimeknights.tconsturct.library.json.condition}
 */
@RequiredArgsConstructor
public class TagNotEmptyLootCondition<T> implements LootItemCondition, ICondition {
  private static final ResourceLocation NAME = TConstruct.getResource("tag_not_empty");
  private final TagKey<T> tag;

  @Override
  public LootItemConditionType getType() {
    return TinkerCommons.lootTagNotEmptyCondition.get();
  }

  @Override
  public ResourceLocation getID() {
    return NAME;
  }

  @Override
  public boolean test() {
    Registry<T> registry = RegistryHelper.getRegistry(tag.registry());
    return registry != null && registry.getTagOrEmpty(tag).iterator().hasNext();
  }

  @Override
  public boolean test(IContext context) {
    // if the context has no tags, fallback to registry test
    // helps using this in book conditions
    if (context.getAllTags(tag.registry()).isEmpty()) {
      return test();
    }
    return !context.getTag(tag).getValues().isEmpty();
  }

  @Override
  public boolean test(LootContext context) {
    return test();
  }


  public static class ConditionSerializer implements Serializer<TagNotEmptyLootCondition<?>>, IConditionSerializer<TagNotEmptyLootCondition<?>> {
    /** Helper to deal with generics */
    private static <T> TagKey<T> createKey(JsonObject json) {
      ResourceKey<? extends Registry<T>> registry = ResourceKey.createRegistryKey(JsonHelper.getResourceLocation(json, "registry"));
      return TagKey.create(registry, JsonHelper.getResourceLocation(json, "tag"));
    }

    @Override
    public void write(JsonObject json, TagNotEmptyLootCondition<?> value) {
      json.addProperty("registry", value.tag.registry().location().toString());
      json.addProperty("tag", value.tag.location().toString());
    }

    @Override
    public void serialize(JsonObject json, TagNotEmptyLootCondition<?> value, JsonSerializationContext context) {
      write(json, value);
    }

    @Override
    public TagNotEmptyLootCondition<?> read(JsonObject json) {
      return new TagNotEmptyLootCondition<>(createKey(json));
    }

    @Override
    public TagNotEmptyLootCondition<?> deserialize(JsonObject json, JsonDeserializationContext context) {
      return read(json);
    }

    @Override
    public ResourceLocation getID() {
      return NAME;
    }
  }
}
