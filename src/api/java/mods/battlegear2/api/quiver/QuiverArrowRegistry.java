package mods.battlegear2.api.quiver;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class QuiverArrowRegistry {

    private static Map<ItemStack,  Class<? extends EntityArrow>> map = new TreeMap<ItemStack,  Class<? extends EntityArrow>>(new StackComparator());

    public static void addArrowToRegistry(Item i, int itemMetadata, Class<? extends EntityArrow> entityArrow){
        ItemStack stack = new ItemStack(i, 1, itemMetadata);
        map.put(stack, entityArrow);
    }

    public static Class<? extends EntityArrow> getArrowClass(ItemStack stack){
        return map.get(stack);
    }

    static class StackComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack stack, ItemStack stack2) {

            if(stack == stack2){
                return 0;
            }else{

                int idDiff = stack.itemID - stack2.itemID;
                if(idDiff != 0){
                    return idDiff;
                }else
                    return stack.getItemDamage() - stack2.getItemDamage();
            }

        }
    }


}
