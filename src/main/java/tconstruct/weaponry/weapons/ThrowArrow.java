package tconstruct.weaponry.weapons;

import tconstruct.weaponry.entity.ArrowEntity;
import tconstruct.weaponry.library.weaponry.AmmoWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.tools.TinkerTools;

public class ThrowArrow extends AmmoWeapon {
    public ThrowArrow() {
        super(1, "ThrowArrow");

        headIcons = TinkerTools.arrow.headIcons;
        handleIcons = TinkerTools.arrow.handleIcons;
        accessoryIcons = TinkerTools.arrow.accessoryIcons;
        extraIcons = TinkerTools.arrow.extraIcons;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
            case 0:
                return "_arrow_head";
            case 1:
                return ""; // Doesn't break
            case 2:
                return "_arrow_shaft";
            case 3:
            case 4:
                return "_arrow_fletching";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_arrow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "arrow";
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.arrowhead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.fletching;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.fletching;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack reference = stack.copy();
        reference.stackSize = 1;
        Entity projectile = new ArrowEntity(world, player, 1.9f, 0f, reference);

        if(!world.isRemote)
            world.spawnEntityInWorld(projectile);

        return stack;
    }

    @Override
    protected Entity createProjectile(ItemStack reference, World world, EntityPlayer player, float accuracy) {
        return new ArrowEntity(world, player, 1.0f, accuracy, reference);
    }
}
