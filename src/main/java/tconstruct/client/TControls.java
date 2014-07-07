package tconstruct.client;

import mantle.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import tconstruct.TConstruct;
import tconstruct.armor.ArmorProxyCommon;
import tconstruct.client.event.EventCloakRender;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.util.network.packet.PacketDoubleJump;
import tconstruct.util.network.packet.PacketExtendedInventory;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class TControls extends TKeyHandler
{
    public static final String keybindCategory = "tconstruct.keybindings";
    public static KeyBinding armorKey = new KeyBinding("key.tarmor", 24, keybindCategory);
    public static KeyBinding refreshCapes = new KeyBinding("key.tcapes.reload", 88, keybindCategory);
    public static KeyBinding toggleGoggles = new KeyBinding("key.tgoggles", 34, keybindCategory);
    public static KeyBinding beltSwap = new KeyBinding("key.tbelt", 48, keybindCategory);
    public static KeyBinding zoomKey = new KeyBinding("key.tzoom", 44, keybindCategory); //TODO: Make this hold, not toggle
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

    // boolean onStilts = false;

    public TControls()
    {
        super(new KeyBinding[] { armorKey, refreshCapes, toggleGoggles, beltSwap, zoomKey }, new boolean[] { false, false, false, false, false }, getVanillaKeyBindings(), new boolean[] { false, false });
        /*ClientRegistry.registerKeyBinding(armorKey);
        ClientRegistry.registerKeyBinding(refreshCapes);
        ClientRegistry.registerKeyBinding(toggleGoggles);
        ClientRegistry.registerKeyBinding(beltSwap);
        ClientRegistry.registerKeyBinding(zoomKey);*/
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

            /*if (mc.currentScreen == null)
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
            }*/
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

    void resetFallDamage ()
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
        AbstractPacket packet = new PacketExtendedInventory(ArmorProxyCommon.armorGuiID);
        updateServer(packet);
    }

    public static void openKnapsackGui ()
    {
        AbstractPacket packet = new PacketExtendedInventory(ArmorProxyCommon.knapsackGuiID);
        updateServer(packet);
    }

    static void updateServer (AbstractPacket abstractPacket)
    {
        TConstruct.packetPipeline.sendToServer(abstractPacket);
    }

}
