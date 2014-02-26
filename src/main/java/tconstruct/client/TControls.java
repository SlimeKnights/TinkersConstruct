package tconstruct.client;

import cpw.mods.fml.common.gameevent.TickEvent.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.potion.Potion;
import tconstruct.TConstruct;
import tconstruct.client.event.EventCloakRender;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TProxyCommon;
import tconstruct.util.network.packet.AbstractPacket;
import tconstruct.util.network.packet.PacketDoubleJump;
import tconstruct.util.network.packet.PacketExtendedInventory;

public class TControls extends TKeyHandler
{
    public static final String keybindCategory = "key.tconstruct.category";
    // static KeyBinding grabKey = new KeyBinding("key.grab", 29);
    // static KeyBinding stiltsKey = new KeyBinding("key.stilts", 46);
    public static KeyBinding armorKey = new KeyBinding("key.tarmor", 24, keybindCategory);
    public static KeyBinding refreshCapes = new KeyBinding("key.tcapes.reload", 88, keybindCategory);
    static KeyBinding jumpKey;
    static KeyBinding invKey;
    static Minecraft mc;

    boolean jumping;
    int midairJumps = 0;
    boolean climbing = false;
    boolean onGround = false;
    boolean onStilts = false;

    int currentTab = 1;

    // boolean onStilts = false;

    public TControls()
    {
        super(new KeyBinding[] { armorKey, refreshCapes }, new boolean[] { false, false }, getVanillaKeyBindings(), new boolean[] { false, false });
        // TConstruct.logger.info("Controls registered");
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
            if (kb == armorKey && mc.currentScreen == null) // Extended Armor
            {
                openArmorGui();// mc.thePlayer.username);
            }
            if (kb == invKey && mc.currentScreen != null && mc.currentScreen.getClass() == GuiInventory.class)// &&
                                                                                                              // !mc.playerController.isInCreativeMode())
            {
                TabRegistry.addTabsToInventory((GuiContainer) mc.currentScreen);
            }
            if (kb == refreshCapes && mc.currentScreen == null)
            {
                EventCloakRender.instance.refreshCapes();
            }
            if (kb == jumpKey) // Double jump
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
                    resetFallDamage();
                }

                if (!jumping)
                    jumping = mc.thePlayer.isAirBorne;
            }
        }
        /*
         * else if (kb == stiltsKey) //Stilts { float size = 1.8F; if
         * (!onStilts) size = 0.8F;
         * TConstruct.playerTracker.updateSize(mc.thePlayer.username, size);
         * onStilts = !onStilts; //updateServer(mc.thePlayer.username, (byte)
         * 11); if (onStilts) { onStilts = false; } else { onStilts = true; } }
         */
    }

    @Override
    public void keyUp (Type types, KeyBinding kb, boolean tickEnd)
    {
        // landOnGround();
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

    void resetFallDamage()
    {
        AbstractPacket packet = new PacketDoubleJump();
        updateServer(packet);
    }

    void updateSize (String name, float size)
    {
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
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

        updateServer(bos);*/

        //TODO: Enable code with right packet
        //AbstractPacket packet = new ();
        //updateServer(packet);
    }

    public static void openArmorGui ()
    {
        AbstractPacket packet = new PacketExtendedInventory(TProxyCommon.armorGuiID);
        updateServer(packet);
    }

    public static void openKnapsackGui ()
    {
        AbstractPacket packet = new PacketExtendedInventory(TProxyCommon.knapsackGuiID);
        updateServer(packet);
    }

    static void updateServer (AbstractPacket abstractPacket)
    {
        TConstruct.packetPipeline.sendToServer(abstractPacket);
    }

}
