package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.world.World;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;

public class Hatchet extends HarvestTool
{
    public Hatchet()
    {
        super(3);
        this.setUnlocalizedName("InfiTool.Axe");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "axe";
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player)
    {
        if (block != null && block.getMaterial() == Material.leaves)
            return false;

        return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
    }

    static Material[] materials = { Material.wood, Material.leaves, Material.vine, Material.circuits, Material.cactus, Material.gourd };

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.hatchetHead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 8;
    }

    @Override
    public int getPartAmount ()
    {
        return 2;
    }

    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_axe_head";
        case 1:
            return "_axe_head_broken";
        case 2:
            return "_axe_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_axe_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "axe";
    }
}
