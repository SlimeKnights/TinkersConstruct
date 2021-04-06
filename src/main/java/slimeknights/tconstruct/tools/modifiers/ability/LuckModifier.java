package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Random;
import java.util.function.BiConsumer;

public class LuckModifier extends IncrementalModifier {
  /** Random instance we can freely change the seed on */
  private static final Random LOOTING_RANDOM = new Random();

  public LuckModifier() {
    super(0x345EC3);
    MinecraftForge.EVENT_BUS.addListener(this::onLooting);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    IFormattableTextComponent name;
    if (level > 3) {
      name = new TranslationTextComponent(getTranslationKey() + ".beyond");
    } else {
      name = new TranslationTextComponent(getTranslationKey() + "." + level);
    }
    return name.modifyStyle(style -> style.setColor(Color.fromInt(getColor())));
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    int neededPerLevel = ModifierRecipeLookup.getNeededPerLevel(this);
    ITextComponent name = this.getDisplayName(level);
    if (neededPerLevel > 0) {
      // display the numeral based on percentage into the level
      int amount = getAmount(tool);
      int dispLevel = 3 + ((amount - neededPerLevel) * 3 / neededPerLevel);
      // we only have names for 1 to 4, so display 5 and onwards as 4 with a higher number
      if (level > 4) {
        dispLevel += (level - 4) * 3;
      }
      // finally build the string
      IFormattableTextComponent formattable = name.deepCopy();
      formattable = formattable.appendString(" ").append(new TranslationTextComponent(KEY_LEVEL + dispLevel));
      // if not at a full level, add that info too
      if (amount < neededPerLevel) {
        return formattable.appendString(": " + amount + " / " + neededPerLevel);
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
  private void onLooting(LootingLevelEvent event) {
    // TODO: make common modifier event if this becomes used elsewhere
    // must be an attacker with our tool
    DamageSource damageSource = event.getDamageSource();
    if (damageSource == null) {
      return;
    }
    Entity source = event.getDamageSource().getTrueSource();
    if (source instanceof LivingEntity) {
      ItemStack held = ((LivingEntity)source).getHeldItemMainhand();
      if (TinkerTags.Items.MODIFIABLE.contains(held.getItem())) {
        // non broken, has modifier
        ToolStack tool = ToolStack.from(held);
        if (!tool.isBroken()) {
          int level = tool.getModifierLevel(this);
          if (level > 0) {
            // we use a random instance seeded from the current game time
            // its important so the value is consistent between the multiple calls of this event in one kill
            LOOTING_RANDOM.setSeed(source.getEntityWorld().getGameTime());
            // calculate the effective level from the modifier
            event.setLootingLevel(getEffectiveLevel(tool, level, LOOTING_RANDOM));
          }
        }
      }
    }
  }
}
