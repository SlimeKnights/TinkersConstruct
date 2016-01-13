package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ListIterator;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModSoulbound extends Modifier {

  public ModSoulbound() {
    super("soulbound");

    addAspects(new ModifierAspect.DataAspect(this, 0xf5fbac), new ModifierAspect.SingleAspect(this));
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // nothing to do :(
  }

  // We copy the soulbound items into the players corpse in here
  // HIGH priority so we do it before other possibly death-inventory-modifying mods
  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerDeath(PlayerDropsEvent event) {
    if (event.entityPlayer == null || event.entityPlayer instanceof FakePlayer || event.isCanceled()) {
      return;
    }
    if(event.entityPlayer.worldObj.getGameRules().getBoolean("keepInventory")) {
      return;
    }

    ListIterator<EntityItem> iter = event.drops.listIterator();
    while (iter.hasNext()) {
      EntityItem ei = iter.next();
      ItemStack stack = ei.getEntityItem();
      // find soulbound items
      if(TinkerUtil.hasModifier(stack.getTagCompound(), this.identifier)) {
        // copy the items back into the dead players inventory
        event.entityPlayer.inventory.addItemStackToInventory(stack);
        iter.remove();
      }
    }
  }

  // On respawn we copy the items out of the players corpse, into the new player
  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerClone(PlayerEvent.Clone evt) {
    if (!evt.wasDeath || evt.isCanceled()) {
      return;
    }
    if(evt.original == null || evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer) {
      return;
    }
    if(evt.entityPlayer.worldObj.getGameRules().getBoolean("keepInventory")) {
      return;
    }

    for (int i = 0; i < evt.original.inventory.mainInventory.length; i++) {
      ItemStack stack = evt.original.inventory.mainInventory[i];
      if(stack != null && TinkerUtil.hasModifier(stack.getTagCompound(), this.identifier)) {
        evt.entityPlayer.inventory.addItemStackToInventory(stack);
        evt.original.inventory.mainInventory[i] = null;
      }
    }
  }
}
