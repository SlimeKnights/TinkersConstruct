package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.gadgets.entity.shuriken.ShurikenEntityBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

public class ShurikenItem extends SnowballItem {

  private final BiFunction<World, PlayerEntity, ShurikenEntityBase> entity;

  public ShurikenItem(Properties properties, BiFunction<World, PlayerEntity, ShurikenEntityBase> entity) {
    super(properties);
    this.entity = entity;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getHeldItem(handIn);
    if (!playerIn.abilities.isCreativeMode) {
      itemStack.shrink(1);
    }

    playerIn.getCooldownTracker().setCooldown(itemStack.getItem(), 4);

    if(!worldIn.isRemote) {
      ShurikenEntityBase entity = this.entity.apply(worldIn, playerIn);
      entity.setItem(itemStack);
      entity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
      worldIn.addEntity(entity);
    }

    playerIn.addStat(Stats.ITEM_USED.get(this));
    return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
}
