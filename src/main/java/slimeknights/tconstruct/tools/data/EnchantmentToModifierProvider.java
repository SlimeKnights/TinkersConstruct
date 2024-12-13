package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractEnchantmentToModifierProvider;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class EnchantmentToModifierProvider extends AbstractEnchantmentToModifierProvider {
  public EnchantmentToModifierProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void addEnchantmentMappings() {
    // general
    add(Enchantments.UNBREAKING, ModifierIds.reinforced);

    // protection
    add(Enchantments.ALL_DAMAGE_PROTECTION, ModifierIds.protection);
    add(Enchantments.FIRE_PROTECTION, ModifierIds.fireProtection);
    add(Enchantments.BLAST_PROTECTION, ModifierIds.blastProtection);
    add(Enchantments.PROJECTILE_PROTECTION, ModifierIds.projectileProtection);
    add(Enchantments.FALL_PROTECTION, ModifierIds.featherFalling);
    // misc armor
    add(Enchantments.RESPIRATION, ModifierIds.respiration);
    add(Enchantments.AQUA_AFFINITY, ModifierIds.aquaAffinity);
    add(Enchantments.THORNS, TinkerModifiers.thorns.getId());
    add(Enchantments.DEPTH_STRIDER, ModifierIds.depthStrider);
    add(Enchantments.FROST_WALKER, ModifierIds.frostWalker);
    add(Enchantments.SOUL_SPEED, TinkerModifiers.soulspeed.getId());
    add(Enchantments.SWIFT_SNEAK, ModifierIds.swiftSneak);

    // melee
    add(Enchantments.SHARPNESS, ModifierIds.sharpness);
    add(Enchantments.SMITE, ModifierIds.smite);
    add(Enchantments.BANE_OF_ARTHROPODS, ModifierIds.baneOfSssss);
    add(Enchantments.KNOCKBACK, TinkerModifiers.knockback.getId());
    add(Enchantments.FIRE_ASPECT, TinkerModifiers.fiery.getId());
    add(Enchantments.MOB_LOOTING, ModifierIds.luck);
    add(Enchantments.SWEEPING_EDGE, TinkerModifiers.sweeping.getId());
    add(Enchantments.IMPALING, ModifierIds.antiaquatic);

    // harvest
    add(Enchantments.BLOCK_EFFICIENCY, ModifierIds.haste);
    add(Enchantments.SILK_TOUCH, TinkerModifiers.silky.getId());
    add(Enchantments.BLOCK_FORTUNE, ModifierIds.luck);

    // ranged
    add(Enchantments.POWER_ARROWS, ModifierIds.power);
    add(Enchantments.PUNCH_ARROWS, TinkerModifiers.punch.getId());
    add(Enchantments.FLAMING_ARROWS, TinkerModifiers.fiery.getId());
    add(Enchantments.INFINITY_ARROWS, TinkerModifiers.crystalshot.getId());
    add(Enchantments.MULTISHOT, TinkerModifiers.multishot.getId());
    add(Enchantments.QUICK_CHARGE, ModifierIds.quickCharge);
    add(Enchantments.PIERCING, TinkerModifiers.impaling.getId());

    // tag compat
    // upgrade
    addCompat(ModifierIds.experienced);
    addCompat(ModifierIds.killager);
    addCompat(TinkerModifiers.magnetic.getId());
    addCompat(TinkerModifiers.necrotic.getId());
    addCompat(TinkerModifiers.severing.getId());
    addCompat(ModifierIds.stepUp);
    addCompat(ModifierIds.soulbound);
    addCompat(ModifierIds.trueshot);

    // defense
    addCompat(ModifierIds.knockbackResistance);
    addCompat(ModifierIds.magicProtection);
    addCompat(ModifierIds.revitalizing);

    // ability
    addCompat(TinkerModifiers.autosmelt.getId());
    addCompat(TinkerModifiers.doubleJump.getId());
    addCompat(TinkerModifiers.expanded.getId());
    addCompat(ModifierIds.luck);
    addCompat(TinkerModifiers.multishot.getId());
    addCompat(ModifierIds.reach);
    addCompat(ModifierIds.tilling);
    addCompat(TinkerModifiers.reflecting.getId());
  }

  /** Adds a compat enchantment */
  private void addCompat(ModifierId modifier) {
    add(TConstruct.getResource("modifier_like/" + modifier.getPath()), modifier);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Enchantment to Modifier Mapping";
  }
}
