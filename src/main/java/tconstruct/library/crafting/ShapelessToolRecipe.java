package tconstruct.library.crafting;

import java.util.*;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.tools.ToolCore;

public class ShapelessToolRecipe extends ShapelessRecipes
{

    public ShapelessToolRecipe(ItemStack par1ItemStack, List par2List)
    {
        super(par1ItemStack, par2List);
    }

    @Override
    public boolean matches (InventoryCrafting par1InventoryCrafting, World par2World)
    {
        ArrayList arraylist = new ArrayList(this.recipeItems);

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                ItemStack itemstack = par1InventoryCrafting.getStackInRowAndColumn(j, i);

                if (itemstack != null)
                {
                    boolean flag = false;
                    Iterator iterator = arraylist.iterator();

                    while (iterator.hasNext())
                    {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        // TConstruct.logger.info("Rawr! "+itemstack1.getItemDamage());
                        if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getItemDamage() == Short.MAX_VALUE || itemstack.getItemDamage() == itemstack1.getItemDamage()))
                        {
                            if (itemstack.getItem() instanceof ToolCore)
                            {
                                NBTTagCompound tags = itemstack.getTagCompound().getCompoundTag("InfiTool");
                                if (tags.getBoolean("Broken"))
                                    return false;
                            }
                            flag = true;
                            arraylist.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }
        }

        return arraylist.isEmpty();
    }

}
