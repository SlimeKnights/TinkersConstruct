package tconstruct.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Leather extends Item
{

    public Leather(int par1)
    {
        super(par1);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:materials/leather_chocolate");
    }
}
