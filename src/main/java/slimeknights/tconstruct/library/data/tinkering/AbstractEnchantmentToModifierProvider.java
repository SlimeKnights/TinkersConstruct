package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.io.IOException;
import java.util.Objects;

/** Data generator for mappings from enchantments to modifiers */
public abstract class AbstractEnchantmentToModifierProvider extends GenericDataProvider {
  /** Compiled JSON to save, no need to do anything fancier, it already does merging for us */
  private final JsonObject enchantmentMap = new JsonObject();

  public AbstractEnchantmentToModifierProvider(DataGenerator generator) {
    super(generator, PackType.SERVER_DATA, "tinkering");
  }

  /** Add any mappings */
  protected abstract void addEnchantmentMappings();

  @Override
  public void run(HashCache pCache) throws IOException {
    enchantmentMap.entrySet().clear();
    addEnchantmentMappings();
    saveThing(pCache, TConstruct.getResource("enchantments_to_modifiers"), enchantmentMap);
  }

  /* Helpers */

  /** Adds the given enchantment */
  protected void add(Enchantment enchantment, ModifierId modifierId) {
    String key = Objects.requireNonNull(enchantment.getRegistryName()).toString();
    if (enchantmentMap.has(key)) {
      throw new IllegalArgumentException("Duplicate enchantment " + key);
    }
    enchantmentMap.addProperty(key, modifierId.toString());
  }

  /** Adds the given enchantment tag */
  protected void add(TagKey<Enchantment> tag, ModifierId modifierId) {
    String key = "#" + tag.location();
    if (enchantmentMap.has(key)) {
      throw new IllegalArgumentException("Duplicate enchantment tag " + tag.location());
    }
    enchantmentMap.addProperty(key, modifierId.toString());
  }

  /** Adds the given enchantment tag */
  protected void add(ResourceLocation tag, ModifierId modifierId) {
    add(TagKey.create(Registry.ENCHANTMENT_REGISTRY, tag), modifierId);
  }
}
