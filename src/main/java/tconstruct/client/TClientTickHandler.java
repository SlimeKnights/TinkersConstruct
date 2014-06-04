package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.items.armor.TravelGear;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TClientTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();
    GameSettings gs = mc.gameSettings;
    TControls controlInstance = ((TProxyClient) TConstruct.proxy).controlInstance;
    ItemStack prevFeet;
    double prevMotionY;
    boolean morphed;
    float prevMouseSensitivity;
    boolean sprint;

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        TContent.oreBerry.setGraphicsLevel(Block.leaves.graphicsLevel);
        TContent.oreBerrySecond.setGraphicsLevel(Block.leaves.graphicsLevel);
        TContent.slimeLeaves.setGraphicsLevel(Block.leaves.graphicsLevel);
        EntityPlayer player = getPlayer();

        if (player != null)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (mc.thePlayer.onGround)
            {
                controlInstance.landOnGround();
            }
            if (stats.climbWalls && player.isCollidedHorizontally && !player.isSneaking())
            {
                player.motionY = 0.1176D;
                player.fallDistance = 0.0f;
            }

            //Feet changes
            ItemStack feet = player.getCurrentArmor(0);
            if (feet != null)
            {
                if (feet.getItem() instanceof TravelGear && player.stepHeight < 1.0f)
                {
                    player.stepHeight += 0.6f;
                }
                if (feet.getItem() instanceof IModifyable && !player.isSneaking())
                {
                    NBTTagCompound tag = feet.getTagCompound().getCompoundTag(((IModifyable) feet.getItem()).getBaseTagName());
                    int sole = tag.getInteger("Slimy Soles");
                    if (sole > 0)
                    {
                        if (!player.isSneaking() && player.onGround && prevMotionY < -0.4)
                            player.motionY = -prevMotionY * (Math.min(0.99, sole * 0.2));
                    }
                }
                prevMotionY = player.motionY;
            }
            if (feet != prevFeet)
            {
                if (prevFeet != null && prevFeet.getItem() instanceof TravelGear)
                    player.stepHeight -= 0.6f;
                if (feet != null && feet.getItem() instanceof TravelGear)
                    player.stepHeight += 0.6f;
                prevFeet = feet;
            }

            //Legs or wing changes
            /*ItemStack legs = player.getCurrentArmor(1);
            if (legs != null && legs.getItem() instanceof IModifyable)
            {
                NBTTagCompound tag = legs.getTagCompound().getCompoundTag(((IModifyable) legs.getItem()).getBaseTagName());
                if (player.isSprinting())
                {
                    if (!sprint)
                    {
                        sprint = true;
                        int sprintboost = tag.getInteger("Sprint Assist");
                        if (player.isSprinting() && sprintboost > 0)
                        {
                            prevMouseSensitivity = gs.mouseSensitivity;
                            gs.mouseSensitivity *= 1 - (0.15 * sprintboost);
                        }
                    }
                }
                else if (sprint)
                {
                    sprint = false;
                    gs.mouseSensitivity = prevMouseSensitivity;
                }
            }*/
            if (!player.isPlayerSleeping() && !morphed)
            {
                ItemStack chest = player.getCurrentArmor(2);
                if (chest == null || !(chest.getItem() instanceof IModifyable))
                {
                    player.setSize(0.6F, 1.8F);
                }
                else
                {
                    NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                    int dodge = tag.getInteger("Perfect Dodge");
                    if (dodge > 0)
                    {
                        player.setSize(Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                    }
                }
            }
        }
    }

    EntityPlayer getPlayer ()
    {
        return mc.thePlayer;
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public String getLabel ()
    {
        return null;
    }
}
