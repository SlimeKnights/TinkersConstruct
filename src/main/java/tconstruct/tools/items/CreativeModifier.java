package tconstruct.tools.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.*;
import tconstruct.library.tools.ToolCore;

public class CreativeModifier extends Item
{

    public CreativeModifier()
    {
        super();
        this.setCreativeTab(TConstructRegistry.materialTab);
    }

    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems (Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));

        for (ToolRecipe recipe : ToolBuilder.instance.combos)
        {
            ToolCore tool = recipe.getType();
            ItemStack item = new ItemStack(par1, 1, 0);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("TargetLock", tool.getToolName());

            item.setTagCompound(compound);
            par3List.add(item);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:skull_char_gold");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.hasTagCompound())
        {
            String targetLock = "None";
            targetLock = stack.getTagCompound().getString("TargetLock");
            list.add(StatCollector.translateToLocal("creativeModLock.tooltip") + targetLock);
        }
    }
}