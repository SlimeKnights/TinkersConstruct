package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.common.Sounds;
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
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    world.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SHURIKEN_THROW.getSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
    player.getCooldowns().addCooldown(stack.getItem(), 4);
    if(!world.isClientSide()) {
      ShurikenEntityBase entity = this.entity.apply(world, player);
      entity.setItem(stack);
      entity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
      world.addFreshEntity(entity);
    }
    player.awardStat(Stats.ITEM_USED.get(this));
    if (!player.abilities.instabuild) {
      stack.shrink(1);
    }

    return ActionResult.sidedSuccess(stack, world.isClientSide());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }
}
