package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;

import java.util.Set;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.tileentity.TileToolStation;

public class ContainerToolForge extends ContainerToolStation {

  public ContainerToolForge(InventoryPlayer playerInventory, TileToolStation tile) {
    super(playerInventory, tile);
  }

  @Override
  protected Set<ToolCore> getBuildableTools() {
    return TinkerRegistry.getToolForgeCrafting();
  }

  @Override
  protected void playCraftSound(EntityPlayer player) {
    // 1.9 check if sound plays
    player.worldObj.playSound(player, player.getPosition(), SoundEvents.block_anvil_use, SoundCategory.PLAYERS, 1f, 0.9f + 0.2f * TConstruct.random.nextFloat());
  }
}
