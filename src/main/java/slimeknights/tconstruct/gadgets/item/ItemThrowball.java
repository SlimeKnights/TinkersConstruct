package slimeknights.tconstruct.gadgets.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.library.TinkerRegistry;

// we derive from snowball so that we can detect all "rightclick like snowball throws" items via instanceof
public class ItemThrowball extends ItemSnowball {

  public ItemThrowball() {
    this.setMaxStackSize(16);
    this.setHasSubtypes(true);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    for(ThrowballType type : ThrowballType.values()) {
      subItems.add(new ItemStack(this, 1, type.ordinal()));
    }
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    if(!playerIn.capabilities.isCreativeMode) {
      --itemStackIn.stackSize;
    }

    worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

    if(!worldIn.isRemote) {
      ThrowballType type = ThrowballType.values()[itemStackIn.getMetadata() % ThrowballType.values().length];
      launchThrowball(worldIn, playerIn, type, hand);
    }

    playerIn.addStat(StatList.getObjectUseStats(this));
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }

  public void launchThrowball(World world, EntityPlayer player, ThrowballType type, EnumHand hand) {
    EntityThrowball entity = new EntityThrowball(world, player, type);
    entity.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.1F, 0.5F);
    world.spawnEntity(entity);
  }

  @Nonnull
  @Override
  public String getUnlocalizedName(ItemStack stack) {
    int meta = stack.getMetadata(); // should call getMetadata below
    if(meta < ThrowballType.values().length) {
      return super.getUnlocalizedName(stack) + "." + LocUtils.makeLocString(ThrowballType.values()[meta].name());
    }
    else {
      return super.getUnlocalizedName(stack);
    }
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    if(I18n.canTranslate(this.getUnlocalizedName(stack) + ".tooltip")) {
      tooltip.add(TextFormatting.GRAY.toString() + LocUtils.translateRecursive(this.getUnlocalizedName(stack) + ".tooltip"));
    }
  }

  public enum ThrowballType {
    GLOW,
    EFLN
  }
}
