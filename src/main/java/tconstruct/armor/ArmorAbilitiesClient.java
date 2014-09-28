package tconstruct.armor;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.client.ArmorControls;
import tconstruct.library.modifier.IModifyable;

public class ArmorAbilitiesClient
{
    Minecraft mc;
    ArmorControls controlInstance;

    ItemStack prevFeet;
    double prevMotionY;

    boolean morphed;
    boolean morphLoaded = Loader.isModLoaded("Morph");
    boolean smartmoveLoaded = Loader.isModLoaded("SmartMoving");

    float prevMouseSensitivity;
    boolean sprint;

    public ArmorAbilitiesClient(Minecraft mc, ArmorControls p)
    {
        this.mc = mc;
        this.controlInstance = p;
    }

    @SubscribeEvent
    public void playerTick (TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        TPlayerStats stats = TPlayerStats.get(player);
        if (mc.thePlayer.onGround)
        {
            controlInstance.landOnGround();
        }
        if (stats.climbWalls && player.isCollidedHorizontally && !player.isSneaking())
        {
            player.motionY = 0.1176D;
            player.fallDistance = 0.0f;
        }

        //Feet changes - moved to server side
        /*ItemStack feet = player.getCurrentArmor(0);
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
        }*/

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

        if (morphLoaded)
        {
            if (morph.api.Api.hasMorph(player.getCommandSenderName(), event.side.isClient()))
            {
                morphed = true;
            }
        }

        if (!player.isPlayerSleeping() && !morphed && !smartmoveLoaded)
        {
            ItemStack chest = player.getCurrentArmor(2);
            if (chest == null || !(chest.getItem() instanceof IModifyable))
            {
                PlayerAbilityHelper.setEntitySize(player, 0.6F, 1.8F);
            }
            else
            {
                NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                int dodge = tag.getInteger("Perfect Dodge");
                if (dodge > 0)
                {
                    PlayerAbilityHelper.setEntitySize(player, Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                }
            }
        }
    }

    EntityPlayer getPlayer ()
    {
        return mc.thePlayer;
    }
}
