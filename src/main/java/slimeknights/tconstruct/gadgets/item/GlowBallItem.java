package slimeknights.tconstruct.gadgets.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;

import javax.annotation.Nullable;
import java.util.List;

public class GlowBallItem extends SnowballItem {

  public GlowBallItem() {
    super((new Settings()).maxCount(16).group(TinkerGadgets.TAB_GADGETS));
  }

  @Override
  public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getStackInHand(handIn);
    if (!playerIn.abilities.creativeMode) {
      itemstack.decrement(1);
    }

    worldIn.playSound((PlayerEntity) null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
    if (!worldIn.isClient) {
      GlowballEntity glowballEntity = new GlowballEntity(worldIn, playerIn);
      glowballEntity.setItem(itemstack);
      glowballEntity.setProperties(playerIn, playerIn.pitch, playerIn.yaw, 0.0F, 1.5F, 1.0F);
      worldIn.spawnEntity(glowballEntity);
    }

    playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
    return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.appendTooltip(stack, worldIn, tooltip, flagIn);
  }
}
