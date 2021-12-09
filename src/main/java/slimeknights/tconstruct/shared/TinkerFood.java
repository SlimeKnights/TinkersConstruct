package slimeknights.tconstruct.shared;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import slimeknights.tconstruct.shared.block.SlimeType;

@SuppressWarnings("WeakerAccess")
public final class TinkerFood {
  private TinkerFood() {}
  /* Bacon. What more is there to say? */
  public static final Food BACON = (new Food.Builder()).hunger(4).saturation(0.6F).build();

  /** For the modifier */
  public static final Food JEWELED_APPLE = (new Food.Builder()).hunger(4).saturation(1.2F).effect(() -> new EffectInstance(Effects.HASTE, 1200, 0), 1.0F).effect(() -> new EffectInstance(Effects.RESISTANCE, 2400, 0), 1.0F).setAlwaysEdible().build();

  /* Cake block is set up to take food as a parameter */
  public static final Food EARTH_CAKE = new Food.Builder().hunger(1).saturation(0.1f).setAlwaysEdible().effect(() -> new EffectInstance(Effects.LUCK, 20 * 15, 0), 1.0f).build();
  public static final Food SKY_CAKE   = new Food.Builder().hunger(1).saturation(0.1f).setAlwaysEdible().effect(() -> new EffectInstance(Effects.JUMP_BOOST, 20 * 20, 1), 1.0f).build();
  public static final Food ICHOR_CAKE = new Food.Builder().hunger(3).saturation(0.1f).setAlwaysEdible().effect(() -> new EffectInstance(Effects.ABSORPTION, 20 * 30, 0), 1.0f).build();
  public static final Food BLOOD_CAKE = new Food.Builder().hunger(1).saturation(0.2f).setAlwaysEdible().effect(() -> new EffectInstance(Effects.REGENERATION, 50 * 3, 0), 1.0f).build();
  public static final Food MAGMA_CAKE = new Food.Builder().hunger(1).saturation(0.2f).setAlwaysEdible().effect(() -> new EffectInstance(Effects.FIRE_RESISTANCE, 20 * 30, 0), 1.0f).build();
  public static final Food ENDER_CAKE = new Food.Builder().hunger(2).saturation(0.2f).setAlwaysEdible().effect(() -> new EffectInstance(Effects.LEVITATION, 20 * 10, 0), 1.0f).build();

  /**
   * Gets the cake for the given slime type
   * @param slime  Slime type
   * @return  Cake food
   */
  public static Food getCake(SlimeType slime) {
    switch (slime) {
      case EARTH: default: return EARTH_CAKE;
      case SKY: return SKY_CAKE;
      case ICHOR: return ICHOR_CAKE;
      case BLOOD: return BLOOD_CAKE;
      case ENDER: return ENDER_CAKE;
    }
  }
}
