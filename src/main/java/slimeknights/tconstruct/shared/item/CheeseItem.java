package slimeknights.tconstruct.shared.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CheeseItem extends Item {
  public static final Component TOOLTIP = TConstruct.makeTranslation("item", "cheese.tooltip");
  public CheeseItem(Properties pProperties) {
    super(pProperties);
  }

  /** Removes a random effect from the given entity */
  public static void removeRandomEffect(LivingEntity living) {
    if (!living.level().isClientSide) {
      Collection<MobEffectInstance> effects = living.getActiveEffects();
      if (!effects.isEmpty()) {
        // don't remove effects that are not milk removable
        List<MobEffect> removable = effects.stream().filter(effect -> effect.getCurativeItems().stream().anyMatch(item -> item.is(Items.MILK_BUCKET))).map(MobEffectInstance::getEffect).toList();
        if (!removable.isEmpty()) {
          living.removeEffect(removable.get(living.getRandom().nextInt(removable.size())));
        }
      }
    }
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    removeRandomEffect(living);
    return super.finishUsingItem(stack, level, living);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
    tooltip.add(TOOLTIP);
  }
}
