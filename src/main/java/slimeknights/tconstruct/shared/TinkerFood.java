package slimeknights.tconstruct.shared;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

@SuppressWarnings("WeakerAccess")
public final class TinkerFood {

  /* Slime balls are not exactly food items, but you CAN eat them.. if you really want to. */
  public static final Food BLUE_SLIME_BALL = (new Food.Builder()).hunger(1).saturation(1.0F).setAlwaysEdible().effect(new EffectInstance(Effects.SLOWNESS, 20 * 45, 2), 1.0F).effect(new EffectInstance(Effects.JUMP_BOOST, 20 * 60, 2), 1.0F).build();
  public static final Food PURPLE_SLIME_BALL = (new Food.Builder()).hunger(1).saturation(2.0F).setAlwaysEdible().effect(new EffectInstance(Effects.UNLUCK, 20 * 45), 1.0F).effect(new EffectInstance(Effects.LUCK, 20 * 60), 1.0F).build();
  public static final Food BLOOD_SLIME_BALL = (new Food.Builder()).hunger(1).saturation(1.5F).setAlwaysEdible().effect(new EffectInstance(Effects.POISON, 20 * 45, 2), 1.0F).effect(new EffectInstance(Effects.HEALTH_BOOST, 20 * 60), 1.0F).build();
  public static final Food MAGMA_SLIME_BALL = (new Food.Builder()).hunger(1).saturation(1.0F).setAlwaysEdible().effect(new EffectInstance(Effects.WEAKNESS, 20 * 45), 1.0F).effect(new EffectInstance(Effects.WITHER, 20 * 15), 1.0F).effect(new EffectInstance(Effects.FIRE_RESISTANCE, 20 * 60), 1.0F).build();
  public static final Food PINK_SLIME_BALL = (new Food.Builder()).hunger(1).saturation(1.0F).setAlwaysEdible().effect(new EffectInstance(Effects.NAUSEA, 20 * 10, 2), 1.0F).build();

  /* Bacon. What more is there to say? */
  public static final Food BACON = (new Food.Builder()).hunger(4).saturation(0.6F).build();

  /* Jerkies from drying racks */
  public static final Food MONSTER_JERKY = (new Food.Builder()).hunger(4).saturation(0.4F).build();
  public static final Food BEEF_JERKY = (new Food.Builder()).hunger(8).saturation(1.0F).build();
  public static final Food CHICKEN_JERKY = (new Food.Builder()).hunger(6).saturation(0.8F).build();
  public static final Food PORK_JERKY = (new Food.Builder()).hunger(8).saturation(1.0F).build();
  public static final Food MUTTON_JERKY = (new Food.Builder()).hunger(6).saturation(1.0F).build();
  public static final Food RABBIT_JERKY = (new Food.Builder()).hunger(5).saturation(0.8F).build();
  public static final Food FISH_JERKY = (new Food.Builder()).hunger(5).saturation(0.8F).build();
  public static final Food SALMON_JERKY = (new Food.Builder()).hunger(6).saturation(1.0F).build();
  public static final Food CLOWNFISH_JERKY = (new Food.Builder()).hunger(3).saturation(0.8F).build();
  public static final Food PUFFERFISH_JERKY = (new Food.Builder()).hunger(3).saturation(0.8F).build();

  /* Slime Drops. Slime balls + drying rack = "healthy" */
  public static final Food GREEN_SLIME_DROP = (new Food.Builder()).hunger(1).saturation(1.0F).setAlwaysEdible().effect(new EffectInstance(Effects.SPEED, 20 * 90, 2), 1.0F).build();
  public static final Food BLUE_SLIME_DROP = (new Food.Builder()).hunger(3).saturation(1.0F).setAlwaysEdible().effect(new EffectInstance(Effects.JUMP_BOOST, 20 * 90, 2), 1.0F).build();
  public static final Food PURPLE_SLIME_DROP = (new Food.Builder()).hunger(3).saturation(2.0F).setAlwaysEdible().effect(new EffectInstance(Effects.LUCK, 20 * 90), 1.0F).build();
  public static final Food BLOOD_SLIME_DROP = (new Food.Builder()).hunger(3).saturation(1.5F).setAlwaysEdible().effect(new EffectInstance(Effects.HEALTH_BOOST, 20 * 90), 1.0F).build();
  public static final Food MAGMA_SLIME_DROP = (new Food.Builder()).hunger(6).saturation(1.0F).setAlwaysEdible().effect(new EffectInstance(Effects.FIRE_RESISTANCE, 20 * 90), 1.0F).build();
  public static final Food PINK_SLIME_DROP = (new Food.Builder()).hunger(1).saturation(1.0F).setAlwaysEdible().build();

  private TinkerFood() {}
}
