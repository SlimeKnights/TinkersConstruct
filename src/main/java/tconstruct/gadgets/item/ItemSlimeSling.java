package tconstruct.gadgets.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.gadgets.TinkerGadgets;
import tconstruct.library.SlimeBounceHandler;
import tconstruct.library.TConstructRegistry;
import tconstruct.util.network.MovementUpdatePacket;

public class ItemSlimeSling extends Item {

    public ItemSlimeSling() {
        this.setMaxStackSize(1);
        this.setCreativeTab(TConstructRegistry.gadgetsTab);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed.
     * Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("tinker:gadgets/slimesling");
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    // sling logic
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        // has to be on ground to do something
        if (!player.onGround) {
            return;
        }

        // copy chargeup code from bow \o/
        int i = this.getMaxItemUseDuration(stack) - timeLeft;
        float f = i / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        f *= 4f;

        if (f > 6f) {
            f = 6f;
        }

        // check if player was targeting a block
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, false);

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            // we fling the inverted player look vector
            Vec3 vec = player.getLookVec().normalize();

            player.addVelocity(vec.xCoord * -f, vec.yCoord * -f / 3f, vec.zCoord * -f);

            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                TConstruct.packetPipeline.sendTo(new MovementUpdatePacket(player), playerMP);
                playerMP.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
            }
            player.playSound(TinkerGadgets.resource("slimesling"), 1f, 1f);
            SlimeBounceHandler.addBounceHandler(player);
            // TinkerCommons.potionSlimeBounce.apply(player);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add("Aim at a block behind you to get thrown into the air.");
        list.add(player.onGround ? "Shoot for the moon!" : "Must be on the ground to use.");
    }
}
