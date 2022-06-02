package slimeknights.tconstruct.fluids.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/** Implements filling a bucket with an NBT fluid */
public class PotionBucketItem extends PotionItem {
  private final Supplier<? extends Fluid> supplier;
  public PotionBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
    super(builder);
    this.supplier = supplier;
  }

  public Fluid getFluid() {
    return supplier.get();
  }

  @Override
  public String getDescriptionId(ItemStack stack) {
    String bucketKey = PotionUtils.getPotion(stack.getTag()).getName(getDescriptionId() + ".effect.");
    if (Util.canTranslate(bucketKey)) {
      return bucketKey;
    }
    return super.getDescriptionId();
  }

  @Override
  public Component getName(ItemStack stack) {
    Potion potion = PotionUtils.getPotion(stack.getTag());
    String bucketKey = potion.getName(getDescriptionId() + ".effect.");
    if (Util.canTranslate(bucketKey)) {
      return new TranslatableComponent(bucketKey);
    }
    // default to filling with the contents
    return new TranslatableComponent(getDescriptionId() + ".contents", new TranslatableComponent(potion.getName("item.minecraft.potion.effect.")));
  }

  @Override
  public ItemStack getDefaultInstance() {
    return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    Player player = living instanceof Player p ? p : null;
    if (player instanceof ServerPlayer serverPlayer) {
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
    }

    // effects are 2x duration
    if (!level.isClientSide) {
      for (MobEffectInstance effect : PotionUtils.getMobEffects(stack)) {
        if (effect.getEffect().isInstantenous()) {
          effect.getEffect().applyInstantenousEffect(player, player, living, effect.getAmplifier(), 2.5D);
        } else {
          MobEffectInstance newEffect = new MobEffectInstance(effect);
          newEffect.duration = newEffect.duration * 5 / 2;
          living.addEffect(newEffect);
        }
      }
    }

    if (player != null) {
      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
        stack.shrink(1);
      }
    }

    if (player == null || !player.getAbilities().instabuild) {
      if (stack.isEmpty()) {
        return new ItemStack(Items.BUCKET);
      }
      if (player != null) {
        player.getInventory().add(new ItemStack(Items.BUCKET));
      }
    }
    level.gameEvent(living, GameEvent.DRINKING_FINISH, living.eyeBlockPosition());
    return stack;
  }

  @Override
  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
    PotionUtils.addPotionTooltip(pStack, pTooltip, 2.5f);
  }

  @Override
  public int getUseDuration(ItemStack pStack) {
    return 96; // 3x duration of potion bottles
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new PotionBucketWrapper(stack);
  }

  public static class PotionBucketWrapper extends FluidBucketWrapper {
    public PotionBucketWrapper(ItemStack container) {
      super(container);
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
      return new FluidStack(((PotionBucketItem)container.getItem()).getFluid(),
                            FluidAttributes.BUCKET_VOLUME, container.getTag());
    }
  }
}
