package tconstruct.weaponry.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import tconstruct.items.tools.Broadsword;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.Reference;

import java.util.List;

public class Boneana extends Broadsword {
    private IIcon brokenIcon;

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Reference.resource("broadsword/boneana"));
        brokenIcon = iconRegister.registerIcon(Reference.resource("broadsword/boneana_split"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("InfiTool"))
            return emptyIcon;

        if(stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken"))
            return brokenIcon;
        return itemIcon;
    }

    @Override
    public String getLocalizedToolName() {
        return "Bonæna";
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        // you are not welcome here >:C
    }
}
