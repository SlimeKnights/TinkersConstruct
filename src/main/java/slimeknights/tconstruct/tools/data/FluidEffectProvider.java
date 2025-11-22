package slimeknights.tconstruct.tools.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.BlockAtEntityPredicate;
import slimeknights.mantle.data.predicate.entity.HasMobEffectPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.recipe.data.FluidNameIngredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.FakeRegistryEntry;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.predicate.HarvestTierPredicate;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.GroupCost;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.BlockInteractFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.BreakBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MeltBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MoveBlocksFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.OffsetBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PotionCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.AddBreathFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.AwardStatFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.CureEffectsFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.EntityInteractFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.FireFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.FreezeFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.PotionFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.PushEntityFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RandomTeleportFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RemoveEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RestoreHungerFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.AreaMobEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ConditionalFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.DropItemFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ExplosionFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ScalingFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.SequenceFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.SetBlockFluidEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import slimeknights.tconstruct.world.block.DirtType;

import java.util.function.Function;

public class FluidEffectProvider extends AbstractFluidEffectProvider {
  public FluidEffectProvider(PackOutput packOutput) {
    super(packOutput, TConstruct.MOD_ID);
  }

  @Override
  protected void addFluids() {
    // cheap //
    // water
    addFluid(Fluids.WATER, FluidValues.SIP)
      .addDamage(LivingEntityPredicate.WATER_SENSITIVE, 2f, TinkerDamageTypes.WATER)
      .addEntityEffect(FluidEffect.EXTINGUISH_FIRE)
      .addBlockEffect(BlockPredicate.or(BlockPredicate.BLOCKS_MOTION, BlockPredicate.tag(TinkerTags.Blocks.UNREPLACABLE_BY_LIQUID)).inverted(), new BreakBlockFluidEffect(0));
    addFluid(TinkerFluids.powderedSnow, FluidType.BUCKET_VOLUME / 10)
      .coldDamage(2f)
      .addEntityEffect(new FreezeFluidEffect(TimeAction.ADD, 80))
      .addBlockEffect(new PlaceBlockFluidEffect(Blocks.SNOW));

    // fire
    addFluid(Fluids.LAVA, FluidType.BUCKET_VOLUME / 20)
      .fireDamage(1f)
      .addEntityEffect(new FireFluidEffect(TimeAction.ADD, 4))
      .placeFire();
    // blaze - more damage, less fire, and brighter
    addFluid(TinkerFluids.blazingBlood.getTag(), FluidType.BUCKET_VOLUME / 20)
      .fireDamage(2f)
      .addEntityEffect(new FireFluidEffect(TimeAction.ADD, 2))
      .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.GLOWING, 20*5).buildEntity(TimeAction.ADD))
      .addBlockEffect(new PlaceBlockFluidEffect(TinkerCommons.glow.get()));

    // milk
    addFluid(Tags.Fluids.MILK, FluidValues.SIP * 2)
      .addEntityEffect(new CureEffectsFluidEffect(Items.MILK_BUCKET))
      .addEntityEffect(StrongBonesModifier.FLUID_EFFECT);

    // slime - effects with blocks //
    // earth - extra slowness
    addSlime(TinkerFluids.earthSlime)
      .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20*5).buildEntity(TimeAction.ADD))
      .addEntityEffect(new PushEntityFluidEffect(0, -1))
      .addBlockEffect(MoveBlocksFluidEffect.pull(Sounds.SLIME_SLING.getSound()));
    // sky - jump boost
    addSlime(TinkerFluids.skySlime)
      .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20*5).buildEntity(TimeAction.ADD))
      .addEntityEffect(new PushEntityFluidEffect(0, 1))
      .addBlockEffect(MoveBlocksFluidEffect.push(Sounds.SLIME_SLING.getSound()));
    // ichor - levitation
    addSlime(TinkerFluids.ichor)
      .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.SLOW_FALLING, 7*20).effect(MobEffects.MOVEMENT_SLOWDOWN, 20*5).buildEntity(TimeAction.ADD))
      .addBlockEffect(new PlaceBlockFluidEffect(null));
    // ender - teleporting
    LivingEntityPredicate hasReturning = new HasMobEffectPredicate(TinkerEffects.returning.get());
    FluidMobEffect returningEffect = new FluidMobEffect(TinkerEffects.returning.get(), 7*20, 1, null);
    addSlime(TinkerFluids.enderSlime)
      .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20*5).buildEntity(TimeAction.ADD))
      // if returning, extend it, need to do this first as otherwise we extend ourself
      .addEntityEffect(hasReturning, new MobEffectFluidEffect(returningEffect, TimeAction.ADD))
      // if no returning, give returning then teleport
      .addEntityEffect(hasReturning.inverted(), SequenceFluidEffect.entities().effect(new MobEffectFluidEffect(returningEffect, TimeAction.SET)).effect(new RandomTeleportFluidEffect(LevelingInt.eachLevel(8), LevelingInt.flat(16))).build())
      .addBlockEffect(SequenceFluidEffect.blocks().effect(new BreakBlockFluidEffect(0)).effect(new PlaceBlockFluidEffect(null)).build());

    // slimelike - miscelaneous //
    // venom - poison & strength
    addFluid(TinkerFluids.venom, FluidValues.SIP).addEffect(FluidMobEffect.builder().effect(MobEffects.POISON, 20 * 5).effect(MobEffects.DAMAGE_BOOST, 20 * 10), TimeAction.ADD);
    // magma - small explosion
    addFluid(TinkerFluids.magma, FluidValues.SLIME_DROP * 2).addEffect(ExplosionFluidEffect.radius(0.5f, 0.5f).damage(LevelingValue.eachLevel(2)).placeFire().build());
    // ender - enderference / enderference cloud
    addFluid(TinkerFluids.moltenEnder, FluidValues.SLIME_DROP)
      .addEntityEffect(EntityInteractFluidEffect.INSTANCE)
      .addBlockEffect(BlockInteractFluidEffect.INSTANCE);

    // glass - piercing and spikes //
    addGlass(TinkerFluids.moltenGlass)
      .spikeDamage(1)
      .addBlockEffect(ScalingFluidEffect.blocks()
                                        .effect(1, new PlaceBlockFluidEffect(TinkerCommons.clearGlassPane.get()))
                                        .effect(4, new PlaceBlockFluidEffect(TinkerCommons.clearGlass.get()))
                                        .build());
    addGlass(TinkerFluids.liquidSoul)
      .spikeDamage(1)
      .addBlockEffect(ScalingFluidEffect.blocks()
                                        .effect(1, new PlaceBlockFluidEffect(TinkerCommons.soulGlassPane.get()))
                                        .effect(4, new PlaceBlockFluidEffect(TinkerCommons.soulGlass.get()))
                                        .build());
    addGlass(TinkerFluids.moltenObsidian)
      .spikeDamage(2)
      .addBlockEffect(ScalingFluidEffect.blocks()
                                        .effect(1, new PlaceBlockFluidEffect(TinkerCommons.obsidianPane.get()))
                                        .effect(4, new PlaceBlockFluidEffect(Blocks.OBSIDIAN))
                                        .build());

    // clay - direct damage and building //
    addClay(TinkerFluids.moltenClay)
      .addEntityEffect(new DamageFluidEffect(2f, TinkerDamageTypes.FLUID_IMPACT))
      .addEntityEffect(new DropItemFluidEffect(Items.BRICK))
      .addBlockEffect(ScalingFluidEffect.blocks()
                                        .effect(1, new DropItemFluidEffect(Items.BRICK))
                                        .effect(2, new PlaceBlockFluidEffect(Blocks.BRICK_SLAB))
                                        .effect(4, new PlaceBlockFluidEffect(Blocks.BRICKS))
                                        .build());
    addClay(TinkerFluids.searedStone)
      .addEntityEffect(new DamageFluidEffect(3f, TinkerDamageTypes.FLUID_IMPACT))
      .addEntityEffect(new DropItemFluidEffect(TinkerSmeltery.searedBrick))
      .addBlockEffect(ScalingFluidEffect.blocks()
                                        .effect(1, new DropItemFluidEffect(TinkerSmeltery.searedBrick))
                                        .effect(2, new PlaceBlockFluidEffect(TinkerSmeltery.searedBricks.getSlab()))
                                        .effect(4, new PlaceBlockFluidEffect(TinkerSmeltery.searedBricks.get()))
                                        .build());
    addClay(TinkerFluids.scorchedStone)
      .addEntityEffect(new DamageFluidEffect(3f, TinkerDamageTypes.FLUID_IMPACT))
      .addEntityEffect(new DropItemFluidEffect(TinkerSmeltery.scorchedBrick))
      .addBlockEffect(ScalingFluidEffect.blocks()
                                        .effect(1, new DropItemFluidEffect(TinkerSmeltery.scorchedBrick))
                                        .effect(2, new PlaceBlockFluidEffect(TinkerSmeltery.scorchedBricks.getSlab()))
                                        .effect(4, new PlaceBlockFluidEffect(TinkerSmeltery.scorchedBricks.get()))
                                        .build());

    // gems - direct damage and mining //
    addGem(TinkerFluids.moltenAmethyst).addBlockEffect(new HarvestTierPredicate(Tiers.STONE),     new BreakBlockFluidEffect(3));
    addGem(TinkerFluids.moltenQuartz  ).addBlockEffect(new HarvestTierPredicate(Tiers.IRON),      new BreakBlockFluidEffect(5));
    addGem(TinkerFluids.moltenEmerald ).addBlockEffect(new HarvestTierPredicate(Tiers.IRON),      new BreakBlockFluidEffect(10, Enchantments.SILK_TOUCH, 1));
    addGem(TinkerFluids.moltenDiamond ).addBlockEffect(new HarvestTierPredicate(Tiers.DIAMOND),   new BreakBlockFluidEffect(10, Enchantments.BLOCK_FORTUNE, 3));
    addMetal(TinkerFluids.moltenDebris).addBlockEffect(new HarvestTierPredicate(Tiers.NETHERITE), new BreakBlockFluidEffect(50));

    // foods - setup to give equivalent saturation on a full bowl/bottle to their food counterparts, though hunger may be slightly different
    addFluid(TinkerFluids.honey.getTag(), FluidValues.SIP)
      .addEntityEffect(new RestoreHungerFluidEffect(1, 0.12f, false, ItemOutput.fromItem(Items.HONEY_BOTTLE)))
      .addEntityEffect(new RemoveEffectFluidEffect(MobEffects.POISON));
    // soups
    addFluid(TinkerFluids.beetrootSoup.getTag(), FluidValues.SIP).addEntityEffect(new RestoreHungerFluidEffect(1, 0.72f, false, ItemOutput.fromItem(Items.BEETROOT_SOUP)));
    addFluid(TinkerFluids.mushroomStew.getTag(), FluidValues.SIP).addEntityEffect(new RestoreHungerFluidEffect(1, 0.72f, false, ItemOutput.fromItem(Items.MUSHROOM_STEW)));
    addFluid(TinkerFluids.rabbitStew.getTag(), FluidValues.SIP).addEntityEffect(new RestoreHungerFluidEffect(2, 0.6f, false, ItemOutput.fromItem(Items.RABBIT_STEW)));
    addFluid(TinkerFluids.meatSoup.getTag(), FluidValues.SIP).addEntityEffect(new RestoreHungerFluidEffect(2, 0.48f, false, ItemOutput.fromItem(TinkerFluids.meatSoupBowl)));
    // pig iron fills you up food, but still hurts
    addMetal(TinkerFluids.moltenPigIron).fireDamage(3).addEntityEffect(new RestoreHungerFluidEffect(2, 0.7f, false, ItemOutput.fromItem(TinkerCommons.bacon)));

    // metals, lose reference to mistborn (though a true fan would probably get angry at how much I stray from the source)
    // copper/bronze - air/rest
    addMetal(TinkerFluids.moltenCopper).fireDamage(1).addEntityEffect(new AddBreathFluidEffect(90));
    compatMetal(TinkerFluids.moltenBronze, "tin").fireDamage(3).addEntityEffect(new AwardStatFluidEffect(Stats.TIME_SINCE_REST, -6000));
    addMetal(TinkerFluids.moltenAmethystBronze).fireDamage(3).addEntityEffect(new AwardStatFluidEffect(Stats.TIME_SINCE_REST, 6000));
    // iron/steel - pull/push
    addMetal(TinkerFluids.moltenIron).fireDamage(2f).addEffect(FluidMobEffect.builder().effect(TinkerEffects.magnetic.get(), 20 * 5, 2), TimeAction.SET);
    addMetal(TinkerFluids.moltenSteel).fireDamage(2f).addEffect(FluidMobEffect.builder().effect(TinkerEffects.repulsive.get(), 20 * 5, 2), TimeAction.SET);
    // zinc/brass - speed/heat
    compatMetal(TinkerFluids.moltenZinc).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SPEED, 20 * 10), TimeAction.SET);
    compatMetal(TinkerFluids.moltenBrass, "zinc").fireDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.FIRE_RESISTANCE, 20 * 8), TimeAction.SET);
    // tin/pewter - sight/strength
    compatMetal(TinkerFluids.moltenTin).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.NIGHT_VISION, 20 * 8), TimeAction.SET);
    compatMetal(TinkerFluids.moltenPewter, "tin", "lead").fireDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.DAMAGE_BOOST, 20 * 7), TimeAction.SET);
    // gold/electrum/rose gold - health/haste/absorption
    addMetal(TinkerFluids.moltenGold).magicDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.REGENERATION, 20 * 6, 2), TimeAction.SET);
    compatMetal(TinkerFluids.moltenElectrum, "silver").magicDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.DIG_SPEED, 20 * 8, 1), TimeAction.SET);
    addMetal(TinkerFluids.moltenRoseGold).magicDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.ABSORPTION, 20 * 15, 2), TimeAction.SET);
    // chromium/nicrosil - luck/xp
    compatMetal(TinkerFluids.moltenChromium).magicDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.LUCK, 20 * 5, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenNicrosil).magicDamage(3).addEffect(FluidMobEffect.builder().effect(TinkerEffects.experienced.get(), 20 * 5, 1), TimeAction.SET);
    // cadmium/bendalloy - breath/energy
    compatMetal(TinkerFluids.moltenCadmium).fireDamage(2).addEntityEffect(new AddBreathFluidEffect(-90));
    compatMetal(TinkerFluids.moltenBendalloy).fireDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.SATURATION, 20 * 5, 1), TimeAction.SET);
    // aluminum/duralumin/silver - remove effects/you
    compatMetal(TinkerFluids.moltenAluminum).magicDamage(2).addEntityEffect(new CureEffectsFluidEffect(Items.MILK_BUCKET));
    compatMetal(TinkerFluids.moltenDuralumin).magicDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.INVISIBILITY, 20 * 5, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenSilver).magicDamage(2).addEntityEffect(new RemoveEffectFluidEffect(MobEffects.WITHER));
    // non-cosmere ores
    compatMetal(TinkerFluids.moltenLead).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.POISON, 20 * 5, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenNickel).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.WEAKNESS, 20 * 7, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenPlatinum).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.DIG_SLOWDOWN, 20 * 10, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenTungsten).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20 * 10, 2), TimeAction.SET);
    compatMetal(TinkerFluids.moltenOsmium).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.MOVEMENT_SLOWDOWN, 20 * 10, 2), TimeAction.SET);
    compatMetal(TinkerFluids.moltenUranium).fireDamage(2).addEffect(FluidMobEffect.builder().effect(MobEffects.POISON, 20 * 10, 1), TimeAction.SET);
    // non-cosmere alloys
    compatMetal(TinkerFluids.moltenInvar, "nickel").fireDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.HUNGER, 20 * 10, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenConstantan, "nickel").fireDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.HUNGER, 20 * 10, 1), TimeAction.SET);
    // slime metal
    int slimeMetal = FluidValues.NUGGET * 2;
    addFluid(TinkerFluids.moltenSlimesteel, slimeMetal).addEffect(ExplosionFluidEffect.radius(1, 0.5f).knockback(LevelingValue.eachLevel(1)).build());
    addFluid(TinkerFluids.moltenCinderslime, slimeMetal).addEffect(ExplosionFluidEffect.radius(1, 1).damage(LevelingValue.eachLevel(3)).knockback(LevelingValue.flat(-2)).build());
    addFluid(TinkerFluids.moltenQueensSlime, slimeMetal).addEffect(ExplosionFluidEffect.radius(1, 1).damage(LevelingValue.eachLevel(3)).placeFire().ignoreBlocks().build());
    // tinkers nether
    addMetal(TinkerFluids.moltenCobalt).fireDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.DIG_SPEED, 20 * 7, 1).effect(MobEffects.MOVEMENT_SPEED, 20 * 7, 1), TimeAction.SET);
    addMetal(TinkerFluids.moltenManyullyn).fireDamage(4).addEffect(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 20 * 3, 1), TimeAction.SET);
    addMetal(TinkerFluids.moltenHepatizon).magicDamage(4).addEffect(FluidMobEffect.builder().effect(MobEffects.WITHER, 20 * 8, 1), TimeAction.SET);
    addMetal(TinkerFluids.moltenNetherite).magicDamage(5).addEffect(FluidMobEffect.builder().effect(MobEffects.BLINDNESS, 20 * 5, 1), TimeAction.SET);
    // tinkers end
    addMetal(TinkerFluids.moltenKnightmetal).spikeDamage(4).addEffect(FluidMobEffect.builder().effect(TinkerEffects.pierce.get(), 20 * 5, 2), TimeAction.SET);
    // thermal compat
    compatFluid("glowstone", FluidValues.GEM).addEffect(FluidMobEffect.builder().effect(MobEffects.GLOWING, 20 * 10), TimeAction.ADD).addBlockEffect(new PlaceBlockFluidEffect(TinkerCommons.glow.get()));
    compatFluid("redstone", FluidValues.GEM).addEffect(ExplosionFluidEffect.radius(1, 0.5f).knockback(LevelingValue.eachLevel(-2)).ignoreBlocks().build());
    compatMetal(TinkerFluids.moltenSignalum).addEffect(ExplosionFluidEffect.radius(1, 1).damage(LevelingValue.eachLevel(2)).knockback(LevelingValue.flat(-2)).ignoreBlocks().build());
    compatMetal(TinkerFluids.moltenLumium).magicDamage(4).addEffect(FluidMobEffect.builder().effect(MobEffects.GLOWING, 20 * 5, 1).effect(MobEffects.MOVEMENT_SPEED, 20 * 5, 1).effect(MobEffects.JUMP, 20 * 5, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenEnderium).magicDamage(4).addEffect(FluidMobEffect.builder().effect(TinkerEffects.enderference.get(), 20 * 10, 1), TimeAction.SET);
    // mekanism compat
    compatMetal(TinkerFluids.moltenRefinedGlowstone).magicDamage(3).addEffect(FluidMobEffect.builder().effect(MobEffects.GLOWING, 20 * 10, 1), TimeAction.SET);
    compatMetal(TinkerFluids.moltenRefinedObsidian).spikeDamage(3).addEffect(FluidMobEffect.builder().effect(TinkerEffects.bleeding.get(), 20 * 2, 1), TimeAction.SET);

    // immersive engineering compat
    // ethanol - burns
    compatFluid("ethanol",      50).fireDamage(2f).addEntityEffect(new FireFluidEffect(TimeAction.ADD, 6)).placeFire();
    compatFluid("acetaldehyde", 50).fireDamage(3f).addEntityEffect(new FireFluidEffect(TimeAction.ADD, 6)).placeFire();
    // herbicide - kills plants
    IJsonPredicate<BlockState> plants = BlockPredicate.or(BlockPredicate.tag(BlockTags.REPLACEABLE_BY_TREES), TinkerPredicate.BUSH);
    compatFluid("herbicide", 10)
      // remove plants without drops, both in the offset direction and above
      .offsetBlockEffect(plants, FluidEffect.REMOVE_BLOCK)
      .addBlockEffect(new OffsetBlockFluidEffect(new ConditionalFluidEffect.Block(plants, FluidEffect.REMOVE_BLOCK), Direction.UP))
      // replace grass and farmland with dirt
      .addBlockEffect(BlockPredicate.or(BlockPredicate.set(Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL, Blocks.FARMLAND), BlockPredicate.tag(DirtType.VANILLA.getBlockTag())), new SetBlockFluidEffect(Blocks.DIRT))
      // discard leaves
      .addBlockEffect(BlockPredicate.tag(BlockTags.LEAVES), FluidEffect.REMOVE_BLOCK);
    // plant oil - its edible but makes you feel sick
    compatFluid("plantoil", 50)
      .addEntityEffect(new RestoreHungerFluidEffect(1, 1, true, ItemOutput.fromItem(Items.WHEAT_SEEDS)))
      .addEntityEffects(FluidMobEffect.builder().effect(MobEffects.CONFUSION, 5 * 20, 1).buildEntity(TimeAction.ADD));
    {
      String ie = "immersiveengineering";
      MobEffect flammable = FakeRegistryEntry.effect(new ResourceLocation(ie, "flammable"));
      compatFluid(ie, "creosote",  50)
        .addEffect(FluidMobEffect.builder().effect(flammable, 8 * 20, 1), TimeAction.ADD)
        .addEntityEffect(new FireFluidEffect(TimeAction.ADD, 8));
      compatFluid(ie, "biodiesel", 50)
        .addEffect(FluidMobEffect.builder().effect(flammable, 8 * 20, 2), TimeAction.ADD)
        .addEntityEffect(new FireFluidEffect(TimeAction.ADD, 8));
      FluidMobEffect conductive = new FluidMobEffect(FakeRegistryEntry.effect(new ResourceLocation(ie, "conductive")), 8 * 20, 2);
      compatFluid(ie, "redstone_acid",  50)
        .addEntityEffect(new MobEffectFluidEffect(conductive, TimeAction.ADD))
        .addBlockEffect(new MobEffectCloudFluidEffect(conductive))
        .addBlockEffect(FluidEffect.WEATHER);
      compatFluid(ie, "phenolic_resin", 50).addEffect(FluidMobEffect.builder().effect(FakeRegistryEntry.effect(new ResourceLocation(ie, "sticky")), 8 * 20, 2), TimeAction.ADD);
      Block concreteSprayed = FakeRegistryEntry.block(new ResourceLocation(ie, "concrete_sprayed"));
      AreaMobEffectFluidEffect concreteFeet = new AreaMobEffectFluidEffect(new FluidMobEffect(FakeRegistryEntry.effect(new ResourceLocation(ie, "concrete_feet")), MobEffectInstance.INFINITE_DURATION, 1), TimeAction.SET, GroupCost.MAX);
      compatFluid(ie, "concrete", 100)
        .addEntityEffect(new BlockAtEntityPredicate(BlockPredicate.CAN_BE_REPLACED, 0), new SetBlockFluidEffect(concreteSprayed))
        .offsetBlockEffect(BlockPredicate.CAN_BE_REPLACED, new SetBlockFluidEffect(concreteSprayed))
        .addEntityEffect(concreteFeet).offsetBlockEffect(concreteFeet);
    }

    // twilight forest compat
    compatMetal(TinkerFluids.moltenSteeleaf).magicDamage(2).addEffect(FluidMobEffect.builder().effect(TinkerEffects.experienced.get(), 20 * 5, 1), TimeAction.SET);
    addFluid(TinkerFluids.fieryLiquid, FluidValues.SIP).metalCondition("fiery")
        .addBlockEffect(new MeltBlockFluidEffect(BlockPredicate.ANY, FluidValues.INGOT, 1500));

    // potion fluid compat
    // standard potion is 250 mb, but we want a smaller number. divide into 5 pieces at 25% a piece (so healing is 1 health), means you gain 25% per potion
    addFluid(TinkerFluids.potion, FluidValues.SIP)
      .addEntityEffect(new PotionFluidEffect(0.25f, TagPredicate.ANY))
      .addBlockEffect(new PotionCloudFluidEffect(0.25f, TagPredicate.ANY));

    // create has three types of bottles stored on their fluid, react to it to boost
    Function<String,TagPredicate> createBottle = value -> {
      CompoundTag compound = new CompoundTag();
      compound.putString("Bottle", value);
      return new TagPredicate(compound);
    };
    String create = "create";
    addFluid("potion_create", FluidNameIngredient.of(new ResourceLocation(create, "potion"), FluidValues.SIP))
      .hidden() // we have the regular potion type showing, the create one in addition is a bit confusing
      .addCondition(new ModLoadedCondition(create))
      .addEntityEffect(new PotionFluidEffect(0.25f, createBottle.apply("REGULAR")))
      .addEntityEffect(new PotionFluidEffect(0.5f, createBottle.apply("SPLASH")))
      .addEntityEffect(new PotionFluidEffect(0.75f, createBottle.apply("LINGERING")))
      .addBlockEffect(new PotionCloudFluidEffect(0.25f, createBottle.apply("REGULAR")))
      .addBlockEffect(new PotionCloudFluidEffect(0.5f, createBottle.apply("SPLASH")))
      .addBlockEffect(new PotionCloudFluidEffect(0.75f, createBottle.apply("LINGERING")));
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Spilling Fluid Provider";
  }
}
