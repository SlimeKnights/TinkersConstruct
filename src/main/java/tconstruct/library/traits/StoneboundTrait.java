package tconstruct.library.traits;

import net.minecraft.item.ItemStack;

public class StoneboundTrait extends AbstractTrait {

  public StoneboundTrait() {
    super("Stonebound");
  }

  @Override
  public int getMaxCount() {
    return 2;
  }

  @Override
  public float miningSpeed(ItemStack stack, float speed, float currentSpeed, boolean isEffective) {
    // todo: calculate actual speed change based on damage
    currentSpeed = Math.max(0f, speed * 9 / 10);

    return currentSpeed;
  }
}
