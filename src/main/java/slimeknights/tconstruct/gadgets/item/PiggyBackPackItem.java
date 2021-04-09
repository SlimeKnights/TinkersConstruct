package slimeknights.tconstruct.gadgets.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.library.capability.piggyback.ITinkerPiggyback;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.effect.TinkerEffect;
import slimeknights.tconstruct.library.network.TinkerNetwork;

import org.jetbrains.annotations.NotNull;

public class PiggyBackPackItem extends ArmorTooltipItem {

  // todo: turn this into a config
  private static final int MAX_ENTITY_STACK = 3; // how many entities can be carried at once

  private static final ArmorMaterial PIGGYBACK = new ArmorMaterial() {
    @Override
    public int getDurability(EquipmentSlot slotIn) {
      return 0;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slotIn) {
      return 0;
    }

    @Override
    public int getEnchantability() {
      return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
      return SoundEvents.BLOCK_SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public String getName() {
      return Util.resource("piggyback");
    }

    @Override
    public float getToughness() {
      return 0;
    }

    @Override
    public float getKnockbackResistance() {
      return 0;
    }
  };

  public PiggyBackPackItem() {
    super(PIGGYBACK, EquipmentSlot.CHEST, (new Settings()).group(TinkerGadgets.TAB_GADGETS).maxCount(16));
  }

  @NotNull
  @Override
  public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
    ItemStack itemStackIn = playerIn.getStackInHand(hand);
    return new TypedActionResult<>(ActionResult.PASS, itemStackIn);
  }

  @Override
  public ActionResult useOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // is the chest slot empty?
    ItemStack chestArmor = playerIn.getEquippedStack(this.slot);

    // need enough space to exchange the chest armor
    if (chestArmor.getItem() != this && playerIn.inventory.getEmptySlot() == -1) {
      // not enough inventory space
      return ActionResult.PASS;
    }

    if (this.pickupEntity(playerIn, target)) {
      // unequip old armor
      if (chestArmor.getItem() != this) {
        ItemHandlerHelper.giveItemToPlayer(playerIn, chestArmor);
        chestArmor = ItemStack.EMPTY;
      }

      // we could pick it up just fine, check if we need to "equip" more of the item
      if (chestArmor.isEmpty()) {
        playerIn.equipStack(this.slot, stack.split(1));
      } else if (chestArmor.getCount() < this.getEntitiesCarriedCount(playerIn)) {
        stack.split(1);
        chestArmor.increment(1);
      }
      // successfully picked up an entity
      return ActionResult.SUCCESS;
    }

    return ActionResult.PASS;
  }

  private boolean pickupEntity(PlayerEntity player, Entity target) {
    if (player.getEntityWorld().isClient) {
      return false;
    }
    // silly players, clicking on entities they're already carrying or riding
    if (target.getVehicle() == player || player.getVehicle() == target) {
      return false;
    }

    int count = 0;
    Entity toRide = player;
    while (toRide.hasPassengers() && count < MAX_ENTITY_STACK) {
      toRide = toRide.getPassengerList().get(0);
      count++;
      // don't allow more than 1 player, that can easily cause endless loops with riding detection for some reason.
      if (toRide instanceof PlayerEntity && target instanceof PlayerEntity) {
        return false;
      }
    }

    // can only ride one entity each
    if (!toRide.hasPassengers() && count < MAX_ENTITY_STACK) {
      // todo: possibly throw off all passengers of the target
      if (target.startRiding(toRide, true)) {
        if (player instanceof ServerPlayerEntity) {
          TinkerNetwork.getInstance().sendVanillaPacket(player, new EntityPassengersSetS2CPacket(player));
        }
        return true;
      }
    }
    return false;
  }

  private int getEntitiesCarriedCount(LivingEntity player) {
    int count = 0;
    Entity ridden = player;
    while (ridden.hasPassengers()) {
      count++;
      ridden = ridden.getPassengerList().get(0);
    }

    return count;
  }

  public void matchCarriedEntitiesToCount(LivingEntity player, int maxCount) {
    int count = 0;
    // get top rider
    Entity ridden = player;
    while (ridden.hasPassengers()) {
      ridden = ridden.getPassengerList().get(0);
      count++;

      if (count > maxCount) {
        ridden.stopRiding();
      }
    }
  }

  /**
   * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
   * update it's contents.
   */
  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entityIn;
      if (livingEntity.getEquippedStack(EquipmentSlot.CHEST) == stack && entityIn.hasPassengers()) {
        int amplifier = this.getEntitiesCarriedCount(livingEntity) - 1;
        livingEntity.addStatusEffect(new StatusEffectInstance(TinkerGadgets.carryEffect.get(), 1, amplifier, true, false));
      }
    }
  }

  public static class CarryPotionEffect extends TinkerEffect {
    static final String UUID = "ff4de63a-2b24-11e6-b67b-9e71128cae77";

    public CarryPotionEffect() {
      super(StatusEffectType.NEUTRAL, true);

      this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, UUID, -0.05D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
      return true; // check every tick
    }

    @Override
    public void applyUpdateEffect(@NotNull LivingEntity livingEntityIn, int p_76394_2_) {
      ItemStack chestArmor = livingEntityIn.getEquippedStack(EquipmentSlot.CHEST);
      if (chestArmor.isEmpty() || chestArmor.getItem() != TinkerGadgets.piggyBackpack.get()) {
        TinkerGadgets.piggyBackpack.get().matchCarriedEntitiesToCount(livingEntityIn, 0);
      } else {
        TinkerGadgets.piggyBackpack.get().matchCarriedEntitiesToCount(livingEntityIn, chestArmor.getCount());
        if (!livingEntityIn.getEntityWorld().isClient) {
          livingEntityIn.getCapability(CapabilityTinkerPiggyback.PIGGYBACK, null).ifPresent(ITinkerPiggyback::updatePassengers);
        }
      }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderInventoryEffect(StatusEffectInstance effect, AbstractInventoryScreen<?> gui, MatrixStack matrices, int x, int y, float z) {
      this.renderHUDEffect(effect, gui, matrices, x, y, z, 1f);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderHUDEffect(StatusEffectInstance effect, DrawableHelper gui, MatrixStack matrices, int x, int y, float z, float alpha) {
      MinecraftClient.getInstance().getTextureManager().bindTexture(Icons.ICONS);
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
