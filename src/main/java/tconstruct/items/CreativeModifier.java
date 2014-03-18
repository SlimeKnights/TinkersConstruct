package tconstruct.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.crafting.ToolRecipe;
import tconstruct.library.tools.ToolCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeModifier extends Item
{

    public CreativeModifier(int id)
    {
        super(id);
        this.setCreativeTab(TConstructRegistry.materialTab);
    }

    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
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
    public void registerIcons (IconRegister iconRegister)
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
