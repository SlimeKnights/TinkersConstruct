package slimeknights.tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

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
    player.worldObj.playSoundAtEntity(player, Sounds.anvil_use, 1f, 0.8f + 0.2f * TConstruct.random.nextFloat());
  }
}
