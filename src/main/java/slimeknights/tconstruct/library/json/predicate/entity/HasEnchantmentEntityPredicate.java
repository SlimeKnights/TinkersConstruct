package slimeknights.tconstruct.library.json.predicate.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.RegistryEntryLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

/**
 * Predicate that checks if the given entity has the given enchantment on any of their equipment
 */
public record HasEnchantmentEntityPredicate(Enchantment enchantment) implements LivingEntityPredicate {
  public static final IGenericLoader<HasEnchantmentEntityPredicate> LOADER = new RegistryEntryLoader<>("enchantment", ForgeRegistries.ENCHANTMENTS, HasEnchantmentEntityPredicate::new, HasEnchantmentEntityPredicate::enchantment);

  @Override
  public boolean matches(LivingEntity entity) {
    return EnchantmentHelper.getEnchantmentLevel(enchantment, entity) > 0;
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<LivingEntity>> getLoader() {
    return LOADER;
  }
}
