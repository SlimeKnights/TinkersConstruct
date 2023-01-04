package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.data.tinkering.AbstractEnchantmentToModifierProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class EnchantmentToModifierProvider extends AbstractEnchantmentToModifierProvider {
  public EnchantmentToModifierProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void addEnchantmentMappings() {
    // general
    add(Enchantments.UNBREAKING, TinkerModifiers.reinforced.getId());

    // protection
    add(Enchantments.ALL_DAMAGE_PROTECTION, TinkerModifiers.protection.getId());
    add(Enchantments.FIRE_PROTECTION, TinkerModifiers.fireProtection.getId());
    add(Enchantments.BLAST_PROTECTION, TinkerModifiers.blastProtection.getId());
    add(Enchantments.PROJECTILE_PROTECTION, TinkerModifiers.projectileProtection.getId());
    add(Enchantments.FALL_PROTECTION, TinkerModifiers.featherFalling.getId());
    // misc armor
    add(Enchantments.RESPIRATION, TinkerModifiers.respiration.getId());
    add(Enchantments.AQUA_AFFINITY, TinkerModifiers.aquaAffinity.getId());
    add(Enchantments.THORNS, TinkerModifiers.thorns.getId());
    // TODO: depth strider
    add(Enchantments.FROST_WALKER, TinkerModifiers.frostWalker.getId());
    add(Enchantments.SOUL_SPEED, TinkerModifiers.soulspeed.getId());

    // melee
    add(Enchantments.SHARPNESS, ModifierIds.sharpness);
    add(Enchantments.SMITE, ModifierIds.smite);
    add(Enchantments.BANE_OF_ARTHROPODS, ModifierIds.baneOfSssss);
    add(Enchantments.KNOCKBACK, TinkerModifiers.knockback.getId());
    add(Enchantments.FIRE_ASPECT, TinkerModifiers.fiery.getId());
    add(Enchantments.MOB_LOOTING, ModifierIds.looting);
    add(Enchantments.SWEEPING_EDGE, TinkerModifiers.sweeping.getId());
    add(Enchantments.IMPALING, ModifierIds.antiaquatic);

    // harvest
    add(Enchantments.BLOCK_EFFICIENCY, TinkerModifiers.haste.getId());
    add(Enchantments.SILK_TOUCH, TinkerModifiers.silky.getId());
    add(Enchantments.BLOCK_FORTUNE, ModifierIds.fortune);

    // ranged
    add(Enchantments.POWER_ARROWS, ModifierIds.power);
    add(Enchantments.PUNCH_ARROWS, TinkerModifiers.punch.getId());
    add(Enchantments.FLAMING_ARROWS, TinkerModifiers.fiery.getId());
    add(Enchantments.INFINITY_ARROWS, TinkerModifiers.crystalshot.getId());
    add(Enchantments.MULTISHOT, TinkerModifiers.multishot.getId());
    add(Enchantments.QUICK_CHARGE, ModifierIds.quickCharge);
    add(Enchantments.PIERCING, TinkerModifiers.impaling.getId());

    // TODO: tags for common mod added enchantments
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Enchantment to Modifier Mapping";
  }
}
