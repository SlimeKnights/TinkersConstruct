package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Utinet.minecraft.world.damagesource.CombatRulesation logic
 */
public class ArmorUtil {
  private ArmorUtil() {}

  /** Old three-argument armor absorption formula, retained for modifier math. */
  private static float getDamageAfterAbsorb(float damage, float armor, float toughness) {
    float toughnessFactor = 2.0F + toughness / 4.0F;
    float armorFactor = Mth.clamp(armor - damage / toughnessFactor, armor * 0.2F, 20.0F);
    return damage * (1.0F - armorFactor / 25.0F);
  }

  /**
   * Inverse of {@link net.minecraft.world.damagesource.CombatRules#getDamageAfterAbsorb(float, float, float)}  with respect to damage
   * @param damage     Damage returned by the vanilla function, must be 0 or more
   * @param armor      Total armor value, tested between 0 and 30
   * @param toughness  Total toughness value, tested between 0 and 20
   * @return  Original damage to be dealt
   */
  public static float getDamageBeforeArmorAbsorb(float damage, float armor, float toughness) {
    if (damage <= 0) {
      return 0;
    }
    float boostedToughness = toughness + 8f; // all usages of toughness in the inverse had 8 added, so do it once
    float atProduct = armor*boostedToughness; // this product also showed up a lot in the inverse
    return 5 * Mth.clamp(
      ((float)Math.sqrt(boostedToughness * (0.04f*armor*atProduct - 2f*atProduct + 25f*boostedToughness + 16f*damage))
       + 0.2f*atProduct - 5f*boostedToughness) / 8f,
      damage * 25f / (125f - armor),
      damage);
  }

  /**
   * Extension of of {@link net.minecraft.world.damagesource.CombatRules#getDamageAfterMagicAbsorb(float, float)} to allow increasing damage via negative numbers
   * @param damage            Damage to absorb (or increase)
   * @param enchantModifiers  Enchantment modifier amount, between -20 and 20
   * @return  Original damage to be dealt
   */
  public static float getDamageAfterMagicAbsorb(float damage, float enchantModifiers) {
    return getDamageAfterMagicAbsorb(damage, enchantModifiers, 20f);
  }

  /**
   * Extension of of {@link net.minecraft.world.damagesource.CombatRules#getDamageAfterMagicAbsorb(float, float)} to allow increasing damage via negative numbers and a higher cap
   * @param damage            Damage to absorb (or increase)
   * @param enchantModifiers  Enchantment modifier amount, between -20 and 20
   * @return  Original damage to be dealt
   */
  public static float getDamageAfterMagicAbsorb(float damage, float enchantModifiers, float cap) {
    // saves a bit of effort for 0 ranges
    if (enchantModifiers == 0 || damage <= 0) {
      return damage;
    }
    return damage * (1f - Mth.clamp(enchantModifiers, -20f, cap) / 25f);
  }

  /**
   * Inverse of {@link net.minecraft.world.damagesource.CombatRules#getDamageAfterMagicAbsorb(float, float)} with respect to damage
   * @param damage            Damage returned by the vanilla function, must be 0 or more
   * @param enchantModifiers  Enchantment modifier amount
   * @return  Original damage to be dealt
   */
  public static float getDamageBeforeMagicAbsorb(float damage, float enchantModifiers) {
    return damage / (1f - (Mth.clamp(enchantModifiers, 0f, 20f) / 25f));
  }

  /** Same as {@link #getDamageForEvent(float, float, float, float, float, float)} but sets the cap to 20f */
  public static float getDamageForEvent(float originalDamage, float armor, float toughness, float vanillaModifiers, float finalModifiers) {
    return getDamageForEvent(originalDamage, armor, toughness, vanillaModifiers, finalModifiers, 20f);
  }

  /**
   * Calculates the final damage for use in {@link net.neoforged.neoforge.event.entity.living.LivingHurtEvent}. Requires applying several inverse functions to cancel out vanilla formulas that are applied later
   * @param originalDamage     Original damage to be dealt
   * @param armor              Armor amount on the player
   * @param toughness          Armor toughness attribute
   * @param vanillaModifiers   Vanilla armor modifiers from enchantments
   * @param finalModifiers     Armor modifiers from modifiers and vanilla
   * @param modifierCap        Maximum protection value allowed
   * @return  Damage to return in the event
   */
  public static float getDamageForEvent(float originalDamage, float armor, float toughness, float vanillaModifiers, float finalModifiers, float modifierCap) {
    // if we are changing no values, nothing to do
    if (vanillaModifiers == finalModifiers && modifierCap == 20) {
      return originalDamage;
    }

    // we want the modifiers to be applied after armor attributes, but due to the location of the events we have to run before
    // essentially, for armor A(x) and modifiers M(x), we want M(A(x)), but the order it runs gives us A(M(x)). Since A(x) is not linear, the order matters
    // the solution is instead of returning M(x), we return A-1(M(A(x))), giving us A(A-1(M(A(x)))) == M(A(x))
    float damage = originalDamage;
    // if there is no armor value though, no work is needed
    if (armor > 0) {
      damage = getDamageAfterAbsorb(damage, armor, toughness);
    }

    // next, we want to apply our modifiers bonus M(x), it works out to be a reduction between 0 and 80%
    // this includes the vanilla bonus as that makes our modifier 1 to 1 with the vanilla enchant
    // again, can skip if no bonus. This means we are just removing the vanilla bonus
    if (finalModifiers != 0) {
      damage = getDamageAfterMagicAbsorb(damage, finalModifiers, modifierCap);
    }

    // if there is a vanilla bonus, we want to cancel it out so our bonus remains
    // essentially, for a vanilla bonus V(x), instead of M(A(x)), we get V(M(A(x))) which applies vanilla modifiers twice
    // the solution is we return A-1(V-1(M(A(x)))), giving V(A(A-1(V-1(M(A(x)))))) == V(V-1(M(A(x)))) == M(A(x))
    if (vanillaModifiers > 0) {
      damage = getDamageBeforeMagicAbsorb(damage, vanillaModifiers);
    }

    // finally, apply the inverse A-1(x) that was mentioned in several prior comments, assuming armor is defined
    if (armor > 0) {
      damage = getDamageBeforeArmorAbsorb(damage, armor, toughness);
    }

    // final damage: A-1(V-1(M(A(x))))
    return damage;
  }

  private static final String DIAMOND_ARMOR = "textures/models/armor/diamond_layer_1.png";
  private static final String DIAMOND_LEGGINGS = "textures/models/armor/diamond_layer_2.png";

  /**
   * We override the armor model to not use the "vanilla" texture in favor of our own system that fetches the texture from NBT.
   * However, this means vanilla constructs some non-existing textures for us producing errors in the log.
   * Since we don't end up using that texture, bypass the error by just returning a vanilla texture.
   * We would just use our system, but it notably supports returning no texture to not render (would still lead to errors) and requires unneeded stack parsing, so faster to just use an arbitrary texture we know exists.
   */
  public static String getDummyArmorTexture(EquipmentSlot slot) {
    return slot == EquipmentSlot.LEGS ? DIAMOND_LEGGINGS : DIAMOND_ARMOR;
  }
}
