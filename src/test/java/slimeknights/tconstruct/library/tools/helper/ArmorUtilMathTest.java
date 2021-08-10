package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.util.CombatRules;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** Tests math functions that are part of armor util, does not bootstrap the game */
public class ArmorUtilMathTest {
  @Test
  void damageArmorAbsorb_inverse() {
    float deltaDamage = 1f;
    float maxDamage = 50;
    float deltaAttr = 0.2f;
    Offset<Float> tolerance = within(0.001f);
    for (float damage = 0; damage < maxDamage; damage += deltaDamage) {
      // goes to max armor
      for (float armor = 0; armor < 30; armor += deltaAttr) {
        // goes to max toughness
        for (float toughness = 0; toughness < 20; toughness += deltaAttr) {
          float inverseSecond = ArmorUtil.getDamageBeforeArmorAbsorb(CombatRules.getDamageAfterAbsorb(damage, armor, toughness), armor, toughness);
          assertThat(inverseSecond)
            .withFailMessage("Inverse f-1(f(x) failed at damage %.2f, armor %.2f, toughness %.2f - actual %.10f", damage, armor, toughness, inverseSecond)
            .isEqualTo(damage, tolerance);
          float inverseFirst = CombatRules.getDamageAfterAbsorb(ArmorUtil.getDamageBeforeArmorAbsorb(damage, armor, toughness), armor, toughness);
          assertThat(inverseFirst)
            .withFailMessage("Inverse f(f-1(x) failed at damage %.2f, armor %.2f, toughness %.2f - actual %.10f", damage, armor, toughness, inverseFirst)
            .isEqualTo(damage, tolerance);
          //System.out.printf("Results at damage %5.2f, armor %5.2f, toughness %5.2f - f-1(f(x) %6.3f, f(f-1(x)) %6.3f\n", damage, armor, toughness, inverseSecond, inverseFirst);
        }
      }
    }
  }

  @Test
  void damageMagicAbsorb_inverse() {
    float deltaDamage = 1f;
    float maxDamage = 50;
    float deltaModifier = 0.2f;
    Offset<Float> tolerance = within(0.001f);
    for (float damage = 0; damage < maxDamage; damage += deltaDamage) {
      // goes to a bit beyond max to ensure the clamp is working
      for (float modifier = 0; modifier < 25; modifier += deltaModifier) {
        float inverseSecond = ArmorUtil.getDamageBeforeMagicAbsorb(CombatRules.getDamageAfterMagicAbsorb(damage, modifier), modifier);
        assertThat(inverseSecond)
          .withFailMessage("Inverse f-1(f(x) failed at damage %.2f, modifier %.2f - actual %.3f", damage, modifier, inverseSecond)
          .isEqualTo(damage, tolerance);
        float inverseFirst = CombatRules.getDamageAfterMagicAbsorb(ArmorUtil.getDamageBeforeMagicAbsorb(damage, modifier), modifier);
        assertThat(inverseFirst)
          .withFailMessage("Inverse f(f-1(x) failed at damage %.2f, modifier %.2f - actual %.3f", damage, modifier, inverseFirst)
          .isEqualTo(damage, tolerance);
        //System.out.printf("Results at damage %5.2f, modifier %5.2f - f-1(f(x) %6.3f, f(f-1(x)) %6.3f\n", damage, modifier, inverseSecond, inverseFirst);
      }
    }
  }

  @Test
  void damageMagicAbsorb_consistentWithVanillaOverCommonDomain() {
    float deltaDamage = 1f;
    float maxDamage = 50;
    float deltaModifier = 0.2f;
    Offset<Float> tolerance = within(0.001f);
    for (float damage = 0; damage < maxDamage; damage += deltaDamage) {
      // goes to a bit beyond max to ensure the clamp is working
      for (float modifier = 0; modifier < 25; modifier += deltaModifier) {
        float target = CombatRules.getDamageAfterMagicAbsorb(damage, modifier);
        float actual = ArmorUtil.getDamageAfterMagicAbsorb(damage, modifier);
        assertThat(actual)
          .withFailMessage("Function result mismatch at damage %.2f, modifier %.2f - target %.3f, actual %.3f", damage, modifier, target, actual)
          .isEqualTo(target, tolerance);
        //System.out.printf("Result at damage %5.2f, modifier %5.2f - target %6.3f, actual %6.3f\n", damage, modifier, target, actual);
      }
    }
  }

  @Test
  void armorAttributeOrder_testEventModifier() {
    float deltaDamage = 1f;
    float maxDamage = 50;
    float deltaAttr = 1f;
    Offset<Float> tolerance = within(0.001f);
    for (float damage = 0; damage < maxDamage; damage += deltaDamage) {
      // goes to max armor
      for (float armor = 0; armor < 30; armor += deltaAttr) {
        // goes to max toughness
        for (float toughness = 0; toughness < 20; toughness += deltaAttr) {
          // A(x)
          float vanillaDamage = CombatRules.getDamageAfterAbsorb(damage, armor, toughness);
          // goes 180% to 20% adjustment
          for (float modifier = -20; modifier < 20; modifier += deltaAttr) {
            // goal: M(A(x))
            float target = ArmorUtil.getDamageAfterMagicAbsorb(vanillaDamage, modifier);
            // data to return from the event: A-1(M(A(x))
            float eventReturn = ArmorUtil.getDamageForEvent(damage, armor, toughness, 0, modifier);
            // result after vanilla logic: A(A-1(M(A(x)))
            float finalResult = CombatRules.getDamageAfterAbsorb(eventReturn, armor, toughness);
            assertThat(finalResult)
              .withFailMessage("Incorrect result for damage %.2f, armor %.2f, toughness %.2f, modifier %.2f - target %.3f, actual %.3f", damage, armor, toughness, modifier, target, finalResult)
              .isEqualTo(target, tolerance);
            //System.out.printf("Result at damage %5.2f, armor %5.2f, toughness %5.2f, modifier %6.2f - target %6.3f, actual %6.3f\n", damage, armor, toughness, modifier, target, finalResult);
          }
        }
      }
    }
  }

  @Test
  void armorAttributeOrder_testRemoveVanilla() {
    float deltaDamage = 1f;
    float maxDamage = 50;
    float deltaAttr = 1f;
    Offset<Float> tolerance = within(0.001f);
    for (float damage = 0; damage < maxDamage; damage += deltaDamage) {
      // goes to max armor
      for (float armor = 0; armor < 30; armor += deltaAttr) {
        // goes to max toughness
        for (float toughness = 0; toughness < 20; toughness += deltaAttr) {
          // goal: A(x)
          float target = CombatRules.getDamageAfterAbsorb(damage, armor, toughness);
          // goes 100% to 20% adjustment
          for (float vanillaModifier = 0; vanillaModifier < 20; vanillaModifier += deltaAttr) {
            // data to return from the event: A-1(V-1(A(x))
            float eventReturn = ArmorUtil.getDamageForEvent(damage, armor, toughness, vanillaModifier, 0);
            // result after vanilla logic: V(A(A-1(V-1(A(x)))) == V(V-1(A(x)) == A(x)
            float finalResult = CombatRules.getDamageAfterMagicAbsorb(CombatRules.getDamageAfterAbsorb(eventReturn, armor, toughness), vanillaModifier);
            assertThat(finalResult)
              .withFailMessage("Incorrect result for damage %.2f, armor %.2f, toughness %.2f, vanilla modifier %.2f - target %.3f, actual %.3f", damage, armor, toughness, vanillaModifier, target, finalResult)
              .isEqualTo(target, tolerance);
            //System.out.printf("Result at damage %5.2f, armor %5.2f, toughness %5.2f, vanilla modifier %5.2f - target %6.3f, actual %6.3f\n", damage, armor, toughness, vanillaModifier, target, finalResult);
          }
        }
      }
    }
  }

  @Test
  void armorAttributeOrder_fullIntegration() {
    float deltaDamage = 2f;
    float maxDamage = 50;
    float deltaAttr = 2.5f;
    Offset<Float> tolerance = within(0.001f);
    for (float damage = 0; damage < maxDamage; damage += deltaDamage) {
      // goes to max armor
      for (float armor = 0; armor < 30; armor += deltaAttr) {
        // goes to max toughness
        for (float toughness = 0; toughness < 20; toughness += deltaAttr) {
          // A(x)
          float vanillaDamage = CombatRules.getDamageAfterAbsorb(damage, armor, toughness);
          // goes 180% to 20% adjustment
          for (float finalModifier = -20; finalModifier < 20; finalModifier += deltaAttr) {
            // goal: M(A(x))
            float target = ArmorUtil.getDamageAfterMagicAbsorb(vanillaDamage, finalModifier);
            // goes 100% to 20% adjustment
            for (float vanillaModifier = 0; vanillaModifier < 20; vanillaModifier += deltaAttr) {
              // data to return from the event: A-1(V-1(M(A(x)))
              float eventReturn = ArmorUtil.getDamageForEvent(damage, armor, toughness, vanillaModifier, finalModifier);
              // result after vanilla logic: V(A(A-1(V-1(M(A(x))))) == V(V-1(M(A(x))) == M(A(x))
              float finalResult = CombatRules.getDamageAfterMagicAbsorb(CombatRules.getDamageAfterAbsorb(eventReturn, armor, toughness), vanillaModifier);
              assertThat(finalResult)
                .withFailMessage("Incorrect result for damage %.2f, armor %.2f, toughness %.2f, vanilla modifier %.2f, final modifier %.2f - target %.3f, actual %.3f", damage, armor, toughness, vanillaModifier, finalModifier, target, finalResult)
                .isEqualTo(target, tolerance);
              //System.out.printf("Result at damage %5.2f, armor %5.2f, toughness %5.2f, vanilla modifier %5.2f, final modifier %6.2f - target %6.3f, actual %6.3f\n", damage, armor, toughness, vanillaModifier, finalModifier, target, finalResult);
            }
          }
        }
      }
    }
  }
}
