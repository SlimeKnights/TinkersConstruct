package mods.tinker.tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TMetalBlock extends TConstructBlock
{
    public TMetalBlock(int id, Material material, float hardness, String[] tex)
    {
        super(id, material, hardness, tex);
        this.setStepSound(Block.soundMetalFootstep);
    }

    public boolean isBeaconBase(World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
    
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("Usable for Beacon bases");
    }
}
