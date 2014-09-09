package tconstruct.client;

import cpw.mods.fml.common.gameevent.TickEvent.Type;
import mantle.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.TConstruct;
import tconstruct.armor.*;
import tconstruct.armor.items.TravelGear;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.util.network.*;

public class ArmorControls extends TKeyHandler
{
    public static final String keybindCategory = "tconstruct.keybindings";
    public static KeyBinding armorKey = new KeyBinding("key.tarmor", 24, keybindCategory);
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
    public static boolean zoom = false;
    boolean activeGoggles = false; //TODO: Set this on server login

    int currentTab = 1;

    // boolean onStilts = false;

    public ArmorControls()
    {
        super(new KeyBinding[] { armorKey, toggleGoggles, beltSwap, zoomKey }, new boolean[] { false, false, false, false }, getVanillaKeyBindings(), new boolean[] { false, false });
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
            if (kb == invKey && mc.currentScreen != null && mc.currentScreen.getClass() == GuiInventory.class)// &&// !mc.playerController.isInCreativeMode())
            {
                MinecraftForge.EVENT_BUS.register(new TabRegistry());
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

            if (mc.currentScreen == null)
            {
                if (kb == toggleGoggles)
                {
                    ItemStack goggles = mc.thePlayer.getCurrentArmor(3);
                    if (goggles != null && goggles.getItem() instanceof TravelGear) //TODO: Genericize this
                    {
                        activeGoggles = !activeGoggles;
                        toggleGoggles();
                    }
                }
                if (kb == beltSwap)
                {
                    if (ArmorProxyClient.armorExtended.inventory[3] != null)
                    {
                        PlayerAbilityHelper.swapBelt(mc.thePlayer, ArmorProxyClient.armorExtended);
                        toggleBelt();
                    }
                }
                if (kb == zoomKey)
                    zoom = !zoom;
            }
        }
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
    }

    void resetFallDamage ()
    {
        AbstractPacket packet = new DoubleJumpPacket();
        updateServer(packet);
    }

    public static void openArmorGui ()
    {
        AbstractPacket packet = new AccessoryInventoryPacket(ArmorProxyCommon.armorGuiID);
        updateServer(packet);
    }

    public static void openKnapsackGui ()
    {
        AbstractPacket packet = new AccessoryInventoryPacket(ArmorProxyCommon.knapsackGuiID);
        updateServer(packet);
    }

    private void toggleGoggles ()
    {
        AbstractPacket packet = new GogglePacket(activeGoggles);
        updateServer(packet);
    }

    private void toggleBelt ()
    {
        AbstractPacket packet = new BeltPacket();
        updateServer(packet);
    }

    static void updateServer (AbstractPacket abstractPacket)
    {
        TConstruct.packetPipeline.sendToServer(abstractPacket);
    }

}
