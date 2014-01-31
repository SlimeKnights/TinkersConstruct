package tconstruct.items.tools;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.DualHarvestTool;
import tconstruct.util.config.PHConstruct;

public class Mattock extends DualHarvestTool
{
    public Mattock()
    {
        super(3);
        this.setUnlocalizedName("InfiTool.Mattock");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return axeMaterials;
    }

    @Override
    protected Material[] getEffectiveSecondaryMaterials ()
    {
        return shovelMaterials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "axe";
    }

    @Override
    protected String getSecondHarvestType ()
    {
        return "shovel";
    }

    static Material[] axeMaterials = { Material.field_151575_d, Material.field_151570_A};//TODO find this//, Material.pumpkin, Material.field_151585_k, Material.field_151582_l };
    static Material[] shovelMaterials = { Material.field_151577_b, Material.field_151578_c, Material.field_151571_B };

    @Override
    public Item getHeadItem ()
    {
        return TRepo.hatchetHead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TRepo.shovelHead;
    }

    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_mattock_head";
        case 1:
            return "_mattock_head_broken";
        case 2:
            return "_mattock_handle";
        case 3:
            return "_mattock_back";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_mattock_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "mattock";
    }

    /* Mattock specific */

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return false;

        return AbilityHelper.hoeGround(stack, player, world, x, y, z, side, random);
    }

    public void buildTool (int id, String name, List list)
    {
        if (!PHConstruct.denyMattock || allowCrafting(id))
        {
            Item accessory = getAccessoryItem();
            ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, id) : null;
            Item extra = getExtraItem();
            ItemStack extraStack = extra != null ? new ItemStack(extra, 1, id) : null;
            ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), new ItemStack(getHandleItem(), 1, id), accessoryStack, extraStack, name + getToolName());
            if (tool == null)
            {
                if (!TRepo.supressMissingToolLogs)
                {
                    TConstruct.logger.warn("Creative builder failed tool for " + name + this.getToolName());
                    TConstruct.logger.warn("Make sure you do not have item ID conflicts");
                }
            }
            else
            {
                tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
                list.add(tool);
            }
        }
    }

    private boolean allowCrafting (int head)
    {
        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };
        for (int i = 0; i < nonMetals.length; i++)
        {
            if (head == nonMetals[i])
                return false;
        }
        return true;
    }

    @Override
    public boolean willAllowOffhandWeapon ()
    {
        return false;
    }

    @Override
    public boolean willAllowShield ()
    {
        return false;
    }

    @Override
    public boolean isOffhandHandDualWeapon ()
    {
        return false;
    }

    @Override
    public boolean sheatheOnBack ()
    {
        return true;
    }
}