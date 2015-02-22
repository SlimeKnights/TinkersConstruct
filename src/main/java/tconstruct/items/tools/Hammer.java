package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import tconstruct.library.*;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;

public class Hammer extends AOEHarvestTool
{
    public Hammer()
    {
        super(2, 1, 0);
        this.setUnlocalizedName("InfiTool.Hammer");
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 2;
    }

    @Override
    protected String getHarvestType ()
    {
        return "pickaxe";
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    static Material[] materials = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil };

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.hammerHead;
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
        return TinkerTools.largePlate;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 4.5f;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_hammer_head";
        case 1:
            return "_hammer_handle_broken";
        case 2:
            return "_hammer_handle";
        case 3:
            return "_hammer_front";
        case 4:
            return "_hammer_back";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_hammer_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "hammer";
    }

    @Override
    public IIcon getIcon (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null) {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass == 0) // Handle
            {
                if (tags.getBoolean("Broken"))
                    return getCorrectIcon(brokenIcons, tags.getInteger("RenderHandle"));
                return getCorrectIcon(handleIcons, tags.getInteger("RenderHandle"));
            }
            else if(renderPass == 1)
                return getCorrectIcon(headIcons, tags.getInteger("RenderHead"));
        }

        return super.getIcon(stack, renderPass);
    }

    @Override
    public void getSubItems (Item id, CreativeTabs tab, List list)
    {
        super.getSubItems(id, tab, list);

        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, 10), new ItemStack(getHandleItem(), 1, 8), new ItemStack(getAccessoryItem(), 1, 11), new ItemStack(getExtraItem(), 1, 11), StatCollector.translateToLocal("tool.infiminer"));

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setBoolean("Special", true);
        tags.setInteger("Modifiers", 0);
        tags.setInteger("Attack", Integer.MAX_VALUE / 100);
        tags.setInteger("TotalDurability", Integer.MAX_VALUE / 100);
        tags.setInteger("BaseDurability", Integer.MAX_VALUE / 100);
        tags.setInteger("MiningSpeed", Integer.MAX_VALUE / 100);
        tags.setInteger("Unbreaking", 10);

        tags.setBoolean("Built", true);
        list.add(tool);
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

    @Override
    public String[] getTraits ()
    {
        return new String[] { "weapon", "harvest", "melee", "bludgeoning" };
    }

}
