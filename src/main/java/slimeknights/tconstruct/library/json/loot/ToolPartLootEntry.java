package slimeknights.tconstruct.library.json.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.loadable.LoadableCodec;
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
  public static final MapCodec<ToolPartLootEntry> CODEC = RecordCodecBuilder.mapCodec(
    instance -> instance.group(
      TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(entry -> entry.tag),
      new LoadableCodec<>(RandomMaterial.LOADER).fieldOf("material").forGetter(entry -> entry.material)
    ).and(singletonFields(instance)).apply(instance, ToolPartLootEntry::new)
  );
  private final TagKey<Item> tag;
  private final RandomMaterial material;

  protected ToolPartLootEntry(TagKey<Item> tag, RandomMaterial material, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
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

}
