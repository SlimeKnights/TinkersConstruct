package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BONUS_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DAMAGE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DUAL_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GEMS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HARVEST_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HARVEST_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HELMET_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HELMET_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.INTERACTION_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.INVISIBLE_INK_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.LEGGING_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.LEGGING_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELEE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELEE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.PROTECTION_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SHIELD_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLIME_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SPECIAL_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.UPGRADES;

public class ModifierTagProvider extends AbstractModifierTagProvider {
  public ModifierTagProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(GEMS).addOptional(ModifierIds.diamond, ModifierIds.emerald);
    tag(INVISIBLE_INK_BLACKLIST)
      .add(TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId())
      .addOptional(ModifierIds.shiny, TinkerModifiers.golden.getId());
    tag(EXTRACT_MODIFIER_BLACKLIST)
      .add(TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId());
    // blacklist modifiers that are not really slotless, they just have a slotless recipe
    tag(EXTRACT_SLOTLESS_BLACKLIST).add(ModifierIds.luck, ModifierIds.toolBelt);

    // modifiers in this tag support both left click and right click interaction
    tag(DUAL_INTERACTION)
      .add(TinkerModifiers.bucketing.getId(), TinkerModifiers.spilling.getId(),
           TinkerModifiers.glowing.getId(), TinkerModifiers.firestarter.getId(),
           ModifierIds.stripping, ModifierIds.tilling, ModifierIds.pathing,
           TinkerModifiers.shears.getId(), TinkerModifiers.harvest.getId())
      .addOptional(ModifierIds.pockets);
    tag(SLIME_DEFENSE)
      .add(TinkerModifiers.meleeProtection.getId(), TinkerModifiers.projectileProtection.getId(),
           ModifierIds.fireProtection, TinkerModifiers.magicProtection.getId(),
           TinkerModifiers.blastProtection.getId(), TinkerModifiers.golden.getId());

    // book tags
    this.tag(UPGRADES).addTag(GENERAL_UPGRADES, MELEE_UPGRADES, DAMAGE_UPGRADES, HARVEST_UPGRADES, ARMOR_UPGRADES, RANGED_UPGRADES);
    this.tag(ARMOR_UPGRADES).addTag(GENERAL_ARMOR_UPGRADES, HELMET_UPGRADES, CHESTPLATE_UPGRADES, LEGGING_UPGRADES, BOOT_UPGRADES);
    this.tag(ABILITIES).addTag(GENERAL_ABILITIES, INTERACTION_ABILITIES, MELEE_ABILITIES, HARVEST_ABILITIES, ARMOR_ABILITIES, RANGED_ABILITIES);
    this.tag(ARMOR_ABILITIES).addTag(GENERAL_ARMOR_ABILITIES, HELMET_ABILITIES, CHESTPLATE_ABILITIES, LEGGING_ABILITIES, BOOT_ABILITIES, SHIELD_ABILITIES);
    this.tag(DEFENSE).addTag(PROTECTION_DEFENSE, SPECIAL_DEFENSE);
    this.tag(SLOTLESS).addTag(GENERAL_SLOTLESS, BONUS_SLOTLESS);

    // upgrades
    this.tag(GENERAL_UPGRADES).add(
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, TinkerModifiers.overforced.getId(), TinkerModifiers.soulbound.getId(),
      TinkerModifiers.experienced.getId(), TinkerModifiers.magnetic.getId(), TinkerModifiers.zoom.getId(),
      TinkerModifiers.tank.getId(), TinkerModifiers.fireprimer.getId())
        .addOptional(ModifierIds.theOneProbe);

    this.tag(MELEE_UPGRADES).add(
      TinkerModifiers.knockback.getId(), TinkerModifiers.padded.getId(),
      TinkerModifiers.severing.getId(), TinkerModifiers.necrotic.getId(), TinkerModifiers.sweeping.getId(),
      TinkerModifiers.fiery.getId(), TinkerModifiers.freezing.getId());
    this.tag(DAMAGE_UPGRADES).add(
      ModifierIds.sharpness, ModifierIds.pierce, ModifierIds.swiftstrike,
      ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling, ModifierIds.killager, ModifierIds.smite);

    this.tag(HARVEST_UPGRADES).add(TinkerModifiers.haste.getId(), ModifierIds.blasting, ModifierIds.hydraulic, ModifierIds.lightspeed);

    this.tag(GENERAL_ARMOR_UPGRADES).add(
      TinkerModifiers.fiery.getId(), TinkerModifiers.freezing.getId(), TinkerModifiers.thorns.getId(),
      TinkerModifiers.ricochet.getId(), TinkerModifiers.springy.getId());
    this.tag(HELMET_UPGRADES).add(TinkerModifiers.itemFrame.getId(), ModifierIds.respiration);
    this.tag(CHESTPLATE_UPGRADES).add(TinkerModifiers.haste.getId(), TinkerModifiers.knockback.getId());
    this.tag(LEGGING_UPGRADES).add(TinkerModifiers.leaping.getId(), TinkerModifiers.shieldStrap.getId(), ModifierIds.speedy, ModifierIds.stepUp);
    this.tag(BOOT_UPGRADES).add(ModifierIds.depthStrider, ModifierIds.featherFalling, TinkerModifiers.lightspeedArmor.getId(), TinkerModifiers.soulspeed.getId());

    this.tag(RANGED_UPGRADES).add(
      ModifierIds.pierce, ModifierIds.power, TinkerModifiers.punch.getId(), ModifierIds.quickCharge,
      TinkerModifiers.scope.getId(), TinkerModifiers.sinistral.getId(), ModifierIds.trueshot,
      TinkerModifiers.fiery.getId(), TinkerModifiers.freezing.getId(),
      TinkerModifiers.impaling.getId(), TinkerModifiers.necrotic.getId());

    // abilities
    this.tag(GENERAL_ABILITIES).add(
      TinkerModifiers.expanded.getId(), ModifierIds.gilded, TinkerModifiers.unbreakable.getId(),
      ModifierIds.luck, TinkerModifiers.melting.getId());
    this.tag(MELEE_ABILITIES).add(
      TinkerModifiers.blocking.getId(), TinkerModifiers.parrying.getId(),
      TinkerModifiers.dualWielding.getId(), TinkerModifiers.spilling.getId());
    this.tag(HARVEST_ABILITIES).add(TinkerModifiers.autosmelt.getId(), TinkerModifiers.exchanging.getId(), TinkerModifiers.silky.getId());
    this.tag(RANGED_ABILITIES).add(
      TinkerModifiers.bulkQuiver.getId(), TinkerModifiers.trickQuiver.getId(),
      TinkerModifiers.crystalshot.getId(), TinkerModifiers.multishot.getId());
    this.tag(INTERACTION_ABILITIES).add(
      TinkerModifiers.bucketing.getId(), TinkerModifiers.firestarter.getId(), TinkerModifiers.glowing.getId(),
      ModifierIds.pathing, ModifierIds.stripping, ModifierIds.tilling,
      TinkerModifiers.spitting.getId(),
      TinkerModifiers.bonking.getId(), TinkerModifiers.flinging.getId(), TinkerModifiers.springing.getId(), TinkerModifiers.warping.getId());
    // armor
    this.tag(GENERAL_ARMOR_ABILITIES).add(ModifierIds.protection, TinkerModifiers.spilling.getId());
    this.tag(HELMET_ABILITIES).add(ModifierIds.aquaAffinity, TinkerModifiers.slurping.getId());
    this.tag(CHESTPLATE_ABILITIES).add(TinkerModifiers.ambidextrous.getId(), ModifierIds.reach, ModifierIds.strength);
    this.tag(LEGGING_ABILITIES).add(ModifierIds.pockets, ModifierIds.toolBelt, TinkerModifiers.wetting.getId());
    this.tag(BOOT_ABILITIES).add(
      TinkerModifiers.bouncy.getId(), TinkerModifiers.doubleJump.getId(), ModifierIds.longFall,
      TinkerModifiers.flamewake.getId(), ModifierIds.snowdrift, ModifierIds.plowing, ModifierIds.pathMaker, ModifierIds.frostWalker);
    this.tag(SHIELD_ABILITIES).add(TinkerModifiers.boundless.getId(), TinkerModifiers.reflecting.getId());

    // defense
    this.tag(PROTECTION_DEFENSE).add(
      TinkerModifiers.blastProtection.getId(), ModifierIds.fireProtection, TinkerModifiers.magicProtection.getId(),
      TinkerModifiers.meleeProtection.getId(), TinkerModifiers.projectileProtection.getId(),
      TinkerModifiers.dragonborn.getId(), TinkerModifiers.shulking.getId(), ModifierIds.turtleShell);
    this.tag(SPECIAL_DEFENSE).add(TinkerModifiers.golden.getId(), ModifierIds.knockbackResistance, ModifierIds.revitalizing);

    // slotless
    this.tag(GENERAL_SLOTLESS).add(
      TinkerModifiers.overslime.getId(), ModifierIds.shiny, ModifierIds.worldbound,
      TinkerModifiers.offhanded.getId(), ModifierIds.blindshot,
      TinkerModifiers.farsighted.getId(), TinkerModifiers.nearsighted.getId(),
      TinkerModifiers.dyed.getId(), TinkerModifiers.embellishment.getId());
    this.tag(BONUS_SLOTLESS).add(
      ModifierIds.draconic, ModifierIds.rebalanced,
      ModifierIds.harmonious, ModifierIds.recapitated, ModifierIds.resurrected, ModifierIds.writable);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Tag Provider";
  }
}
