package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import tconstruct.library.*;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;

public class Excavator extends AOEHarvestTool
{
    public Excavator()
    {
        super(2, 1,0);
        this.setUnlocalizedName("InfiTool.Excavator");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "shovel";
    }

    static Material[] materials = { Material.grass, Material.ground, Material.sand, Material.snow, Material.craftedSnow, Material.clay };

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.excavatorHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.toughBinding;
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 2.75f;
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_excavator_head";
        case 1:
            return "_excavator_head_broken";
        case 2:
            return "_excavator_handle";
        case 3:
            return "_excavator_binding";
        case 4:
            return "_excavator_grip";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_excavator_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "excavator";
    }

    @Override
    public float breakSpeedModifier ()
    {
        return 0.4f;
    }

    @Override
    public float stoneboundModifier ()
    {
        return 216f;
    }

}
