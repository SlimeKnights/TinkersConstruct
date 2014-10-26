package tconstruct.weaponry.weapons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.client.CrosshairType;
import tconstruct.weaponry.entity.JavelinEntity;
import tconstruct.library.weaponry.AmmoWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.tools.TinkerTools;

public class Javelin extends AmmoWeapon {
    public Javelin() {
        super(3, "Javelin");
    }

    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        // javelin is the only throwing/ammo weapon that hurts on leftclicking
        return AbilityHelper.onLeftClickEntity(stack, player, entity, this, 0);
    }

    @Override
    public float getWindupProgress(ItemStack itemStack, EntityPlayer player) {
        if(!itemStack.hasTagCompound())
            return super.getWindupProgress(itemStack, player);

        if(!itemStack.getTagCompound().getCompoundTag("InfiTool").hasKey("Throwing"))
            return 0.5f;

        float timeleft = itemStack.getTagCompound().getCompoundTag("InfiTool").getInteger("Throwing");
        float threshold = getWindupTime(itemStack)/5;
        if(timeleft < threshold)
            return (threshold-timeleft)/threshold;
        else
            return 0.5f - 0.25f * ((float)getWindupTime(itemStack)-timeleft)/threshold;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

        // already throwing
        if(tags.hasKey("Throwing"))
            return stack;

        // has ammo?
        if(this.getAmmoCount(stack) <= 0)
            return stack;

        // start throwing
        tags.setInteger("Throwing", getWindupTime(stack));

        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(stack, world, entity, par4, par5);

        if(!stack.hasTagCompound())
            return;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if(!tags.hasKey("Throwing"))
            return;

        EntityPlayer player = (EntityPlayer) entity;
        if(player.inventory.getCurrentItem() != stack)
            return;

        int timeLeft = tags.getInteger("Throwing");
        timeLeft--;
        if(timeLeft > 0)
            tags.setInteger("Throwing", timeLeft);
        else {
            onPlayerStoppedUsing(stack, world, player, 0);
            tags.removeTag("Throwing");
        }
    }

    @Override
    public boolean zoomOnWindup(ItemStack itemStack) {
        return true;
    }

    @Override
    public float getZoom(ItemStack itemStack) {
        return 1.5f;
    }

    @Override
    public float minAccuracy(ItemStack itemStack) {
        return 2.5f;
    }

    @Override
    public float maxAccuracy(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public int getWindupTime(ItemStack itemStack) {
        return 20;
    }

    @Override
    public int getPartAmount() {
        return 3;
    }

    @Override
    public String getIconSuffix(int partType) {
        switch (partType)
        {
            case 0:
                return "_javelin_head";
            case 1:
                return ""; // no broken, since it runs out of ammo
            case 2:
                return "_javelin_handle";
            case 3:
                return "_javelin_accessory";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix() {
        return "_javelin_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "javelin";
    }

    @Override
    public float getAmmoModifier() {
        return 0.02f;
    }

    @Override
    public Item getHeadItem() {
        return TinkerWeaponry.arrowhead;
    }

    @Override
    public Item getHandleItem() {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.toughRod;
    }

    @Override
    protected Entity createProjectile(ItemStack reference, World world, EntityPlayer player, float accuracy) {
        reference.getTagCompound().getCompoundTag("InfiTool").removeTag("Throwing"); // needed so the NBTs are equal
        JavelinEntity entity = new JavelinEntity(world, player, 2.0f, accuracy, reference);

        return entity;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrosshairType getCrosshairType() { return CrosshairType.WEIRD; }
}
