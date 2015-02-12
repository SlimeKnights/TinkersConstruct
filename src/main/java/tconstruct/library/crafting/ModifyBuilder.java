package tconstruct.library.crafting;

import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.*;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;

public class ModifyBuilder
{
    public static ModifyBuilder instance = new ModifyBuilder();
    public List<ItemModifier> itemModifiers = new ArrayList<ItemModifier>();

    public ItemStack modifyItem (ItemStack input, ItemStack[] modifiers)
    {
        ItemStack copy = input.copy(); //Prevent modifying the original
        if (!(copy.getItem() instanceof IModifyable))
            return null;

        IModifyable item = (IModifyable) copy.getItem();

        List<ItemModifier> currentModifiers = new ArrayList<ItemModifier>();
        ItemModifier newMod = null;

        NBTTagCompound tags = copy.getTagCompound().getCompoundTag(item.getBaseTagName());

        for (ItemModifier mod : itemModifiers)
        {
            if (mod.matches(modifiers, copy) && mod.validType(item))
                newMod = mod;

            if (tags.hasKey(mod.key))
                currentModifiers.add(mod);
        }

        if (newMod == null)
            return null;

        for (ItemModifier mod : currentModifiers)
        {
            if (!mod.canModifyWith(newMod) || !newMod.canModifyWith(mod))
                return null;
        }

        newMod.addMatchingEffect(copy);
        newMod.modify(modifiers, copy);
        return copy;
    }

    public ItemModifier getModifierByKey (String modKey)
    {
        for (ItemModifier mod : itemModifiers)
            if (mod.key == modKey)
                return mod;
        return null;
    }

    public static void registerModifier (ItemModifier mod)
    {
        if (mod == null)
            throw new NullPointerException("Modifier cannot be null.");
        instance.itemModifiers.add(mod);
        MinecraftForge.EVENT_BUS.post(new RegisterModifierEvent(mod.key, mod));
    }

    public static class RegisterModifierEvent extends Event
    {
        public final String key;
        public final ItemModifier modifier;

        RegisterModifierEvent (String key, ItemModifier modifier)
        {
            this.key = key;
            this.modifier = modifier;
        }
    }
}
