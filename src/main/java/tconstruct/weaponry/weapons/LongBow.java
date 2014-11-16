package tconstruct.weaponry.weapons;

import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.library.weaponry.BowBaseAmmo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.tools.TinkerTools;

public class LongBow extends BowBaseAmmo {
    public LongBow() {
        super(0, "Longbow");
    }

    @Override
    public float minAccuracy(ItemStack itemStack) {
        return 7.5f;
    }

    @Override
    public float maxAccuracy(ItemStack itemStack) {
        return 3.33f;
    }

    @Override
    public float getZoom(ItemStack itemStack) {
        return 1.7f;
    }

    @Override
    protected Entity createProjectile(ItemStack arrows, World world, EntityPlayer player, float speed, float accuracy, float windup) {
        if(arrows.getItem() instanceof ArrowAmmo)
        {
            // modify accuraccy of the arrow depending on its accuraccy and weight
            NBTTagCompound tags = arrows.getTagCompound().getCompoundTag("InfiTool");
            float matAccuracy = tags.getFloat("Accuracy");
            float weight = tags.getFloat("Mass");

            // we need heavier arrows because we have POW. therefore we increase the weight penality on accuracy
            accuracy += ((100f-matAccuracy)/10f)/Math.max(1f, weight-1f);
            if(accuracy < 0)
                accuracy = 0;
        }

        return super.createProjectile(arrows, world, player, speed, accuracy, windup);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
            case 0:
                return "_bow_top";
            case 1:
                return "_bowstring_broken";
            case 2:
                return "_bowstring";
            case 3:
                return "_bow_bottom";
            case 4:
                return "_bow_grip";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_bow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "longbow";
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    protected boolean animateLayer(int renderPass) {
        return renderPass < 3;
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerWeaponry.partBowLimb;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerWeaponry.bowstring;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerWeaponry.partBowLimb;
    }

    @Override
    public Item getExtraItem() {
        return TinkerTools.largePlate;
    }
}
