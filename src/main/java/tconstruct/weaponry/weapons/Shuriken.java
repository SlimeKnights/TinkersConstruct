package tconstruct.weaponry.weapons;

import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.client.CrosshairType;
import tconstruct.client.IconFlipped;
import tconstruct.weaponry.entity.ShurikenEntity;
import tconstruct.library.weaponry.AmmoWeapon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Map;

public class Shuriken extends AmmoWeapon {

    public Shuriken() {
        super(1, "shuriken");
    }

    @Override
    public float getDamageModifier() {
        return 0.4f;
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    public String getIconSuffix(int partType) {
        switch (partType)
        {
            case 0:
                return "_shuriken";
            case 1:
                return ""; // no broken, since it runs out of ammo
            case 2:
                return "_shuriken";
            case 3:
                return "_shuriken";
            case 4:
                return "_shuriken";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix() {
        return "_shuriken_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "shuriken";
    }

    @Override
    public Item getHeadItem() {
        return TinkerWeaponry.partShuriken;
    }

    @Override
    public Item getHandleItem() {
        return TinkerWeaponry.partShuriken;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerWeaponry.partShuriken;
    }

    @Override
    public Item getExtraItem() {
        return TinkerWeaponry.partShuriken;
    }

    @Override
    public float minAccuracy(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float maxAccuracy(ItemStack itemStack) {
        return 0;
    }

    @Override
    public float getWindupProgress(ItemStack itemStack, EntityPlayer player) {
        return 1.0f; // always fully winded!
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        launchProjectile(stack, world, player);

        // this is only used for the animation
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));

        return stack;
    }

    @Override
    protected Entity createProjectile(ItemStack reference, World world, EntityPlayer player, float accuracy) {
        return new ShurikenEntity(world, player, 1.9f, 0f, reference);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int durationLeft) {
        // do nothing, or we'd throw double-shurikens!
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 2;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.block;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        // register icons as usually
        super.registerIcons(iconRegister);

        // now we flip all the different part icons, so we only need 1 graphic for 4 different orientations \o/
        // handle first: flip x
        Iterator<Map.Entry<Integer, IIcon>> iter = handleIcons.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry<Integer, IIcon> entry = iter.next();
            entry.setValue(new IconFlipped(entry.getValue(), true, false));
            // the entry object should reference the direct object in the map, no further updating needed
        }

        // accessory: flip y
        iter = accessoryIcons.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry<Integer, IIcon> entry = iter.next();
            entry.setValue(new IconFlipped(entry.getValue(), false, true));
        }

        // extra: flip x and y
        iter = extraIcons.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry<Integer, IIcon> entry = iter.next();
            entry.setValue(new IconFlipped(entry.getValue(), true, true));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CrosshairType getCrosshairType() {
        return CrosshairType.TIP;
    }
}
