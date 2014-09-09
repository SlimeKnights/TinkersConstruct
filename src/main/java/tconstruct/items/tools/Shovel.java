package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import tconstruct.library.tools.HarvestTool;
import tconstruct.tools.TinkerTools;

public class Shovel extends HarvestTool
{
    public Shovel()
    {
        super(2);
        this.setUnlocalizedName("InfiTool.Shovel");
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
        return TinkerTools.shovelHead;
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
            return "_shovel_head";
        case 1:
            return "_shovel_head_broken";
        case 2:
            return "_shovel_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_shovel_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "shovel";
    }

    /*
     * @Override public Icon getIcon (ItemStack stack, int renderPass) {
     * NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
     * 
     * if (renderPass == 0) // Handle { return
     * (handleIcons.get(tags.getInteger("RenderHandle"))); }
     * 
     * if (renderPass == 1) // Head { if (tags.getBoolean("Broken")) return
     * (brokenHeadIcons.get(tags.getInteger("RenderHead"))); else return
     * (headIcons.get(tags.getInteger("RenderHead"))); }
     * 
     * if (renderPass == 2) { if (tags.hasKey("Effect1")) return
     * (effectIcons.get(tags.getInteger("Effect1"))); }
     * 
     * if (renderPass == 3) { if (tags.hasKey("Effect2")) return
     * (effectIcons.get(tags.getInteger("Effect2"))); }
     * 
     * if (renderPass == 4) { if (tags.hasKey("Effect3")) return
     * (effectIcons.get(tags.getInteger("Effect3"))); }
     * 
     * return TProxyClient.blankSprite; }
     */
}
