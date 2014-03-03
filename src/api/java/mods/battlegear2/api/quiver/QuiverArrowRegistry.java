package mods.battlegear2.api.quiver;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class QuiverArrowRegistry {

    private static Map<ItemStack,  Class<? extends EntityArrow>> map = new TreeMap<ItemStack,  Class<? extends EntityArrow>>(new StackComparator());

    public static void addArrowToRegistry(int itemId, int itemMetadata, Class<? extends EntityArrow> entityArrow){
        ItemStack stack = new ItemStack(itemId, 1, itemMetadata);
        addArrowToRegistry(stack, entityArrow);
    }
    
    public static void addArrowToRegistry(ItemStack stack, Class<? extends EntityArrow> entityArrow){
        ItemStack st = stack.copy();
        st.stackSize = 1;
        map.put(st, entityArrow);
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
                }else{
                	idDiff = stack.getItemDamage() - stack2.getItemDamage();
                	if(idDiff != 0){
                        return idDiff;
                    }else{
                    	int tag = 0;
                    	if(stack.hasTagCompound()){
                    		tag = stack.getTagCompound().hashCode();
                    	}
                    	int tag2 = 0;
                    	if(stack2.hasTagCompound()){
                    		tag2 = stack2.getTagCompound().hashCode();
                    	}
                    	return tag-tag2;
                    }
                }
            }

        }
    }


}
