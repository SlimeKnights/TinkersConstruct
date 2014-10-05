package tconstruct.library.weaponry;

import tconstruct.library.TConstructRegistry;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;

public abstract class AmmoItem extends ToolCore implements IAmmo {
    public AmmoItem(int baseDamage, String name) {
        super(baseDamage);
        this.setCreativeTab(TConstructRegistry.weaponryTab);
    }

    @Override
    public int getAmmoCount(ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        return tags.getInteger("Ammo");
    }

    @Override
    public int getMaxAmmo(ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        return getMaxAmmo(tags);
    }

    @Override
    public int getMaxAmmo(NBTTagCompound tags) {
        float dur = tags.getInteger("TotalDurability");
        return (int)Math.ceil(dur*getAmmoModifier());
    }

    @Override
    public int addAmmo(int toAdd, ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int oldCount = tags.getInteger("Ammo");
        int newCount = Math.min(oldCount + toAdd, getMaxAmmo(stack));
        tags.setInteger("Ammo", newCount);
        return toAdd - (newCount - oldCount);
    }

    @Override
    public int consumeAmmo(int toUse, ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int oldCount = tags.getInteger("Ammo");
        int newCount = Math.max(oldCount - toUse, 0);
        tags.setInteger("Ammo", newCount);
        return toUse - (oldCount - newCount);
    }

    public float getAmmoModifier() { return 0.1f; }

    public boolean pickupAmmo(ItemStack stack, ItemStack candidate, EntityPlayer player)
    {
        if(stack.getItem() == null || !(stack.getItem() instanceof IAmmo))
            return false;

        // check if our candidate fits
        if(candidate != null)
        {
            // same item
            if(testIfAmmoMatches(stack, candidate)) {
                IAmmo pickedup = ((IAmmo) stack.getItem());
                IAmmo ininventory = ((IAmmo) candidate.getItem());
                // we can be sure that it's ammo, since stack is ammo and they're equal
                ininventory.addAmmo(pickedup.getAmmoCount(stack), candidate);

                return true;
            }
        }

        // search the players inventory
        for(ItemStack invItem : player.inventory.mainInventory) {
            if (testIfAmmoMatches(stack, invItem)) {
                IAmmo pickedup = ((IAmmo) stack.getItem());
                IAmmo ininventory = ((IAmmo) invItem.getItem());
                // we can be sure that it's ammo, since stack is ammo and they're equal
                ininventory.addAmmo(pickedup.getAmmoCount(stack), invItem);

                return true;
            }
        }

        // couldn't find a matching thing.
        return false;
    }

    private boolean testIfAmmoMatches(ItemStack reference, ItemStack candidate)
    {
        if(candidate == null)
            return false;
        if(!candidate.hasTagCompound() || !candidate.getTagCompound().hasKey("InfiTool"))
            return false;

        // create a stack to test against
        ItemStack testsubject = candidate.copy();
        // all NBT has to match, but the ammo-count obviously differs. So we set our testsubject to the same
        // this ensures that it's the one to collect and the one we have matches.
        testsubject.getTagCompound().getCompoundTag("InfiTool").setInteger("Ammo", getAmmoCount(reference));

        return ItemStack.areItemStacksEqual(reference, testsubject);
    }

    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        // ammo doesn't hurt on smacking stuff with it
        return false;
    }
}
