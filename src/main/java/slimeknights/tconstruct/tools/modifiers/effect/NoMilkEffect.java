package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;
import slimeknights.tconstruct.common.TinkerEffect;

import java.util.ArrayList;
import java.util.List;

/** Effect that cannot be cured with milk */
public class NoMilkEffect extends TinkerEffect {
  public NoMilkEffect(EffectType typeIn, int color, boolean show) {
    super(typeIn, color, show);
  }

  @Override
  public List<ItemStack> getCurativeItems() {
    return new ArrayList<>();
  }
}
