package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.fluids.FluidAttributes;
import slimeknights.mantle.recipe.data.FluidNameIngredient;
import slimeknights.mantle.recipe.helper.TagEmptyCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.tinkering.AbstractSpillingFluidProvider;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.MobTypePredicate;
import slimeknights.tconstruct.library.modifiers.spilling.effects.CureEffectsSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect.DamageType;
import slimeknights.tconstruct.library.modifiers.spilling.effects.EffectSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.ExtinguishSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.PotionFluidEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.RemoveEffectSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.RestoreHungerSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.TeleportSpillingEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;

import java.util.function.Function;

public class SpillingFluidProvider extends AbstractSpillingFluidProvider {
  public SpillingFluidProvider(DataGenerator generator) {
    super(generator, TConstruct.MOD_ID);
  }

  @Override
  protected void addFluids() {
    // vanilla
    addFluid(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 20)
      .addEffect(LivingEntityPredicate.WATER_SENSITIVE, new DamageSpillingEffect(DamageType.PIERCING, 2f))
      .addEffect(ExtinguishSpillingEffect.INSTANCE);
    addFluid(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20)
      .addEffect(LivingEntityPredicate.FIRE_IMMUNE.inverted(), new DamageSpillingEffect(DamageType.FIRE, 2f))
      .addEffect(new SetFireSpillingEffect(10));
    addFluid(Tags.Fluids.MILK, FluidAttributes.BUCKET_VOLUME / 10)
      .addEffect(new CureEffectsSpillingEffect(new ItemStack(Items.MILK_BUCKET)))
      .addEffect(StrongBonesModifier.SPILLING_EFFECT);

    // blaze - more damage, less fire
    burningFluid("blazing_blood", TinkerFluids.blazingBlood.getLocalTag(), FluidAttributes.BUCKET_VOLUME / 20, 3f, 5);

    // slime
    int slimeballPiece = FluidValues.SLIMEBALL / 5;
    // earth - lucky
    addFluid(TinkerFluids.earthSlime.getForgeTag(), slimeballPiece)
      .addEffect(new EffectSpillingEffect(MobEffects.LUCK, 15, 1))
      .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 15, 1));
    // sky - jump boost
    addFluid(TinkerFluids.skySlime.getLocalTag(), slimeballPiece)
      .addEffect(new EffectSpillingEffect(MobEffects.JUMP, 20, 1))
      .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 15, 1));
    // ender - levitation
    addFluid(TinkerFluids.enderSlime.getLocalTag(), slimeballPiece)
      .addEffect(new EffectSpillingEffect(MobEffects.LEVITATION, 5, 1))
      .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 15, 1));
    // slimelike
    // blood - food
    addFluid(TinkerFluids.blood.getLocalTag(), slimeballPiece)
      .addEffect(new RestoreHungerSpillingEffect(1, 0.2f))
      .addEffect(new EffectSpillingEffect(MobEffects.DIG_SLOWDOWN, 10, 1));
    // venom - poison
    addFluid(TinkerFluids.venom.getLocalTag(), slimeballPiece)
      .addEffect(new EffectSpillingEffect(MobEffects.POISON, 25, 1));
    // magma - fire resistance
    addFluid(TinkerFluids.magma.getForgeTag(), slimeballPiece)
      .addEffect(new EffectSpillingEffect(MobEffects.FIRE_RESISTANCE, 25, 1));
    // soul - slowness and blindness
    addFluid(TinkerFluids.liquidSoul.getLocalTag(), slimeballPiece)
      .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 25, 2))
      .addEffect(new EffectSpillingEffect(MobEffects.BLINDNESS, 5, 1));
    // ender - teleporting
    addFluid(TinkerFluids.moltenEnder.getForgeTag(), FluidAttributes.BUCKET_VOLUME / 20)
      .addEffect(new DamageSpillingEffect(DamageType.MAGIC, 1f))
      .addEffect(TeleportSpillingEffect.INSTANCE);

    // foods
    addFluid(TinkerFluids.honey.getForgeTag(), slimeballPiece)
      .addEffect(new RestoreHungerSpillingEffect(1, 0.2f))
      .addEffect(new RemoveEffectSpillingEffect(MobEffects.POISON));
    addFluid(TinkerFluids.beetrootSoup.getForgeTag(), slimeballPiece)
      .addEffect(new RestoreHungerSpillingEffect(1, 1.5f));
    addFluid(TinkerFluids.mushroomStew.getForgeTag(), slimeballPiece)
      .addEffect(new RestoreHungerSpillingEffect(1, 1.5f));
    addFluid(TinkerFluids.rabbitStew.getForgeTag(), slimeballPiece)
      .addEffect(new RestoreHungerSpillingEffect(2, 2.4f));

    // multi-recipes
    burningFluid("glass",           TinkerTags.Fluids.GLASS_SPILLING,           FluidAttributes.BUCKET_VOLUME / 10, 1f,   3);
    burningFluid("clay",            TinkerTags.Fluids.CLAY_SPILLING,            FluidValues.BRICK / 5,              1.5f, 3);
    burningFluid("metal_cheap",     TinkerTags.Fluids.CHEAP_METAL_SPILLING,     FluidValues.NUGGET,                 1.5f, 7);
    burningFluid("metal_average",   TinkerTags.Fluids.AVERAGE_METAL_SPILLING,   FluidValues.NUGGET,                 2f,   7);
    burningFluid("metal_expensive", TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING, FluidValues.NUGGET,                 2.5f, 7);

    // gold applies magic
    addFluid(TinkerFluids.moltenGold.getForgeTag(), FluidValues.NUGGET)
      .addEffect(new MobTypePredicate(MobType.UNDEAD), new DamageSpillingEffect(DamageType.MAGIC, 2f))
      .addEffect(new SetFireSpillingEffect(3));
    // pig iron fills you up food, but still hurts
    addFluid(TinkerFluids.moltenPigIron.getLocalTag(), FluidValues.NUGGET)
      .addEffect(new RestoreHungerSpillingEffect(2, 0.3f))
      .addEffect(new SetFireSpillingEffect(2));
    // uranium also does poison
    burningFluid(TinkerFluids.moltenUranium.getForgeTag(), 1.5f, 3)
      .addEffect(new EffectSpillingEffect(MobEffects.POISON, 10, 1));

    // potion fluid compat
    TagKey<Fluid> potionTag = FluidTags.create(new ResourceLocation("forge", "potion"));
    // standard potion is 250 mb, but we want a smaller number. For the effects, we really want to divide into 4 pieces
    addFluid("potion_fluid", potionTag, FluidValues.BOTTLE / 2)
      .condition(new NotCondition(new TagEmptyCondition<>(potionTag)))
      .addEffect(new PotionFluidEffect(0.5f, TagPredicate.ANY));

    // create has three types of bottles stored on their fluid, react to it to boost
    Function<String,TagPredicate> createBottle = value -> {
      CompoundTag compound = new CompoundTag();
      compound.putString("Bottle", value);
      return new TagPredicate(compound);
    };
    String create = "create";
    addFluid("potion_create", FluidNameIngredient.of(new ResourceLocation(create, "potion"), FluidAttributes.BUCKET_VOLUME / 8))
      .condition(new ModLoadedCondition(create))
      .addEffect(new PotionFluidEffect(0.25f, createBottle.apply("REGULAR")))
      .addEffect(new PotionFluidEffect(0.5f, createBottle.apply("SPLASH")))
      .addEffect(new PotionFluidEffect(1f, createBottle.apply("LINGERING")));

  }

  @Override
  public String getName() {
    return "Tinkers' Construct Spilling Fluid Provider";
  }
}
