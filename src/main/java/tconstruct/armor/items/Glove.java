package tconstruct.armor.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import tconstruct.library.TConstructRegistry;

public class Glove extends Item
{
    public Glove()
    {
        super();
        this.setCreativeTab(TConstructRegistry.materialTab);
    }

    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon("tinker:armor/dirthand");
    }
}
