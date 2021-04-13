package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Random;
import java.util.function.BiConsumer;

public class LuckModifier extends IncrementalModifier {
  /** Random instance we can freely change the seed on */
  private static final Random LOOTING_RANDOM = new Random();

  public LuckModifier() {
    super(0x345EC3);
//    MinecraftForge.EVENT_BUS.addListener(this::onLooting);
  }

  @Override
  public Text getDisplayName(int level) {
    MutableText name;
    if (level > 3) {
      name = new TranslatableText(getTranslationKey() + ".beyond");
    } else {
      name = new TranslatableText(getTranslationKey() + "." + level);
    }
    return name.styled(style -> style.withColor(TextColor.fromRgb(getColor())));
  }

  @Override
  public Text getDisplayName(IModifierToolStack tool, int level) {
    int neededPerLevel = ModifierRecipeLookup.getNeededPerLevel(this);
    Text name = this.getDisplayName(level);
    if (neededPerLevel > 0) {
      // display the numeral based on percentage into the level
      int amount = getAmount(tool);
      int dispLevel = 3 + ((amount - neededPerLevel) * 3 / neededPerLevel);
      // we only have names for 1 to 4, so display 5 and onwards as 4 with a higher number
      if (level > 4) {
        dispLevel += (level - 4) * 3;
      }
      // finally build the string
      MutableText formattable = name.shallowCopy();
      formattable = formattable.append(" ").append(new TranslatableText(KEY_LEVEL + dispLevel));
      // if not at a full level, add that info too
      if (amount < neededPerLevel) {
        return formattable.append(": " + amount + " / " + neededPerLevel);
      }
      return formattable;
    }
    return name;
  }

  /**
   * Gets the looting level for the given modifier level and tool data
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param random   Random instance, for partial levels
   * @return  Looting level
   */
  private int getEffectiveLevel(IModifierToolStack tool, int level, Random random) {
    // each level of the modifier is worth 3 levels of the enchant
    int applyLevel = level * 3;
    int neededPerSlot = ModifierRecipeLookup.getNeededPerLevel(this);
    if (neededPerSlot > 0) {
      // if we just have a partial amount, every third is worth 1 level
      int neededPerLevel = neededPerSlot / 3;
      int amount = getAmount(tool);

      // 0 to 33%: lose 2 levels
      if (amount < neededPerLevel) {
        applyLevel -= 2;
      } else {
        // rescale to be from 0 to 67%
        amount -= neededPerLevel;
        // 33% to 66%: lose 1 level
        if (amount < neededPerLevel) {
          applyLevel--;
        } else {
          // 66% to 100%, further rescale amount
          amount -= neededPerLevel;
        }
      }

      // for the remainder, if we don't have a full level left decrease it
      if (amount < neededPerLevel && amount < random.nextInt(neededPerLevel)) {
        applyLevel--;
      }
    }
    return applyLevel;
  }

  @Override
  public void applyEnchantments(IModifierToolStack tool, int level, BiConsumer<Enchantment,Integer> consumer) {
    // apply level if we still have any
    int applyLevel = getEffectiveLevel(tool, level, RANDOM);
    if (applyLevel > 0) {
      consumer.accept(Enchantments.FORTUNE, applyLevel);
    }
  }

  /** Applies the looting bonus for this modifier */
  //TODO: PORTING
//  private void onLooting(LootingLevelEvent event) {
//    // TODO: make common modifier event if this becomes used elsewhere
//    // must be an attacker with our tool
//    DamageSource damageSource = event.getDamageSource();
//    if (damageSource == null) {
//      return;
//    }
//    Entity source = event.getDamageSource().getAttacker();
//    if (source instanceof LivingEntity) {
//      ItemStack held = ((LivingEntity)source).getMainHandStack();
//      if (TinkerTags.Items.MODIFIABLE.contains(held.getItem())) {
//        // non broken, has modifier
//        ToolStack tool = ToolStack.from(held);
//        if (!tool.isBroken()) {
//          int level = tool.getModifierLevel(this);
//          if (level > 0) {
//            // we use a random instance seeded from the current game time
//            // its important so the value is consistent between the multiple calls of this event in one kill
//            LOOTING_RANDOM.setSeed(source.getEntityWorld().getTime());
//            // calculate the effective level from the modifier
//            event.setLootingLevel(getEffectiveLevel(tool, level, LOOTING_RANDOM));
//          }
//        }
//      }
//    }
//  }
}
