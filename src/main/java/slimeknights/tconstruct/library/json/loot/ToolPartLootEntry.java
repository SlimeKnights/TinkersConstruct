package slimeknights.tconstruct.library.json.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/** Entry for a random tool part from a list with a random material */
public class ToolPartLootEntry extends LootPoolSingletonContainer {
  private final TagKey<Item> tag;
  private final RandomMaterial material;

  protected ToolPartLootEntry(TagKey<Item> tag, RandomMaterial material, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
    super(weight, quality, conditions, functions);
    this.tag = tag;
    this.material = material;
  }

  @Override
  public LootPoolEntryType getType() {
    return TinkerToolParts.toolPartLootEntry.get();
  }

  @Override
  protected void createItemStack(Consumer<ItemStack> consumer, LootContext context) {
    List<IToolPart> options = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, tag)
      .flatMap(item -> item instanceof IToolPart mat ? Stream.of(mat) : Stream.empty()).toList();
    if (!options.isEmpty()) {
      RandomSource random = context.getRandom();
      IToolPart choice = options.get(random.nextInt(options.size()));
      MaterialVariantId material = this.material.getMaterial(choice.getStatType(), random);
      if (choice.canUseMaterial(material.getId())) {
        consumer.accept(choice.withMaterial(material));
      }
    }
  }


  /* Builders */

  /** Creates a builder with the given material */
  public static LootPoolSingletonContainer.Builder<?> entry(TagKey<Item> tag, RandomMaterial material) {
    return simpleBuilder((weight, quality, conditions, functions) -> new ToolPartLootEntry(tag, material, weight, quality, conditions, functions));
  }

  /** Creates a builder for a fixed material */
  public static LootPoolSingletonContainer.Builder<?> fixed(TagKey<Item> tag, MaterialVariantId material) {
    return entry(tag, RandomMaterial.fixed(material));
  }

  /** Serializer logic */
  public static class Serializer extends LootPoolSingletonContainer.Serializer<ToolPartLootEntry> {
    @Override
    public void serializeCustom(JsonObject json, ToolPartLootEntry object, JsonSerializationContext conditions) {
      super.serializeCustom(json, object, conditions);
      json.addProperty("tag", object.tag.location().toString());
      json.add("material", RandomMaterial.LOADER.serialize(object.material));
    }

    @Override
    protected ToolPartLootEntry deserialize(JsonObject json, JsonDeserializationContext context, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
      TagKey<Item> tag = Loadables.ITEM_TAG.getIfPresent(json, "tag");
      RandomMaterial material = RandomMaterial.LOADER.getIfPresent(json, "material");
      return new ToolPartLootEntry(tag, material, weight, quality, conditions, functions);
    }
  }
}
