package common.darkknight.jewelrycraft.util;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class JewelryNBT
{

    public static void addMetal(ItemStack item, ItemStack metal)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound ingotNBT = new NBTTagCompound();
        metal.writeToNBT(ingotNBT);
        itemStackData.setTag("ingot", ingotNBT);
    }

    public static void addJewel(ItemStack item, ItemStack jewel)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound jewelNBT = new NBTTagCompound();
        jewel.writeToNBT(jewelNBT);
        itemStackData.setTag("jewel", jewelNBT);
    }

    public static void addModifier(ItemStack item, ItemStack modifier)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound modifierNBT = new NBTTagCompound();
        modifier.writeToNBT(modifierNBT);
        itemStackData.setTag("modifier", modifierNBT);
    }

    public static void addEntity(ItemStack item, EntityLivingBase entity)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound entityNBT = new NBTTagCompound();
        entity.writeToNBT(entityNBT);
        itemStackData.setTag("entity", entityNBT);
    }

    public static void addEntityID(ItemStack item, EntityLivingBase entity)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound entityNBT = new NBTTagCompound();
        int id = EntityList.getEntityID(entity); 
        entityNBT.setInteger("entityID", id);
        itemStackData.setTag("entityID", entityNBT);
    }

    public static void addCoordonates(ItemStack item, double x, double y, double z)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound coords = new NBTTagCompound();
        coords.setDouble("x", x);
        coords.setDouble("y", y);
        coords.setDouble("z", z);
        itemStackData.setTag("x", coords);
        itemStackData.setTag("y", coords);
        itemStackData.setTag("z", coords);
    }

    public static void addBlockCoordonates(ItemStack item, int x, int y, int z)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound coords = new NBTTagCompound();
        coords.setInteger("blockX", x);
        coords.setInteger("blockY", y);
        coords.setInteger("blockZ", z);
        itemStackData.setTag("blockX", coords);
        itemStackData.setTag("blockY", coords);
        itemStackData.setTag("blockZ", coords);
    }

    public static void addCoordonatesAndDimension(ItemStack item, double x, double y, double z, int dim, String name)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound coords = new NBTTagCompound();
        coords.setDouble("x", x);
        coords.setDouble("y", y);
        coords.setDouble("z", z);
        coords.setInteger("dimension", dim);
        coords.setString("dimName", name);
        itemStackData.setTag("x", coords);
        itemStackData.setTag("y", coords);
        itemStackData.setTag("z", coords);
        itemStackData.setTag("dimension", coords);
        itemStackData.setTag("dimName", coords);
    }

    public static void addMode(ItemStack item, String modeN)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        NBTTagCompound mode = new NBTTagCompound();
        mode.setString("mode", modeN);
        itemStackData.setTag("mode", mode);
    }
    
    public static void removeNBT(ItemStack item, String tag)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        itemStackData.removeTag(tag);
    }
    
    public static boolean hasTag(ItemStack item, String tag)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        if(itemStackData.hasKey(tag)) return true;
        return false;
    }
    
    public static void removeEntity(ItemStack item)
    {
        JewelryNBT.removeNBT(item, "entityID");
        JewelryNBT.removeNBT(item, "entity");
        JewelryNBT.removeNBT(item, "ench");
    }

    public static void addFakeEnchantment(ItemStack item)
    {
        NBTTagCompound itemStackData;
        if (item.hasTagCompound())
            itemStackData = item.getTagCompound();
        else
        {
            itemStackData = new NBTTagCompound();
            item.setTagCompound(itemStackData);
        }
        itemStackData.setTag("ench", new NBTTagList("ench"));
    }

    public static ItemStack jewel(ItemStack stack)
    {
        if(stack != null && stack != new ItemStack(0, 0, 0) && stack.hasTagCompound() && stack.getTagCompound().hasKey("jewel"))
        {
            NBTTagCompound jewelNBT = (NBTTagCompound) stack.getTagCompound().getTag("jewel");
            ItemStack jewel = new ItemStack(0, 0, 0);
            jewel.readFromNBT(jewelNBT);
            return jewel;
        }
        return null;
    }

    public static boolean isJewelX(ItemStack stack, ItemStack jewel)
    {
        if(jewel(stack) != null && jewel(stack).itemID == jewel.itemID && jewel(stack).getItemDamage() == jewel.getItemDamage()) return true;
        return false;
    }

    public static ItemStack modifier(ItemStack stack)
    {
        if(stack != null && stack != new ItemStack(0, 0, 0) && stack.hasTagCompound() && stack.getTagCompound().hasKey("modifier"))
        {
            NBTTagCompound modifierNBT = (NBTTagCompound) stack.getTagCompound().getTag("modifier");
            ItemStack modifier = new ItemStack(0, 0, 0);
            modifier.readFromNBT(modifierNBT);
            return modifier;
        }
        return null;
    }

    public static boolean isModifierX(ItemStack stack, ItemStack modifier)
    {
        if(modifier(stack) != null && modifier(stack).itemID == modifier.itemID && modifier(stack).getItemDamage() == modifier.getItemDamage()) return true;
        return false;
    }

    public static boolean isModifierEffectType(ItemStack stack)
    {
        if(modifier(stack) != null && (isModifierX(stack, new ItemStack(Item.blazePowder)) || isModifierX(stack, new ItemStack(Item.sugar))
                || isModifierX(stack, new ItemStack(Item.pickaxeIron)) || isModifierX(stack, new ItemStack(Item.feather))
                || isModifierX(stack, new ItemStack(Item.potion, 1, 8270)))) return true;
        return false;
    }

    public static ItemStack ingot(ItemStack stack)
    {
        if(stack != null && stack != new ItemStack(0, 0, 0) && stack.hasTagCompound() && stack.getTagCompound().hasKey("ingot"))
        {
            NBTTagCompound ingotNBT = (NBTTagCompound) stack.getTagCompound().getTag("ingot");
            ItemStack ingot = new ItemStack(0, 0, 0);
            ingot.readFromNBT(ingotNBT);
            return ingot;
        }
        return null;
    }

    public static boolean isIngotX(ItemStack stack, ItemStack ingot)
    {
        if(ingot(stack) != null && ingot(stack).itemID == ingot.itemID && ingot(stack).getItemDamage() == ingot.getItemDamage()) return true;
        return false;
    }

    public static boolean isModeX(ItemStack stack, String modeN)
    {
        if(modeName(stack) != null && modeName(stack).equals(modeN)) return true;
        return false;
    }

    public static EntityLivingBase entity(ItemStack stack, EntityPlayer player)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("entityID") && stack.getTagCompound().hasKey("entity"))
        {
            NBTTagCompound enID = (NBTTagCompound) stack.getTagCompound().getTag("entityID");
            NBTTagCompound en = (NBTTagCompound) stack.getTagCompound().getTag("entity");
            int entityID = 0;
            entityID = enID.getInteger("entityID");
            EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByID(entityID, player.worldObj);
            entity.readFromNBT(en);
            return entity;
        }
        return null;
    }

    public static boolean isEntityX(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
    {
        if(entity(stack, player) != null && entity(stack, player).equals(entity)) return true;
        return false;
    }

    public static String dimName(ItemStack stack)
    {
        if(stack != null && stack != new ItemStack(0, 0, 0) && stack.hasTagCompound() && stack.getTagCompound().hasKey("dimName"))
        {
            NBTTagCompound dim = (NBTTagCompound) stack.getTagCompound().getTag("dimName");
            String name = dim.getString("dimName");
            return name;
        }
        return null;
    }

    public static String modeName(ItemStack stack)
    {
        if(stack != null && stack != new ItemStack(0, 0, 0) && stack.hasTagCompound() && stack.getTagCompound().hasKey("mode"))
        {
            NBTTagCompound dim = (NBTTagCompound) stack.getTagCompound().getTag("mode");
            String name = dim.getString("mode");
            return name;
        }
        return null;
    }

    public static boolean isDimNameX(ItemStack stack, String dimName)
    {
        if(ingot(stack) != null && dimName(stack).equals(dimName)) return true;
        return false;
    }

    public static int dimension(ItemStack stack)
    {
        if(stack != null && stack != new ItemStack(0, 0, 0) && stack.hasTagCompound() && stack.getTagCompound().hasKey("dimension"))
        {
            NBTTagCompound dim = (NBTTagCompound) stack.getTagCompound().getTag("dimension");
            int dimension = dim.getInteger("dimension");
            return dimension;
        }
        return -2;
    }

    public static boolean isDimensionX(ItemStack stack, int dimension)
    {
        if(dimension(stack) != -2 && dimension(stack) == dimension) return true;
        return false;
    }

    public static int blockCoordX(ItemStack stack)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("blockX"))
        {
            NBTTagCompound x = (NBTTagCompound) stack.getTagCompound().getTag("blockX");
            int posX = x.getInteger("blockX");
            return posX;
        }
        return -1;
    }

    public static int blockCoordY(ItemStack stack)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("blockY"))
        {
            NBTTagCompound y = (NBTTagCompound) stack.getTagCompound().getTag("blockY");
            int posY = y.getInteger("blockY");
            return posY;
        }
        return -1;
    }

    public static int blockCoordZ(ItemStack stack)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("blockZ"))
        {
            NBTTagCompound z = (NBTTagCompound) stack.getTagCompound().getTag("blockZ");
            int posZ = z.getInteger("blockZ");
            return posZ;
        }
        return -1;
    }

    public static double playerPosX(ItemStack stack)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("x"))
        {
            NBTTagCompound x = (NBTTagCompound) stack.getTagCompound().getTag("x");
            double posX = x.getDouble("x");
            return posX;
        }
        return -1;
    }

    public static double playerPosY(ItemStack stack)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("y"))
        {
            NBTTagCompound y = (NBTTagCompound) stack.getTagCompound().getTag("y");
            double posY = y.getDouble("y");
            return posY;
        }
        return -1;
    }

    public static double playerPosZ(ItemStack stack)
    {
        if (stack != null && stack != new ItemStack(0, 0, 0) && stack.getTagCompound().hasKey("z"))
        {
            NBTTagCompound z = (NBTTagCompound) stack.getTagCompound().getTag("z");
            double posZ = z.getDouble("z");
            return posZ;
        }
        return -1;
    }
}
