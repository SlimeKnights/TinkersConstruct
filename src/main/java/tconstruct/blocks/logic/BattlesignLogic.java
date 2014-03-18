package tconstruct.blocks.logic;

import tconstruct.inventory.FrypanContainer;
import tconstruct.library.util.IActiveLogic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

/* Slots
 * 0: Battlesign item
 */

public class BattlesignLogic extends EquipLogic
{

    public BattlesignLogic()
    {
        super(1);
    }

    @Override
    public String getDefaultName ()
    {
        return "decoration.battlesign";
    }

    /* NBT */
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
    }

    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null; //Not a normal gui block
    }
}
