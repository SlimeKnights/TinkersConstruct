package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.item.ItemArmorTooltip;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.library.capability.ITinkerPiggyback;
import slimeknights.tconstruct.library.capability.TinkerPiggybackSerializer;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.potion.TinkerPotion;

public class ItemPiggybackPack extends ItemArmorTooltip {

  // todo: turn this into a config
  private static final int MAX_ENTITY_STACK = 3; // how many entities can be carried at once
  public static ArmorMaterial PIGGYBACK_MATERIAL = EnumHelper.addArmorMaterial("PIGGYBACK", Util.resource("piggyback"), 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.BLOCK_SLIME_PLACE, 0);

  public ItemPiggybackPack() {
    super(PIGGYBACK_MATERIAL, 0, EntityEquipmentSlot.CHEST);

    this.setMaxStackSize(16);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
    // is the chest slot empty?
    ItemStack chestArmor = playerIn.getItemStackFromSlot(this.armorType);

    // need enough space to exchange the chest armor
    if(chestArmor != null && chestArmor.getItem() != this && playerIn.inventory.getFirstEmptyStack() == -1) {
      // not enough inventory space
      return false;
    }

    if(pickupEntity(playerIn, target)) {
      // unequip old armor
      if(chestArmor != null && chestArmor.getItem() != this) {
        ItemHandlerHelper.giveItemToPlayer(playerIn, chestArmor);
        chestArmor = null;
      }

      // we could pick it up just fine, check if we need to "equip" more of the item
      if(chestArmor == null) {
        playerIn.setItemStackToSlot(this.armorType, stack.splitStack(1));
      } else if(chestArmor.stackSize < getEntitiesCarriedCount(playerIn)) {
        stack.splitStack(1);
        chestArmor.stackSize++;
      }
      // successfully picked up an entity
      return true;
    }

    return false;
  }

  public boolean pickupEntity(EntityPlayer player, Entity target) {
    // silly players, clicking on entities they're already carrying
    if(target.getRidingEntity() == player) {
      return false;
    }

    int count = 0;
    Entity toRide = player;
    while(toRide.isBeingRidden() && count < MAX_ENTITY_STACK) {
      toRide = toRide.getPassengers().get(0);
      count++;
    }

    // can only ride one entity each
    if(!toRide.isBeingRidden() && count < MAX_ENTITY_STACK) {
      // todo: possibly throw off all passengers of the target
      return target.startRiding(toRide, true);
    }
    return false;
  }

  public int getEntitiesCarriedCount(EntityLivingBase player) {
    int count = 0;
    Entity ridden = player;
    while(ridden.isBeingRidden()) {
      count++;
      ridden = ridden.getPassengers().get(0);
    }

    return count;
  }

  public void matchCarriedEntitiesToCount(EntityLivingBase player, int maxCount) {
    int count = 0;
    // get top rider
    Entity ridden = player;
    while(ridden.isBeingRidden()) {
      ridden = ridden.getPassengers().get(0);
      count++;

      if(count > maxCount) {
        ridden.dismountRidingEntity();
      }
    }

  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if(entityIn instanceof EntityLivingBase) {
      EntityLivingBase entityLivingBase = (EntityLivingBase) entityIn;
      if(entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == stack && entityIn.isBeingRidden()) {
        int amplifier = getEntitiesCarriedCount(entityLivingBase) - 1;
        entityLivingBase.addPotionEffect(new PotionEffect(CarryPotionEffect.INSTANCE, 1, amplifier, true, false));
      }
    }
  }

  @SubscribeEvent
  public void attachCapability(AttachCapabilitiesEvent.Entity event) {
    if(event.getEntity() instanceof EntityPlayer) {
      event.addCapability(Util.getResource("piggyback"), new TinkerPiggybackSerializer((EntityPlayer)event.getEntity()));
    }
  }

  public static class CarryPotionEffect extends TinkerPotion {

    public static final CarryPotionEffect INSTANCE = new CarryPotionEffect();
    public static final String UUID = "ff4de63a-2b24-11e6-b67b-9e71128cae77";

    protected CarryPotionEffect() {
      super(Util.getResource("carry"), false, true);

      this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, UUID, -0.05D, 2);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
      return true; // check every tick
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int p_76394_2_) {
      ItemStack chestArmor = entityLivingBaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
      if(chestArmor == null) {
        TinkerGadgets.piggybackPack.matchCarriedEntitiesToCount(entityLivingBaseIn, 0);
      }
      else if(chestArmor.getItem() == TinkerGadgets.piggybackPack) {
        TinkerGadgets.piggybackPack.matchCarriedEntitiesToCount(entityLivingBaseIn, chestArmor.stackSize);
        if(!entityLivingBaseIn.worldObj.isRemote) {
          if(entityLivingBaseIn.hasCapability(CapabilityTinkerPiggyback.PIGGYBACK, null)) {
            ITinkerPiggyback piggyback = entityLivingBaseIn.getCapability(CapabilityTinkerPiggyback.PIGGYBACK, null);
            piggyback.updatePassengers();
          }
        }
      }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      renderHUDEffect(x, y, effect, mc, 1f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      mc.getTextureManager().bindTexture(Icons.ICON);
      GuiElement element;
      switch(effect.getAmplifier()) {
        case 0: element = Icons.ICON_PIGGYBACK_1; break;
        case 1: element = Icons.ICON_PIGGYBACK_2; break;
        case 2: element = Icons.ICON_PIGGYBACK_3; break;
        default: element = Icons.ICON_PIGGYBACK_3; break;
      }
      element.draw(x+6, y+7);
    }
  }
}
