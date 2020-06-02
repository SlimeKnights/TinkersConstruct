package slimeknights.tconstruct.gadgets.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.library.capability.piggyback.ITinkerPiggyback;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.effect.TinkerEffect;
import slimeknights.tconstruct.library.network.TinkerNetwork;

import javax.annotation.Nonnull;

public class PiggyBackPackItem extends ArmorTooltipItem {

  // todo: turn this into a config
  private static final int MAX_ENTITY_STACK = 3; // how many entities can be carried at once

  private static final IArmorMaterial PIGGYBACK = new IArmorMaterial() {
    private final Ingredient empty_repair_material = Ingredient.fromItems(Items.AIR);

    @Override
    public int getDurability(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getEnchantability() {
      return 0;
    }

    @Override
    public SoundEvent getSoundEvent() {
      return SoundEvents.BLOCK_SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.empty_repair_material;
    }

    @Override
    public String getName() {
      return Util.resource("piggyback");
    }

    @Override
    public float getToughness() {
      return 0;
    }
  };

  public PiggyBackPackItem() {
    super(PIGGYBACK, EquipmentSlotType.CHEST, (new Properties()).group(TinkerRegistry.tabGadgets).maxStackSize(16));
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    return new ActionResult<>(ActionResultType.PASS, itemStackIn);
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    // is the chest slot empty?
    ItemStack chestArmor = playerIn.getItemStackFromSlot(this.slot);

    // need enough space to exchange the chest armor
    if (chestArmor.getItem() != this && playerIn.inventory.getFirstEmptyStack() == -1) {
      // not enough inventory space
      return false;
    }

    if (this.pickupEntity(playerIn, target)) {
      // unequip old armor
      if (chestArmor.getItem() != this) {
        ItemHandlerHelper.giveItemToPlayer(playerIn, chestArmor);
        chestArmor = ItemStack.EMPTY;
      }

      // we could pick it up just fine, check if we need to "equip" more of the item
      if (chestArmor.isEmpty()) {
        playerIn.setItemStackToSlot(this.slot, stack.split(1));
      } else if (chestArmor.getCount() < this.getEntitiesCarriedCount(playerIn)) {
        stack.split(1);
        chestArmor.grow(1);
      }
      // successfully picked up an entity
      return true;
    }

    return false;
  }

  private boolean pickupEntity(PlayerEntity player, Entity target) {
    if (player.getEntityWorld().isRemote) {
      return false;
    }
    // silly players, clicking on entities they're already carrying or riding
    if (target.getRidingEntity() == player || player.getRidingEntity() == target) {
      return false;
    }

    int count = 0;
    Entity toRide = player;
    while (toRide.isBeingRidden() && count < MAX_ENTITY_STACK) {
      toRide = toRide.getPassengers().get(0);
      count++;
      // don't allow more than 1 player, that can easily cause endless loops with riding detection for some reason.
      if (toRide instanceof PlayerEntity && target instanceof PlayerEntity) {
        return false;
      }
    }

    // can only ride one entity each
    if (!toRide.isBeingRidden() && count < MAX_ENTITY_STACK) {
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
    while (ridden.isBeingRidden()) {
      count++;
      ridden = ridden.getPassengers().get(0);
    }

    return count;
  }

  public void matchCarriedEntitiesToCount(LivingEntity player, int maxCount) {
    int count = 0;
    // get top rider
    Entity ridden = player;
    while (ridden.isBeingRidden()) {
      ridden = ridden.getPassengers().get(0);
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
      if (livingEntity.getItemStackFromSlot(EquipmentSlotType.CHEST) == stack && entityIn.isBeingRidden()) {
        int amplifier = this.getEntitiesCarriedCount(livingEntity) - 1;
        livingEntity.addPotionEffect(new EffectInstance(CarryPotionEffect.INSTANCE, 1, amplifier, true, false));
      }
    }
  }

  public static class CarryPotionEffect extends TinkerEffect {

    public static final CarryPotionEffect INSTANCE = new CarryPotionEffect();
    static final String UUID = "ff4de63a-2b24-11e6-b67b-9e71128cae77";

    CarryPotionEffect() {
      super(Util.getResource("carry"), EffectType.NEUTRAL, true);

      this.addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, UUID, -0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
      return true; // check every tick
    }

    @Override
    public void performEffect(@Nonnull LivingEntity livingEntityIn, int p_76394_2_) {
      ItemStack chestArmor = livingEntityIn.getItemStackFromSlot(EquipmentSlotType.CHEST);
      if (chestArmor.isEmpty() || chestArmor.getItem() != GadgetItems.piggy_backpack.get()) {
        GadgetItems.piggy_backpack.get().matchCarriedEntitiesToCount(livingEntityIn, 0);
      } else {
        GadgetItems.piggy_backpack.get().matchCarriedEntitiesToCount(livingEntityIn, chestArmor.getCount());
        if (!livingEntityIn.getEntityWorld().isRemote) {
          if (livingEntityIn.getCapability(CapabilityTinkerPiggyback.PIGGYBACK, null).isPresent()) {
            ITinkerPiggyback piggyback = livingEntityIn.getCapability(CapabilityTinkerPiggyback.PIGGYBACK, null).orElse(null);
            if (piggyback != null) {
              piggyback.updatePassengers();
            }
          }
        }
      }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, int x, int y, float z) {
      this.renderHUDEffect(effect, gui, x, y, z, 1f);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
      Minecraft.getInstance().getTextureManager().bindTexture(Icons.ICON);
      ElementScreen element;

      switch (effect.getAmplifier()) {
        case 0:
          element = Icons.ICON_PIGGYBACK_1;
          break;
        case 1:
          element = Icons.ICON_PIGGYBACK_2;
          break;
        default:
          element = Icons.ICON_PIGGYBACK_3;
          break;
      }

      element.draw(x + 6, y + 7);
    }
  }
}
