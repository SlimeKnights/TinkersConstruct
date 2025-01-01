package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
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
import java.util.function.Consumer;

public class PiggyBackPackItem extends TooltipItem {
  private static final int MAX_ENTITY_STACK = 3; // how many entities can be carried at once
  public PiggyBackPackItem(Properties props) {
    super(props);
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
    // is the chest slot empty?
    ItemStack chestArmor = playerIn.getItemBySlot(EquipmentSlot.CHEST);

    // need enough space to exchange the chest armor
    if (chestArmor.getItem() != this && playerIn.getInventory().getFreeSlot() == -1) {
      // not enough inventory space
      return InteractionResult.PASS;
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
        playerIn.setItemSlot(EquipmentSlot.CHEST, stack.split(1));
      } else if (chestArmor.getCount() < this.getEntitiesCarriedCount(playerIn)) {
        stack.split(1);
        chestArmor.grow(1);
      }
      // successfully picked up an entity
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.CONSUME;
  }

  private boolean pickupEntity(Player player, Entity target) {
    if (player.getCommandSenderWorld().isClientSide || target.getType().is(TinkerTags.EntityTypes.PIGGYBACKPACK_BLACKLIST)) {
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
      if (toRide instanceof Player && target instanceof Player) {
        return false;
      }
    }

    // can only ride one entity each
    if (!toRide.isVehicle() && count < MAX_ENTITY_STACK) {
      // todo: possibly throw off all passengers of the target
      if (target.startRiding(toRide, true)) {
        if (player instanceof ServerPlayer) {
          TinkerNetwork.getInstance().sendVanillaPacket(player, new ClientboundSetPassengersPacket(player));
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
  public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.CHEST) == stack && entityIn.isVehicle()) {
      int amplifier = this.getEntitiesCarriedCount(livingEntity) - 1;
      livingEntity.addEffect(new MobEffectInstance(TinkerGadgets.carryEffect.get(), 2, amplifier, true, false));
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
    return ImmutableMultimap.of(); // no attributes, the potion effect handles them
  }

  public static class CarryPotionEffect extends TinkerEffect {
    static final String UUID = "ff4de63a-2b24-11e6-b67b-9e71128cae77";

    public CarryPotionEffect() {
      super(MobEffectCategory.NEUTRAL, true);

      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID, -0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
      return true; // check every tick
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity livingEntityIn, int p_76394_2_) {
      ItemStack chestArmor = livingEntityIn.getItemBySlot(EquipmentSlot.CHEST);
      if (chestArmor.isEmpty() || chestArmor.getItem() != TinkerGadgets.piggyBackpack.get()) {
        TinkerGadgets.piggyBackpack.get().matchCarriedEntitiesToCount(livingEntityIn, 0);
      } else {
        TinkerGadgets.piggyBackpack.get().matchCarriedEntitiesToCount(livingEntityIn, chestArmor.getCount());
        if (!livingEntityIn.getCommandSenderWorld().isClientSide) {
          livingEntityIn.getCapability(PiggybackCapability.PIGGYBACK, null).ifPresent(PiggybackHandler::updatePassengers);
        }
      }
    }

    // TODO: proper sprite sheet for effect icons?
    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
      consumer.accept(new IClientMobEffectExtensions() {
        private void renderIcon(MobEffectInstance effect, GuiGraphics graphics, int x, int y) {
          ElementScreen element = switch (effect.getAmplifier()) {
            case 0 -> Icons.PIGGYBACK_1;
            case 1 -> Icons.PIGGYBACK_2;
            default -> Icons.PIGGYBACK_3;
          };

          element.draw(graphics, x + 6, y + 7);
        }

        @Override
        public boolean renderInventoryIcon(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, GuiGraphics graphics, int x, int y, int z) {
          renderIcon(effect, graphics, x, y);
          return true;
        }

        @Override
        public boolean renderGuiIcon(MobEffectInstance effect, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
          renderIcon(effect, graphics, x, y);
          return true;
        }
      });
    }
  }
}
