package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.DamageTypeTags.AVOIDS_GUARDIAN_THORNS;
import static net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR;
import static net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS;
import static net.minecraft.tags.DamageTypeTags.IS_EXPLOSION;
import static net.minecraft.tags.DamageTypeTags.IS_FALL;
import static net.minecraft.tags.DamageTypeTags.IS_FIRE;
import static net.minecraft.tags.DamageTypeTags.IS_LIGHTNING;
import static net.minecraft.tags.DamageTypeTags.IS_PROJECTILE;
import static net.minecraft.tags.DamageTypeTags.WITCH_RESISTANT_TO;
import static net.minecraft.world.damagesource.DamageTypes.CRAMMING;
import static net.minecraft.world.damagesource.DamageTypes.DRAGON_BREATH;
import static net.minecraft.world.damagesource.DamageTypes.FALLING_ANVIL;
import static net.minecraft.world.damagesource.DamageTypes.FALLING_STALACTITE;
import static net.minecraft.world.damagesource.DamageTypes.FLY_INTO_WALL;
import static net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK;
import static net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK_NO_AGGRO;
import static net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK;
import static net.minecraft.world.damagesource.DamageTypes.STING;
import static net.minecraft.world.damagesource.DamageTypes.WITHER;
import static net.minecraft.world.damagesource.DamageTypes.WITHER_SKULL;
import static slimeknights.tconstruct.common.TinkerDamageTypes.BLEEDING;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_FIRE;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_MAGIC;
import static slimeknights.tconstruct.common.TinkerDamageTypes.PIERCING;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SELF_DESTRUCT;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SMELTERY_HEAT;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SMELTERY_MAGIC;
import static slimeknights.tconstruct.common.TinkerDamageTypes.WATER;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.BLAST_PROTECTION;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.FALL_PROTECTION;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.FIRE_PROTECTION;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.MAGIC_PROTECTION;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.MELEE_PROTECTION;
import static slimeknights.tconstruct.common.TinkerTags.DamageTypes.PROJECTILE_PROTECTION;

public class DamageTypeTagProvider extends DamageTypeTagsProvider {
  public DamageTypeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, lookup, TConstruct.MOD_ID, existingFileHelper);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void addTags(Provider pProvider) {
    tag(IS_FIRE).add(SMELTERY_HEAT).add(FLUID_FIRE.values());
    tag(IS_EXPLOSION).add(SELF_DESTRUCT);
    tag(WITCH_RESISTANT_TO).add(SMELTERY_MAGIC).add(FLUID_MAGIC.values());
    tag(BYPASSES_ARMOR).add(PIERCING, SELF_DESTRUCT, BLEEDING).add(WATER.values());
    tag(BYPASSES_ENCHANTMENTS).add(BLEEDING);
    tag(AVOIDS_GUARDIAN_THORNS).add(BLEEDING);
    // whole reason these are a pair is so we can tag one as projectile
    tag(IS_PROJECTILE).add(FLUID_FIRE.ranged(), FLUID_MAGIC.ranged(), WATER.ranged());

    // protection modifier tags
    tag(MELEE_PROTECTION).addTag(FALL_PROTECTION).add(PLAYER_ATTACK, MOB_ATTACK, MOB_ATTACK_NO_AGGRO, CRAMMING, STING);
    tag(PROJECTILE_PROTECTION).addTag(IS_PROJECTILE).add(FALLING_ANVIL, FALLING_ANVIL, FALLING_STALACTITE);
    tag(FIRE_PROTECTION).addTags(IS_FIRE, IS_LIGHTNING);
    tag(BLAST_PROTECTION).addTag(IS_EXPLOSION);
    tag(MAGIC_PROTECTION).addTag(WITCH_RESISTANT_TO).add(WITHER, WITHER_SKULL, DRAGON_BREATH);
    tag(FALL_PROTECTION).addTag(IS_FALL).add(FLY_INTO_WALL);
  }
}
