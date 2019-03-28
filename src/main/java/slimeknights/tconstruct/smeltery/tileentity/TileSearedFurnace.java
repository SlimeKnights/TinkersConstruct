package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.client.GuiSearedFurnace;
import slimeknights.tconstruct.smeltery.inventory.ContainerSearedFurnace;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSearedFurnace;

public class TileSearedFurnace extends TileHeatingStructureFuelTank<MultiblockSearedFurnace> implements ITickable, IInventoryGui {

  public static final Logger log = Util.getLogger("Furnace");

  protected int tick;

  public TileSearedFurnace() {
    super("gui.searedfurnace.name", 0, 16);
    setMultiblock(new MultiblockSearedFurnace(this));
  }

  @Override
  public void update() {
    if(isClientWorld()) {
      return;
    }

    // are we fully formed?
    if(!isActive()) {
      // check for furnace once per second
      if(tick == 0) {
        checkMultiblockStructure();
      }
    }
    else {
      // we have a lot less to do than a smeltery since the inside is unreachable and we have no liquids
      // basically just heating and fuel consumption

      // we heat items every tick, as otherwise we are quite slow compared to a vanilla furnace
      if(tick % 4 == 0) {
        heatItems();
      }

      if(tick == 0) {
        interactWithEntitiesInside();
      }

      if(needsFuel) {
        consumeFuel();
      }

      // we don't check the inside for obstructions since it should not be possible unless the outside was modified
    }

    tick = (tick + 1) % 20;
  }

  /* Grabs the heat for a furnace */
  @Override
  public void updateHeatRequired(int index) {
    ItemStack stack = getStackInSlot(index);
    if(!stack.isEmpty()) {
      ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
      if(!result.isEmpty()) {
        int newSize = stack.getCount() * result.getCount();
        if(newSize <= stack.getMaxStackSize() && newSize <= getInventoryStackLimit()) {
          // we like our steaks medium rare :)
          setHeatRequiredForSlot(index, getHeatForStack(stack, result));
        }
        else {
          // if its too big, set the error state
          setHeatRequiredForSlot(index, 0);
          itemTemperatures[index] = -1;
        }

        // instantly consume fuel if required
        if(!hasFuel()) {
          consumeFuel();
        }

        return;
      }
    }

    setHeatRequiredForSlot(index, 0);
  }

  /**
   * Returns the heat required to smelt the itemstack
   *
   * @param input  Input stack
   * @param result Output stack, here just for conveince so we don't have to index it twice
   * @return A number representing the time
   */
  private int getHeatForStack(@Nonnull ItemStack input, @Nonnull ItemStack result) {
    // base stack temp, here in case Forge adds a hook
    int base = 200;
    float temp = base * input.getCount() / 4f;

    // adjust the speed based on if its a food or not
    // after adjustment with the base of 200, we get a temp of 160 for foods, though its slightly faster than that due to TileHeatingStructure logic
    if(result.getItem() instanceof ItemFood) {
      temp *= 0.8;
    }

    return (int) temp;
  }

  // melt stuff
  @Override
  protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
    ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);

    if(result.isEmpty()) {
      // recipe changed mid smelting...
      return false;
    }

    // remember, we can smelt a whole stack at once
    result = result.copy();
    result.setCount(result.getCount() * stack.getCount());

    setInventorySlotContents(slot, result);
    // we set to 1 so we get positive infinity instead of NaN for progress, since NaN is already defined as no recipe
    itemTemperatures[slot] = 1;
    itemTempRequired[slot] = 0;

    // we return false since the itemstack does not leave the slot, otherwise any data we set here is lost (which really would just be the temp)
    return false;
  }

  @Override
  protected int getUpdatedInventorySize(int width, int height, int depth) {
    return 9 + (3 * width * height * depth);
  }

  protected void interactWithEntitiesInside() {
    // find all monsters within the furnace and kill them 
    AxisAlignedBB bb = info.getBoundingBox().contract(1, 1, 1).offset(0, 0.5, 0).expand(0, 0.5, 0);

    List<EntityLivingBase> entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, bb);

    for(EntityLivingBase entity : entities) {
      if(entity instanceof EntityMob && entity.isEntityAlive()) {
        entity.setDead();
      }
    }
  }

  /* GUI */
  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerSearedFurnace(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiSearedFurnace((ContainerSearedFurnace) createContainer(inventoryplayer, world, pos), this);
  }
}
