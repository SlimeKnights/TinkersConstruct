package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.capability.PiggybackCapability;
import slimeknights.tconstruct.gadgets.capability.PiggybackHandler;
import slimeknights.tconstruct.library.client.Icons;

import javax.annotation.Nonnull;

import net.minecraft.item.Item.Properties;

public class PiggyBackPackItem extends TooltipItem {
  private static final int MAX_ENTITY_STACK = 3; // how many entities can be carried at once
  public PiggyBackPackItem(Properties props) {
    super(props);
  }

  @Override
  public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // is the chest slot empty?
    ItemStack chestArmor = playerIn.getItemBySlot(EquipmentSlotType.CHEST);

    // need enough space to exchange the chest armor
    if (chestArmor.getItem() != this && playerIn.inventory.getFreeSlot() == -1) {
      // not enough inventory space
      return ActionResultType.PASS;
    }

    // try carrying the entity
    if (this.pickupEntity(playerIn, target)) {
      // unequip old armor
      if (chestArmor.getItem() != this) {
        ItemHandlerHelper.giveItemToPlayer(playerIn, chestArmor);
        chestArmor = ItemStack.EMPTY;
      }

      // we could pick it up just fine, check if we need to "equip" more of the item
      if (chestArmor.isEmpty()) {
        playerIn.setItemSlot(EquipmentSlotType.CHEST, stack.split(1));
      } else if (chestArmor.getCount() < this.getEntitiesCarriedCount(playerIn)) {
        stack.split(1);
        chestArmor.grow(1);
      }
      // successfully picked up an entity
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.CONSUME;
  }

  private boolean pickupEntity(PlayerEntity player, Entity target) {
    if (player.getCommandSenderWorld().isClientSide || TinkerTags.EntityTypes.PIGGYBACKPACK_BLACKLIST.contains(target.getType())) {
      return false;
    }
    // silly players, clicking on entities they're already carrying or riding
    if (target.getVehicle() == player || player.getVehicle() == target) {
      return false;
    }

    int count = 0;
    Entity toRide = player;
    while (toRide.isVehicle() && count < MAX_ENTITY_STACK) {
      toRide = toRide.getPassengers().get(0);
      count++;
      // don't allow more than 1 player, that can easily cause endless loops with riding detection for some reason.
      if (toRide instanceof PlayerEntity && target instanceof PlayerEntity) {
        return false;
      }
    }

    // can only ride one entity each
    if (!toRide.isVehicle() && count < MAX_ENTITY_STACK) {
      // todo: possibly throw off all passengers of the target
      if (target.startRiding(toRide, true)) {
        if (player instanceof ServerPlayerEntity) {
          TinkerNetwork.getInstance().sendVanillaPacket(player, new SSetPassengersPacket(player));
        }
        return true;
      }
    }
    return false;
  }

  private int getEntitiesCarriedCount(LivingEntity player) {
    int count = 0;
    Entity ridden = player;
    while (ridden.isVehicle()) {
      count++;
      ridden = ridden.getPassengers().get(0);
    }

    return count;
  }

  public void matchCarriedEntitiesToCount(LivingEntity player, int maxCount) {
    int count = 0;
    // get top rider
    Entity ridden = player;
    while (ridden.isVehicle()) {
      ridden = ridden.getPassengers().get(0);
      count++;

      if (count > maxCount) {
        ridden.stopRiding();
      }
    }
  }

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entityIn;
      if (livingEntity.getItemBySlot(EquipmentSlotType.CHEST) == stack && entityIn.isVehicle()) {
        int amplifier = this.getEntitiesCarriedCount(livingEntity) - 1;
        livingEntity.addEffect(new EffectInstance(TinkerGadgets.carryEffect.get(), 2, amplifier, true, false));
      }
    }
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlot) {
    return ImmutableMultimap.of(); // no attributes, the potion effect handles them
  }

  public static class CarryPotionEffect extends TinkerEffect {
    static final String UUID = "ff4de63a-2b24-11e6-b67b-9e71128cae77";

    public CarryPotionEffect() {
      super(EffectType.NEUTRAL, true);

      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID, -0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
      return true; // check every tick
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity livingEntityIn, int p_76394_2_) {
      ItemStack chestArmor = livingEntityIn.getItemBySlot(EquipmentSlotType.CHEST);
      if (chestArmor.isEmpty() || chestArmor.getItem() != TinkerGadgets.piggyBackpack.get()) {
        TinkerGadgets.piggyBackpack.get().matchCarriedEntitiesToCount(livingEntityIn, 0);
      } else {
        TinkerGadgets.piggyBackpack.get().matchCarriedEntitiesToCount(livingEntityIn, chestArmor.getCount());
        if (!livingEntityIn.getCommandSenderWorld().isClientSide) {
          livingEntityIn.getCapability(PiggybackCapability.PIGGYBACK, null).ifPresent(PiggybackHandler::updatePassengers);
        }
      }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack matrices, int x, int y, float z) {
      this.renderHUDEffect(effect, gui, matrices, x, y, z, 1f);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack matrices, int x, int y, float z, float alpha) {
      Minecraft.getInstance().getTextureManager().bind(Icons.ICONS);
      ElementScreen element;

      switch (effect.getAmplifier()) {
        case 0:
          element = Icons.PIGGYBACK_1;
          break;
        case 1:
          element = Icons.PIGGYBACK_2;
          break;
        default:
          element = Icons.PIGGYBACK_3;
          break;
      }

      element.draw(matrices, x + 6, y + 7);
    }
  }
}
