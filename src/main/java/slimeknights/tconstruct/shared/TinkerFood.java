package slimeknights.tconstruct.shared;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

@SuppressWarnings("WeakerAccess")
public final class TinkerFood {

  /* Slime balls are not exactly food items, but you CAN eat them.. if you really want to. */
  public static final FoodComponent BLUE_SLIME_BALL = (new FoodComponent.Builder()).hunger(1).saturationModifier(1.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 45, 2), 1.0F).effect(() -> new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 60, 2), 1.0F).build();
  public static final FoodComponent PURPLE_SLIME_BALL = (new FoodComponent.Builder()).hunger(1).saturationModifier(2.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.UNLUCK, 20 * 45), 1.0F).effect(() -> new StatusEffectInstance(StatusEffects.LUCK, 20 * 60), 1.0F).build();
  public static final FoodComponent BLOOD_SLIME_BALL = (new FoodComponent.Builder()).hunger(1).saturationModifier(1.5F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.POISON, 20 * 45, 2), 1.0F).effect(() -> new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 60), 1.0F).build();
  public static final FoodComponent MAGMA_SLIME_BALL = (new FoodComponent.Builder()).hunger(1).saturationModifier(1.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 45), 1.0F).effect(() -> new StatusEffectInstance(StatusEffects.WITHER, 20 * 15), 1.0F).effect(() -> new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 60), 1.0F).build();
  public static final FoodComponent PINK_SLIME_BALL = (new FoodComponent.Builder()).hunger(1).saturationModifier(1.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 10, 2), 1.0F).build();

  /* Bacon. What more is there to say? */
  public static final FoodComponent BACON = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.6F).build();

  /* Jerkies from drying racks */
  // for jerky that are similar to cooked meats, start with 1 less hunger
  // saturation is enough so (hunger + (hunger * saturation * 2) between [0.5,1] larger than vanilla
  public static final FoodComponent BEEF_JERKY = (new FoodComponent.Builder()).hunger(7).saturationModifier(1.05F).build();
  public static final FoodComponent CHICKEN_JERKY = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.9F).build();
  public static final FoodComponent PORK_JERKY = (new FoodComponent.Builder()).hunger(7).saturationModifier(1.05F).build();
  public static final FoodComponent MUTTON_JERKY = (new FoodComponent.Builder()).hunger(5).saturationModifier(1.1F).build();
  public static final FoodComponent RABBIT_JERKY = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.95F).build();
  public static final FoodComponent FISH_JERKY = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.95F).build();
  public static final FoodComponent SALMON_JERKY = (new FoodComponent.Builder()).hunger(5).saturationModifier(1.1F).build();
  // these jerkies do not match a cooked food, first two just cure effects, clownfish simply a nice snack
  public static final FoodComponent MONSTER_JERKY = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.4F).build();
  public static final FoodComponent CLOWNFISH_JERKY = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.9F).build();
  public static final FoodComponent PUFFERFISH_JERKY = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.5F).build();

  /* Slime Drops. Slime balls + drying rack = "healthy" */
  public static final FoodComponent GREEN_SLIME_DROP = (new FoodComponent.Builder()).hunger(1).saturationModifier(1.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.SPEED, 20 * 90, 2), 1.0F).build();
  public static final FoodComponent BLUE_SLIME_DROP = (new FoodComponent.Builder()).hunger(3).saturationModifier(1.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 90, 2), 1.0F).build();
  public static final FoodComponent PURPLE_SLIME_DROP = (new FoodComponent.Builder()).hunger(3).saturationModifier(2.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.LUCK, 20 * 90), 1.0F).build();
  public static final FoodComponent BLOOD_SLIME_DROP = (new FoodComponent.Builder()).hunger(3).saturationModifier(1.5F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 90), 1.0F).build();
  public static final FoodComponent MAGMA_SLIME_DROP = (new FoodComponent.Builder()).hunger(6).saturationModifier(1.0F).alwaysEdible().effect(() -> new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 90), 1.0F).build();
  public static final FoodComponent PINK_SLIME_DROP = (new FoodComponent.Builder()).hunger(1).saturationModifier(1.0F).alwaysEdible().build();

  private TinkerFood() {}
}
