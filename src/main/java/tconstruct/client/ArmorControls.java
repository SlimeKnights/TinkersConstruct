package tconstruct.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import mantle.common.network.AbstractPacket;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import tconstruct.TConstruct;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.armor.ArmorProxyCommon;
import tconstruct.armor.PlayerAbilityHelper;
import tconstruct.armor.items.TravelGear;
import tconstruct.util.network.AccessoryInventoryPacket;
import tconstruct.util.network.BeltPacket;
import tconstruct.util.network.DoubleJumpPacket;
import tconstruct.util.network.GogglePacket;

public class ArmorControls {

    public static final String keybindCategory = "tconstruct.keybindings";
    public static final String[] keyDescs = new String[] {"key.tarmor", "key.tgoggles", "key.tbelt", "key.tzoom"};
    public static KeyBinding armorKey = new KeyBinding(keyDescs[0], Keyboard.KEY_NONE, keybindCategory);
    public static KeyBinding toggleGoggles = new KeyBinding(keyDescs[1], Keyboard.KEY_NONE, keybindCategory);
    public static KeyBinding beltSwap = new KeyBinding(keyDescs[2], Keyboard.KEY_NONE, keybindCategory);
    // TODO: Make this hold, not toggle
    public static KeyBinding zoomKey = new KeyBinding(keyDescs[3], Keyboard.KEY_NONE, keybindCategory);
    static KeyBinding jumpKey;
    static KeyBinding invKey;
    static Minecraft mc;

    boolean jumping;
    int midairJumps = 0;
    boolean climbing = false;
    boolean onGround = false;
    public static boolean zoom = false;
    boolean activeGoggles = false; // TODO: Set this on server login

    int currentTab = 1;
    // boolean onStilts = false;

    private final KeyBinding[] keys;

    public ArmorControls() {
        getVanillaKeyBindings();
        keys = new KeyBinding[] {armorKey, toggleGoggles, beltSwap, zoomKey, null, null};
    }

    public void registerKeys() {
        // Register bindings
        for (KeyBinding key : keys) {
            if (key != null) {
                ClientRegistry.registerKeyBinding(key);
            }
        }
        if (Loader.isModLoaded("notenoughkeys")) {
            Api.registerMod(TConstruct.modID, keyDescs);
        }
        // Add mc keys
        keys[4] = jumpKey;
        keys[5] = invKey;
    }

    private static void getVanillaKeyBindings() {
        mc = Minecraft.getMinecraft();
        jumpKey = mc.gameSettings.keyBindJump;
        invKey = mc.gameSettings.keyBindInventory;
    }

    @SubscribeEvent
    public void keyEvent(KeyInputEvent event) {
        if (!Loader.isModLoaded("notenoughkeys")) {
            checkAndPerformKeyActions(null, false);
        }
    }

    @Optional.Method(modid = "notenoughkeys")
    @SubscribeEvent
    public void keyEventSpecial(KeyBindingPressedEvent event) {
        if (event.keyBinding != null && event.isKeyBindingPressed) {
            checkAndPerformKeyActions(event.keyBinding, true);
        }
    }

    private void checkAndPerformKeyActions(KeyBinding keyBinding, boolean inputFromNotEnoughKeys) {
        if (inputFromNotEnoughKeys ? keyBinding == armorKey : armorKey.isPressed()) {
            openArmorGui();
            return;
        }
        if (inputFromNotEnoughKeys ? keyBinding == jumpKey : jumpKey.isPressed()) {
            performHigherJump();
            return;
        }
        if (mc.currentScreen == null) {
            if (inputFromNotEnoughKeys ? keyBinding == toggleGoggles : toggleGoggles.isPressed()) {
                checkAndToggleNightVision();
                return;
            }
            if (inputFromNotEnoughKeys ? keyBinding == beltSwap : beltSwap.isPressed()) {
                doBeltSwapIfPossible();
                return;
            }
            if (inputFromNotEnoughKeys ? keyBinding == zoomKey : zoomKey.isPressed()) {
                zoom = !zoom;
            }
        }
    }

    private void performHigherJump() {
        if (mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        if (jumping && midairJumps > 0) {
            mc.thePlayer.motionY = 0.42D;
            mc.thePlayer.fallDistance = 0;
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                mc.thePlayer.motionY +=
                        (float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }
            midairJumps--;
            resetFallDamage();
        }
        if (!jumping) {
            jumping = mc.thePlayer.isAirBorne;
            ItemStack shoes = mc.thePlayer.getCurrentArmor(0);
            if (shoes != null
                    && shoes.hasTagCompound()
                    && shoes.getTagCompound().hasKey("TinkerArmor")) {
                NBTTagCompound shoeTag = shoes.getTagCompound().getCompoundTag("TinkerArmor");
                midairJumps += shoeTag.getInteger("Double-Jump");
            }
            ItemStack wings = mc.thePlayer.getCurrentArmor(1);
            if (wings != null
                    && wings.hasTagCompound()
                    && wings.getTagCompound().hasKey("TinkerArmor")) {
                NBTTagCompound shoeTag = wings.getTagCompound().getCompoundTag("TinkerArmor");
                midairJumps += shoeTag.getInteger("Double-Jump");
            }
        }
    }

    private void checkAndToggleNightVision() {
        ItemStack goggles = mc.thePlayer.getCurrentArmor(3);
        if (goggles != null && goggles.getItem() instanceof TravelGear) { // TODO: Genericize this
            if (goggles.hasTagCompound()
                    && goggles.getTagCompound()
                            .getCompoundTag(((TravelGear) goggles.getItem()).getBaseTagName())
                            .getBoolean("Night Vision")) {
                activeGoggles = !activeGoggles;
                PlayerAbilityHelper.toggleGoggles(mc.thePlayer, activeGoggles);
                toggleGoggles();
            }
        }
    }

    public static boolean doBeltSwapIfPossible() {
        if (ArmorProxyClient.armorExtended.inventory[3] != null) {
            PlayerAbilityHelper.swapBelt(mc.thePlayer, ArmorProxyClient.armorExtended);
            toggleBelt();
            return true;
        }
        return false;
    }

    public void landOnGround() {
        midairJumps = 0;
        jumping = false;
    }

    public void resetControls() {
        midairJumps = 0;
        jumping = false;
        climbing = false;
        onGround = false;
    }

    void resetFallDamage() {
        AbstractPacket packet = new DoubleJumpPacket();
        updateServer(packet);
    }

    public static void openArmorGui() {
        AbstractPacket packet = new AccessoryInventoryPacket(ArmorProxyCommon.armorGuiID);
        updateServer(packet);
    }

    public static void openKnapsackGui() {
        AbstractPacket packet = new AccessoryInventoryPacket(ArmorProxyCommon.knapsackGuiID);
        updateServer(packet);
    }

    private void toggleGoggles() {
        AbstractPacket packet = new GogglePacket(activeGoggles);
        updateServer(packet);
    }

    private static void toggleBelt() {
        AbstractPacket packet = new BeltPacket();
        updateServer(packet);
    }

    static void updateServer(AbstractPacket abstractPacket) {
        TConstruct.packetPipeline.sendToServer(abstractPacket);
    }
}
