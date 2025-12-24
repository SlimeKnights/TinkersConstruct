package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.AOE_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BLOCK_WHILE_CHARGING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BONUS_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.COSMETIC_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DAMAGE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DRILL_ATTACKS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DUAL_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_UPGRADE_BLACKLIST;
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
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.OVERSLIME_FRIEND;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.PROTECTION_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.REMOVE_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SHIELD_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLIME_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SPECIAL_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.UPGRADES;

public class ModifierTagProvider extends AbstractModifierTagProvider {
  public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(GEMS).add(ModifierIds.diamond, ModifierIds.emerald);
    tag(INVISIBLE_INK_BLACKLIST).add(
      TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.trim.getId(),
      TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId(),
      ModifierIds.shiny, TinkerModifiers.golden.getId()
    );
    tag(REMOVE_MODIFIER_BLACKLIST).add(TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId());
    tag(EXTRACT_MODIFIER_BLACKLIST).add(
      TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.trim.getId(),
      ModifierIds.rebalanced, ModifierIds.redirected
    ).addTag(REMOVE_MODIFIER_BLACKLIST);
    // blacklist modifiers that are not really slotless, they just have a slotless recipe
    tag(EXTRACT_SLOTLESS_BLACKLIST).add(ModifierIds.luck, ModifierIds.toolBelt);
    tag(EXTRACT_UPGRADE_BLACKLIST);

    // modifiers in this tag support both left click and right click interaction
    tag(DUAL_INTERACTION).add(
      TinkerModifiers.bucketing.getId(), TinkerModifiers.splashing.getId(),
      ModifierIds.glowing, TinkerModifiers.firestarter.getId(),
      ModifierIds.stripping, ModifierIds.tilling, ModifierIds.pathing,
      TinkerModifiers.shears.getId(), TinkerModifiers.harvest.getId(), ModifierIds.fishing,
      ModifierIds.pockets
    );
    tag(BLOCK_WHILE_CHARGING).add(
      TinkerModifiers.flinging.getId(), TinkerModifiers.springing.getId(), TinkerModifiers.bonking.getId(), TinkerModifiers.warping.getId(),
      TinkerModifiers.spitting.getId(), ModifierIds.scope, ModifierIds.zoom, ModifierIds.brushing, ModifierIds.throwing
    );
    tag(SLIME_DEFENSE).add(
      ModifierIds.meleeProtection, ModifierIds.projectileProtection,
      ModifierIds.fireProtection, ModifierIds.magicProtection,
      ModifierIds.blastProtection
    );
    tag(OVERSLIME_FRIEND).add(
      ModifierIds.overgrowth, ModifierIds.overcast, ModifierIds.overshield, ModifierIds.overburn, ModifierIds.overlord,
      ModifierIds.overforced, ModifierIds.overslimeFriend, TinkerModifiers.overworked.getId()
    );
    tag(AOE_INTERACTION).add(ModifierIds.pathing, ModifierIds.stripping, ModifierIds.tilling, ModifierIds.brushing, TinkerModifiers.splashing.getId(), TinkerModifiers.harvest.getId());
    tag(CHARGE_EMPTY_BOW_WITH_DRAWTIME).add(TinkerModifiers.flinging.getId(), TinkerModifiers.springing.getId(), TinkerModifiers.bonking.getId(), TinkerModifiers.warping.getId(), ModifierIds.throwing);
    tag(CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME).add(TinkerModifiers.blocking.getId(), ModifierIds.scope, ModifierIds.zoom);
    tag(DRILL_ATTACKS).add(TinkerModifiers.flinging.getId(), TinkerModifiers.springing.getId(), ModifierIds.grapple);

    // book tags
    this.tag(UPGRADES).addTag(GENERAL_UPGRADES, MELEE_UPGRADES, DAMAGE_UPGRADES, HARVEST_UPGRADES, ARMOR_UPGRADES, RANGED_UPGRADES);
    this.tag(ARMOR_UPGRADES).addTag(GENERAL_ARMOR_UPGRADES, HELMET_UPGRADES, CHESTPLATE_UPGRADES, LEGGING_UPGRADES, BOOT_UPGRADES);
    this.tag(ABILITIES).addTag(GENERAL_ABILITIES, INTERACTION_ABILITIES, MELEE_ABILITIES, HARVEST_ABILITIES, ARMOR_ABILITIES, RANGED_ABILITIES);
    this.tag(ARMOR_ABILITIES).addTag(GENERAL_ARMOR_ABILITIES, HELMET_ABILITIES, CHESTPLATE_ABILITIES, LEGGING_ABILITIES, BOOT_ABILITIES, SHIELD_ABILITIES);
    this.tag(DEFENSE).addTag(PROTECTION_DEFENSE, SPECIAL_DEFENSE);
    this.tag(SLOTLESS).addTag(GENERAL_SLOTLESS, BONUS_SLOTLESS, COSMETIC_SLOTLESS);

    // upgrades
    this.tag(GENERAL_UPGRADES).add(
      ModifierIds.diamond, ModifierIds.emerald, ModifierIds.netherite,
      ModifierIds.reinforced, ModifierIds.overforced, ModifierIds.soulbound,
      ModifierIds.experienced, TinkerModifiers.magnetic.getId(), ModifierIds.scope, ModifierIds.zoom,
      ModifierIds.tank, ModifierIds.smelting, TinkerModifiers.fireprimer.getId())
        .addOptional(ModifierIds.theOneProbe);

    this.tag(MELEE_UPGRADES).add(
      TinkerModifiers.knockback.getId(), TinkerModifiers.padded.getId(),
      TinkerModifiers.severing.getId(), TinkerModifiers.necrotic.getId(), TinkerModifiers.sweeping.getId(),
      ModifierIds.fiery, ModifierIds.freezing);
    this.tag(DAMAGE_UPGRADES).add(
      ModifierIds.sharpness, ModifierIds.pierce, ModifierIds.swiftstrike,
      ModifierIds.antiaquatic, ModifierIds.baneOfSssss, ModifierIds.cooling, ModifierIds.killager, ModifierIds.smite);

    this.tag(HARVEST_UPGRADES).add(ModifierIds.haste, ModifierIds.blasting, ModifierIds.hydraulic, ModifierIds.lightspeed);

    this.tag(GENERAL_ARMOR_UPGRADES).add(
      ModifierIds.fiery, ModifierIds.freezing, ModifierIds.thorns,
      ModifierIds.ricochet, ModifierIds.springy);
    this.tag(HELMET_UPGRADES).add(TinkerModifiers.itemFrame.getId(), ModifierIds.respiration, ModifierIds.minimap).addOptional(ModifierIds.headlight);
    this.tag(CHESTPLATE_UPGRADES).add(ModifierIds.haste, TinkerModifiers.knockback.getId(), TinkerModifiers.sleeves.getId());
    this.tag(LEGGING_UPGRADES).add(ModifierIds.leaping, TinkerModifiers.shieldStrap.getId(), ModifierIds.speedy, ModifierIds.swiftSneak, ModifierIds.stepUp);
    this.tag(BOOT_UPGRADES).add(ModifierIds.depthStrider, ModifierIds.featherFalling, ModifierIds.longFall, ModifierIds.lightspeed, TinkerModifiers.soulspeed.getId());

    this.tag(RANGED_UPGRADES).add(
      ModifierIds.pierce, ModifierIds.power, ModifierIds.punch, ModifierIds.quickCharge,
      TinkerModifiers.sinistral.getId(), ModifierIds.trueshot,
      ModifierIds.fiery, ModifierIds.freezing,
      ModifierIds.arrowPierce, ModifierIds.bounce, TinkerModifiers.necrotic.getId(),
      ModifierIds.lure, ModifierIds.collecting, ModifierIds.fins);

    // abilities
    this.tag(GENERAL_ABILITIES).add(
      TinkerModifiers.expanded.getId(), ModifierIds.gilded, TinkerModifiers.unbreakable.getId(),
      ModifierIds.luck, TinkerModifiers.melting.getId());
    this.tag(MELEE_ABILITIES).add(
      TinkerModifiers.blocking.getId(), TinkerModifiers.parrying.getId(),
      TinkerModifiers.dualWielding.getId(), ModifierIds.spilling);
    this.tag(HARVEST_ABILITIES).add(TinkerModifiers.autosmelt.getId(), TinkerModifiers.exchanging.getId(), TinkerModifiers.silky.getId());
    this.tag(RANGED_ABILITIES).add(
      ModifierIds.bulkQuiver, ModifierIds.trickQuiver,
      ModifierIds.crystalshot, TinkerModifiers.multishot.getId(), ModifierIds.ballista,
      ModifierIds.grapple,
      ModifierIds.channeling, ModifierIds.returning);
    this.tag(INTERACTION_ABILITIES).add(
      TinkerModifiers.bucketing.getId(), TinkerModifiers.firestarter.getId(), ModifierIds.glowing,
      ModifierIds.pathing, ModifierIds.stripping, ModifierIds.tilling, ModifierIds.brushing,
      TinkerModifiers.spitting.getId(), TinkerModifiers.splashing.getId(), TinkerModifiers.slurping.getId(),
      TinkerModifiers.bonking.getId(), TinkerModifiers.flinging.getId(), TinkerModifiers.springing.getId(), TinkerModifiers.warping.getId(),
      ModifierIds.throwing, ModifierIds.drillAttack);
    // armor
    this.tag(GENERAL_ARMOR_ABILITIES).add(ModifierIds.protection, TinkerModifiers.bursting.getId(), TinkerModifiers.wetting.getId());
    this.tag(HELMET_ABILITIES).add(ModifierIds.aquaAffinity, TinkerModifiers.slurping.getId());
    this.tag(CHESTPLATE_ABILITIES).add(TinkerModifiers.ambidextrous.getId(), ModifierIds.reach, ModifierIds.strength, ModifierIds.wings);
    this.tag(LEGGING_ABILITIES).add(ModifierIds.pockets, ModifierIds.soulBelt, ModifierIds.toolBelt, ModifierIds.craftingTable);
    this.tag(BOOT_ABILITIES).add(
      ModifierIds.bouncy, ModifierIds.doubleJump,
      TinkerModifiers.flamewake.getId(), ModifierIds.snowdrift, ModifierIds.tilling, ModifierIds.pathing, ModifierIds.frostWalker, ModifierIds.glowing);
    this.tag(SHIELD_ABILITIES).add(ModifierIds.boundless, TinkerModifiers.reflecting.getId());

    // defense
    this.tag(PROTECTION_DEFENSE).add(
      ModifierIds.blastProtection, ModifierIds.fireProtection, ModifierIds.magicProtection,
      ModifierIds.meleeProtection, ModifierIds.projectileProtection,
      ModifierIds.dragonborn, ModifierIds.shulking, ModifierIds.turtleShell);
    this.tag(SPECIAL_DEFENSE).add(ModifierIds.knockbackResistance, ModifierIds.revitalizing);

    // slotless
    this.tag(GENERAL_SLOTLESS).add(
      TinkerModifiers.overslime.getId(), ModifierIds.worldbound,
      ModifierIds.offhanded, ModifierIds.workbench,
      ModifierIds.blindshot, ModifierIds.barebow);
    this.tag(BONUS_SLOTLESS).add(
      ModifierIds.draconic, ModifierIds.rebalanced, ModifierIds.redirected,
      ModifierIds.harmonious, ModifierIds.recapitated, ModifierIds.forecast, ModifierIds.writable)
      .addOptional(ModifierIds.embossed);
    this.tag(COSMETIC_SLOTLESS).add(
      ModifierIds.shiny,
      TinkerModifiers.dyed.getId(), TinkerModifiers.embellishment.getId(), TinkerModifiers.trim.getId(),
      TinkerModifiers.farsighted.getId(), TinkerModifiers.nearsighted.getId());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Tag Provider";
  }
}
