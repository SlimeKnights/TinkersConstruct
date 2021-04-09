package slimeknights.tconstruct.gadgets.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import slimeknights.mantle.util.TranslationHelper;
import slimeknights.tconstruct.gadgets.entity.shuriken.ShurikenEntityBase;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.BiFunction;

public class ShurikenItem extends SnowballItem {

  private final BiFunction<World, PlayerEntity, ShurikenEntityBase> entity;

  public ShurikenItem(Settings properties, BiFunction<World, PlayerEntity, ShurikenEntityBase> entity) {
    super(properties);
    this.entity = entity;
  }

  @Override
  public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getStackInHand(handIn);
    if (!playerIn.abilities.creativeMode) {
      itemStack.decrement(1);
    }

    playerIn.getItemCooldownManager().set(itemStack.getItem(), 4);

    if(!worldIn.isClient) {
      ShurikenEntityBase entity = this.entity.apply(worldIn, playerIn);
      entity.setItem(itemStack);
      entity.setProperties(playerIn, playerIn.pitch, playerIn.yaw, 0.0F, 1.5F, 1.0F);
      worldIn.spawnEntity(entity);
    }

    playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
    return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
    TranslationHelper.addOptionalTooltip(stack, tooltip);
    super.appendTooltip(stack, worldIn, tooltip, flagIn);
  }
}
