package slimeknights.tconstruct.shared;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

@SuppressWarnings("WeakerAccess")
public final class TinkerFood {
  private TinkerFood() {}
  /* Bacon. What more is there to say? */
  public static final Food BACON = (new Food.Builder()).hunger(4).saturation(0.6F).build();


}
