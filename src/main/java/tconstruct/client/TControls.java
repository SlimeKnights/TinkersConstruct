package tconstruct.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.potion.Potion;
import tconstruct.TConstruct;
import tconstruct.client.event.EventCloakRender;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.util.network.packet.PacketDoubleJump;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class TControls extends TKeyHandler
{
    //static KeyBinding grabKey = new KeyBinding("key.grab", 29);
    //static KeyBinding stiltsKey = new KeyBinding("key.stilts", 46);
    public static KeyBinding armorKey = new KeyBinding("key.tarmor", 24, null);
    public static KeyBinding refreshCapes = new KeyBinding("key.tcapes.reload", 88, null);
    static KeyBinding jumpKey;
    static KeyBinding invKey;
    static Minecraft mc;

    boolean jumping;
    int midairJumps = 0;
    boolean climbing = false;
    boolean onGround = false;
    boolean onStilts = false;

    int currentTab = 1;

    //boolean onStilts = false;

    public TControls()
    {
        super(new KeyBinding[] { armorKey, refreshCapes }, new boolean[] { false, false }, getVanillaKeyBindings(), new boolean[] { false, false });
        //TConstruct.logger.info("Controls registered");
    }

    private static KeyBinding[] getVanillaKeyBindings ()
    {
        mc = Minecraft.getMinecraft();
        jumpKey = mc.gameSettings.keyBindJump;
        invKey = mc.gameSettings.keyBindInventory;
        return new KeyBinding[] { jumpKey, invKey };
    }

    @Override
    public void keyDown (Type types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
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

                    midairJumps--;
                    resetFallDamage(mc.thePlayer.getDisplayName());
                }

                if (!jumping)
                    jumping = mc.thePlayer.isAirBorne;
            }
        }
        /*else if (kb == stiltsKey) //Stilts
        {
        	float size = 1.8F;
        	if (!onStilts)
        		size = 0.8F;
        	TConstruct.playerTracker.updateSize(mc.thePlayer.username, size);
        	onStilts = !onStilts;
        	//updateServer(mc.thePlayer.username, (byte) 11);
        	if (onStilts)
        	{
        		onStilts = false;
        	}
        	else
        	{
        		onStilts = true;
        	}
        }*/
    }

    @Override
    public void keyUp (Type types, KeyBinding kb, boolean tickEnd)
    {
        //landOnGround();
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

    void resetFallDamage (String name)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(10);
            outputStream.writeUTF(name);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
    }

    void updateSize (String name, float size)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(11);
            outputStream.writeUTF(name);
            outputStream.writeFloat(size);
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
        /*Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "TConstruct";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);*/

        //TODO Find out what packet should be used here
        TConstruct.packetPipeline.sendToServer(new PacketDoubleJump());
    }

}
