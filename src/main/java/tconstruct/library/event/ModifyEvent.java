package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

@Cancelable
public class ModifyEvent extends Event {
    public final ItemModifier modifier;
    public final IModifyable item;
    public final ItemStack itemStack;

    public ModifyEvent(ItemModifier modifier, IModifyable item, ItemStack itemStack) {
        this.modifier = modifier;
        this.item = item;
        this.itemStack = itemStack;
    }
}
