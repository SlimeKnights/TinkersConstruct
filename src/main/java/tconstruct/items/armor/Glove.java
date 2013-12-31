package tconstruct.items.armor;

import tconstruct.library.TConstructRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

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
