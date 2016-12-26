package slimeknights.tconstruct.gadgets.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.item.ItemArmorTooltip;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.BlockSlime.SlimeType;

public class ItemSlimeBoots extends ItemArmorTooltip {

  // todo: determine if this needs toughness
  public static ArmorMaterial SLIME_MATERIAL = EnumHelper.addArmorMaterial("SLIME", Util.resource("slime"), 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.BLOCK_SLIME_PLACE, 0);

  public ItemSlimeBoots() {
    super(SLIME_MATERIAL, 0, EntityEquipmentSlot.FEET);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setMaxStackSize(1);
    this.hasSubtypes = true;
  }

  @Override
  public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
    // can be worn as boots
    return armorType == EntityEquipmentSlot.FEET;
  }

  // equipping with rightclick
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

    if(itemstack == null) {
      player.setItemStackToSlot(EntityEquipmentSlot.FEET, stack.copy());
      stack.stackSize--;
    }

    return stack;
  }

  /**
   * Return whether the specified armor ItemStack has a color.
   */
  @Override
  public boolean hasColor(ItemStack stack) {
    return true;
  }

  /**
   * Return the color for the specified armor ItemStack.
   */
  @Override
  public int getColor(ItemStack stack) {
    SlimeType type = SlimeType.fromMeta(stack.getMetadata());
    return type.getBallColor();
  }

  /**
   * Determines if this armor will be rendered with the secondary 'overlay'
   * texture. If this is true, the first texture will be rendered using a tint
   * of the color specified by getColor(ItemStack)
   *
   * @param stack
   *          The stack
   * @return true/false
   */
  @Override
  public boolean hasOverlay(ItemStack stack) {
    // use an overlay so we get a tint
    return true;
  }

  @Nonnull
  @Override
  public String getUnlocalizedName(ItemStack stack) {
    int meta = stack.getMetadata(); // should call getMetadata below
    if(meta < SlimeType.values().length) {
      return super.getUnlocalizedName(stack) + "." + LocUtils.makeLocString(SlimeType.values()[meta].name());
    }
    else {
      return super.getUnlocalizedName(stack);
    }
  }

  /**
   * returns a list of items with the same ID, but different meta (eg: dye
   * returns 16 items)
   */
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    for(SlimeType type : SlimeType.values()) {
      subItems.add(new ItemStack(this, 1, type.getMeta()));
    }
  }

  // RUBBERY BOUNCY BOUNCERY WOOOOO
  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    EntityLivingBase entity = event.getEntityLiving();
    if(entity == null) {
      return;
    }
    ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    if(feet == null || feet.getItem() != this) {
      return;
    }

    // thing is wearing slime boots. let's get bouncyyyyy
    if(!entity.isSneaking() && event.getDistance() > 2) {
      event.setDamageMultiplier(0);
      if(entity.worldObj.isRemote) {
        entity.motionY *= -0.9;
        //entity.motionY = event.distance / 15;
        //entity.motionX = entity.posX - entity.lastTickPosX;
        //entity.motionZ = entity.posZ - entity.lastTickPosZ;
        //event.entityLiving.motionY *= -1.2;
        //event.entityLiving.motionY += 0.8;
        event.getEntityLiving().isAirBorne = true;
        event.getEntityLiving().onGround = false;
        double f = 0.91d + 0.04d;
        //System.out.println((entityLiving.worldObj.isRemote ? "client: " : "server: ") + entityLiving.motionX);
        // only slow down half as much when bouncing
        entity.motionX /= f;
        entity.motionZ /= f;
      }
      else {
        event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
      }
      entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
      SlimeBounceHandler.addBounceHandler(entity, entity.motionY);
    }
    else if(!entity.worldObj.isRemote && entity.isSneaking()) {
      event.setDamageMultiplier(0.1f);
    }
  }
}
