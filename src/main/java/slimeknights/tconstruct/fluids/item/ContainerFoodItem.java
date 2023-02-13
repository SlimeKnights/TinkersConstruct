package slimeknights.tconstruct.fluids.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.fluids.util.ConstantFluidContainerWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ContainerFoodItem extends Item {
  public ContainerFoodItem(Properties props) {
    super(props);
  }

  @Override
  public int getUseDuration(ItemStack pStack) {
    return 32;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack pStack) {
    return UseAnim.DRINK;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    // add effects to the tooltip, code based on potion items
    for (Pair<MobEffectInstance, Float> pair : Objects.requireNonNull(stack.getItem().getFoodProperties()).getEffects()) {
      MobEffectInstance effect = pair.getFirst();
      if (effect != null) {
        MutableComponent mutable = new TranslatableComponent(effect.getDescriptionId());
        if (effect.getAmplifier() > 0) {
          mutable = new TranslatableComponent("potion.withAmplifier", mutable, new TranslatableComponent("potion.potency." + effect.getAmplifier()));
        }
        if (effect.getDuration() > 20) {
          mutable = new TranslatableComponent("potion.withDuration", mutable, MobEffectUtil.formatDuration(effect, 1.0f));
        }
        tooltip.add(mutable.withStyle(effect.getEffect().getCategory().getTooltipFormatting()));
      }
    }
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    ItemStack container = stack.getContainerItem();
    ItemStack result = super.finishUsingItem(stack, level, living);
    Player player = living instanceof Player p ? p : null;
    if (player == null || !player.getAbilities().instabuild) {
      if (result.isEmpty()) {
        return container.copy();
      }
      if (player != null) {
        if (!player.getInventory().add(container.copy())) {
          player.drop(stack, false);
        }
      }
    }
    return result;
  }

  public static class FluidContainerFoodItem extends ContainerFoodItem {
    private final Supplier<FluidStack> fluid;
    public FluidContainerFoodItem(Properties props, Supplier<FluidStack> fluid) {
      super(props);
      this.fluid = fluid;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
      return new ConstantFluidContainerWrapper(fluid.get(), stack);
    }
  }
}
