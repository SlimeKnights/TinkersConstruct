package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class LuckModifier extends Modifier implements IArmorLootModifier {
  @Override
  public Component getDisplayName(int level) {
    // displays special names for the 3 levels
    if (level <= 3) {
      return applyStyle(new TranslatableComponent(getTranslationKey() + "." + level));
    }
    return super.getDisplayName(level);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    return super.getDisplayName(tool, level);
  }

  @Override
  public void applyHarvestEnchantments(IToolStackView tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.BLOCK_FORTUNE, level);
  }

  @Override
  public int getLootingValue(IToolStackView tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting + level;
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorLootModifier.class, this);
  }
}
