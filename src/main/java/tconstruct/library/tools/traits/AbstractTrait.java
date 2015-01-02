package tconstruct.library.tools.traits;

public abstract class AbstractTrait implements IMaterialTrait {

  /* Updating */
  @Override
  public void onUpdate() {

  }

  /* Harvesting */
  @Override
  public boolean beforeBlockBreak() {
    return false;
  }

  @Override
  public void afterBlockBreak() // Unfinished, not called
  {

  }

  /* Attacking */
  @Override
  public float onHit(float damage, float currentDamage) {
    return currentDamage;
  }

  @Override
  public boolean doesCriticalHit() {
    return false;
  }

  /* Damage tool */
  @Override
  public int onDamage(int damage, int currentDamage) {
    return currentDamage;
  }
}
