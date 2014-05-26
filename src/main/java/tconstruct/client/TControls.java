package tconstruct.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.ForgeHooks;
import tconstruct.TConstruct;
import tconstruct.client.event.EventCloakRender;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.PlayerAbilityHelper;
import tconstruct.items.armor.TravelGear;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TControls extends TKeyHandler
{
    //static KeyBinding grabKey = new KeyBinding("key.grab", 29);
    //static KeyBinding stiltsKey = new KeyBinding("key.stilts", 46);
    public static KeyBinding armorKey = new KeyBinding("key.tarmor", 24);
    public static KeyBinding refreshCapes = new KeyBinding("key.tcapes.reload", 88);
    public static KeyBinding toggleGoggles = new KeyBinding("key.tgoggles", 34);
    public static KeyBinding beltSwap = new KeyBinding("key.tbelt", 48);
    public static KeyBinding zoomKey = new KeyBinding("key.tzoom", 29);
    static KeyBinding jumpKey;
    static KeyBinding invKey;
    static Minecraft mc;

    boolean jumping;
    int midairJumps = 0;
    boolean climbing = false;
    boolean onGround = false;
    boolean onStilts = false;
    public static boolean zoom = false;

    int currentTab = 1;

    //boolean onStilts = false;

    public TControls()
    {
        super(new KeyBinding[] { armorKey, refreshCapes, toggleGoggles, beltSwap, zoomKey }, new boolean[] { false, false, false, false, false }, getVanillaKeyBindings(),
                new boolean[] { false, false });
    }

    private static KeyBinding[] getVanillaKeyBindings ()
    {
        mc = Minecraft.getMinecraft();
        jumpKey = mc.gameSettings.keyBindJump;
        invKey = mc.gameSettings.keyBindInventory;
        return new KeyBinding[] { jumpKey, invKey };
    }

    @Override
    public String getLabel ()
    {
        return null;
    }

    @Override
    public void keyDown (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        if (tickEnd && mc.theWorld != null)
        {
            if (kb == armorKey && mc.currentScreen == null) //Extended Armor
            {
                openArmorGui();//mc.thePlayer.username);
            }
            if (kb == invKey && mc.currentScreen != null && mc.currentScreen.getClass() == GuiInventory.class)// && !mc.playerController.isInCreativeMode())
            {
                TabRegistry.addTabsToInventory((GuiContainer) mc.currentScreen);
            }
            if (kb == refreshCapes && mc.currentScreen == null)
            {
                EventCloakRender.instance.refreshCapes();
            }
            if (kb == jumpKey) //Double jump
            {
                if (mc.thePlayer.capabilities.isCreativeMode)
                    return;

                if (jumping && midairJumps > 0)
                {
                    mc.thePlayer.motionY = 0.42D;
                    mc.thePlayer.fallDistance = 0;

                    if (mc.thePlayer.isPotionActive(Potion.jump))
                    {
                        mc.thePlayer.motionY += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                    }
                    ForgeHooks.onLivingJump(mc.thePlayer);

                    midairJumps--;
                    updateServer((byte) 10);
                }

                if (!jumping)
                {
                    jumping = mc.thePlayer.isAirBorne;
                    ItemStack shoes = mc.thePlayer.getCurrentArmor(0);
                    if (shoes != null && shoes.hasTagCompound() && shoes.getTagCompound().hasKey("TinkerArmor"))
                    {
                        NBTTagCompound shoeTag = shoes.getTagCompound().getCompoundTag("TinkerArmor");
                        midairJumps += shoeTag.getInteger("Double-Jump");
                    }
                    ItemStack wings = mc.thePlayer.getCurrentArmor(1);
                    if (wings != null && wings.hasTagCompound() && wings.getTagCompound().hasKey("TinkerArmor"))
                    {
                        NBTTagCompound shoeTag = wings.getTagCompound().getCompoundTag("TinkerArmor");
                        midairJumps += shoeTag.getInteger("Double-Jump");
                    }
                }
            }
            if (mc.currentScreen == null)
            {
                if (kb == toggleGoggles)
                {
                    ItemStack goggles = mc.thePlayer.getCurrentArmor(3);
                    if (goggles != null && goggles.getItem() instanceof TravelGear) //TODO: Genericize this
                    {
                        PlayerAbilityHelper.toggleGoggles(mc.thePlayer);
                        updateServer((byte) 9);
                    }
                }
                if (kb == beltSwap)
                {
                    TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(mc.thePlayer.username);
                    if (stats.armor.inventory[3] != null)
                    {
                        //PlayerAbilityHelper.swapBelt(mc.thePlayer, stats);
                        updateServer((byte) 8);
                    }
                }
                if (kb == zoomKey)
                    zoom = !zoom;
            }
        }
    }

    @Override
    public void keyUp (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {
        //landOnGround();
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    public void landOnGround ()
    {
        midairJumps = 0;
        jumping = false;
    }

    public void resetControls ()
    {
        midairJumps = 0;
        jumping = false;
        climbing = false;
        onGround = false;
        onStilts = false;
    }

    /* Packet IDs:
     * 8 - Swaps belt
     * 9 - Toggle goggles
     * 10 - reset fall damage 
     */

    void updateServer (byte packetID)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(packetID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
    }

    public static void openInventoryGui ()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(3);
            outputStream.writeByte(0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
    }

    public static void openArmorGui ()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(3);
            outputStream.writeByte(1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
    }

    public static void openKnapsackGui ()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(3);
            outputStream.writeByte(2);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
    }

    static void updateServer (ByteArrayOutputStream bos)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "TConstruct";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);
    }
}
