package tconstruct.library.tools.traits;

public class StoneboundTrait extends AbstractTrait {

  @Override
  public String getIdentifier() {
    return "Stonebound";
  }


  @Override
  public float miningSpeed(float speed, float currentSpeed, boolean isEffective) {
    // todo: calculate actual speed change based on damage
    currentSpeed = Math.max(0f, speed * 9 / 10);

    return currentSpeed;
  }
}
