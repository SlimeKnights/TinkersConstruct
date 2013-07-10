package mods.tinker.tconstruct.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OreBerries extends CraftingItem
{
    static String[] names = new String[] { "iron", "gold", "copper", "tin", "aluminum", "essence" };
    static String[] tex = new String[] { "oreberry_iron", "oreberry_gold", "oreberry_copper", "oreberry_tin", "oreberry_aluminum", "oreberry_essence" };

    public OreBerries(int id)
    {
        super(id, names, tex, "oreberries/");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0: 
            list.add("Sweet Irony");
            break;
        case 1:
            list.add("Pure Luster");
            break;
        case 2:
            list.add("Tastes like metal");
            break;
        case 3:
            list.add("Tin Man");
            break;
        case 4: 
            list.add("White Chocolate");
            break;
        case 5: 
            list.add("Tastes like Creeper");
            break;
        }
    }
}
