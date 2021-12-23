package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class LuckModifier extends Modifier implements IArmorLootModifier {
  public LuckModifier() {
    super(0x345EC3);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    // displays special names for the 3 levels
    if (level <= 3) {
      return applyStyle(new TranslationTextComponent(getTranslationKey() + "." + level));
    }
    return super.getDisplayName(level);
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    return super.getDisplayName(tool, level);
  }

  @Override
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.FORTUNE, level);
  }

  @Override
  public int getLootingValue(IModifierToolStack tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting + level;
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorLootModifier.class, this);
  }
}
