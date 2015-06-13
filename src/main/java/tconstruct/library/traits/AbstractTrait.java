package tconstruct.library.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import tconstruct.library.Util;

public abstract class AbstractTrait implements ITrait {
  public static final String LOCALIZATION_STRING = "trait.%s.name";
  private final String identifier;

  public AbstractTrait(String identifier) {
    this.identifier = identifier;
  }

  /**
   * The general assumption is that Traits don't have levels.
   * Should it be needed, however, this function allows you to easily get the level of a trait.
   * @return Level of the trait. 0 If the trait is not present.
   */
  public int getTraitLevel(ItemStack stack) {
    /*
    NBTTagCompound traits = TagUtil.getTraitsTagList(stack);
    for (int i = 0; traits.hasKey(String.valueOf(i)); i++) {
      ModifierNBT data = ModifierNBT.read(traits, String.valueOf(i));
      if (identifier.equals(data.identifier)) {
        return data.level;
      }
    }
*/
    return 0;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String getLocalizedName() {
    String locString = Util.sanitizeLocalizationString(getIdentifier());
    return StatCollector.translateToLocal(String.format(LOCALIZATION_STRING, locString));
  }

  /* Updating */
  @Override
  public void onUpdate(ItemStack stack) {

  }


  /* Harvesting */
  @Override
  public float miningSpeed(ItemStack stack, float speed, float currentSpeed, boolean isEffective) {
    return currentSpeed;
  }

  @Override
  public boolean beforeBlockBreak(ItemStack stack) {
    return false;
  }

  @Override
  public void afterBlockBreak(ItemStack stack) {

  }

  /* Attacking */
  @Override
  public float onHit(ItemStack stack, float damage, float currentDamage) {
    return currentDamage;
  }

  @Override
  public boolean doesCriticalHit(ItemStack stack) {
    return false;
  }

  /* Damage tool */
  @Override
  public int onDamage(ItemStack stack, int damage, int currentDamage) {
    return currentDamage;
  }
}
