package slimeknights.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractEnchantmentToModifierProvider;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class EnchantmentToModifierProvider extends AbstractEnchantmentToModifierProvider {
  public EnchantmentToModifierProvider(PackOutput packOutput) {
    super(packOutput);
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
    add(Enchantments.THORNS, ModifierIds.thorns);
    add(Enchantments.DEPTH_STRIDER, ModifierIds.depthStrider);
    add(Enchantments.FROST_WALKER, ModifierIds.frostWalker);
    add(Enchantments.SOUL_SPEED, ModifierIds.soulspeed);
    add(Enchantments.SWIFT_SNEAK, ModifierIds.swiftSneak);

    // melee
    add(Enchantments.SHARPNESS, ModifierIds.sharpness);
    add(Enchantments.SMITE, ModifierIds.smite);
    add(Enchantments.BANE_OF_ARTHROPODS, ModifierIds.baneOfSssss);
    add(Enchantments.KNOCKBACK, ModifierIds.knockback);
    add(Enchantments.FIRE_ASPECT, ModifierIds.fiery);
    add(Enchantments.MOB_LOOTING, ModifierIds.luck);
    add(Enchantments.SWEEPING_EDGE, ModifierIds.sweeping);
    add(Enchantments.IMPALING, ModifierIds.antiaquatic);

    // harvest
    add(Enchantments.BLOCK_EFFICIENCY, ModifierIds.haste);
    add(Enchantments.SILK_TOUCH, ModifierIds.silky);
    add(Enchantments.BLOCK_FORTUNE, ModifierIds.luck);

    // ranged
    add(Enchantments.POWER_ARROWS, ModifierIds.power);
    add(Enchantments.PUNCH_ARROWS, ModifierIds.punch);
    add(Enchantments.FLAMING_ARROWS, ModifierIds.fiery);
    add(Enchantments.INFINITY_ARROWS, ModifierIds.crystalshot);
    add(Enchantments.MULTISHOT, ModifierIds.multishot);
    add(Enchantments.QUICK_CHARGE, ModifierIds.quickCharge);
    add(Enchantments.PIERCING, ModifierIds.arrowPierce);

    // fishing
    add(Enchantments.FISHING_LUCK, ModifierIds.luck);
    add(Enchantments.FISHING_SPEED, ModifierIds.lure);

    // trident
    add(Enchantments.LOYALTY, ModifierIds.returning);
    add(Enchantments.CHANNELING, ModifierIds.channeling);
    add(Enchantments.RIPTIDE, ModifierIds.drillAttack);

    // tag compat
    // upgrade
    addCompat(ModifierIds.experienced);
    addCompat(ModifierIds.killager);
    addCompat(ModifierIds.magnetic);
    addCompat(ModifierIds.necrotic);
    addCompat(TinkerModifiers.severing.getId());
    addCompat(ModifierIds.stepUp);
    addCompat(ModifierIds.soulbound);
    addCompat(ModifierIds.trueshot);
    addCompat(ModifierIds.freezing);
    addCompat(ModifierIds.fiery);

    // defense
    addCompat(ModifierIds.knockbackResistance);
    addCompat(ModifierIds.magicProtection);
    addCompat(ModifierIds.revitalizing);

    // ability
    addCompat(ModifierIds.autosmelt);
    addCompat(ModifierIds.doubleJump);
    addCompat(ModifierIds.expanded);
    addCompat(ModifierIds.luck);
    addCompat(ModifierIds.multishot);
    addCompat(ModifierIds.reach);
    addCompat(ModifierIds.tilling);
    addCompat(ModifierIds.reflecting);
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
