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
import net.minecraftforge.client.event.MouseEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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
	public static final String[] keyDescs = new String[] { "key.tarmor", "key.tgoggles",
			"key.tbelt", "key.tzoom"
	};
	public static KeyBinding armorKey = new KeyBinding(keyDescs[0], 24, keybindCategory);
	public static KeyBinding toggleGoggles = new KeyBinding(keyDescs[1], 34, keybindCategory);
	public static KeyBinding beltSwap = new KeyBinding(keyDescs[2], 48, keybindCategory);
	public static KeyBinding zoomKey = new KeyBinding(keyDescs[3], 44,
			keybindCategory); //TODO: Make this hold, not toggle
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

	private final KeyBinding[] keys;

	public ArmorControls() {
		getVanillaKeyBindings();
		this.keys = new KeyBinding[] {
				ArmorControls.armorKey,
				ArmorControls.toggleGoggles,
				ArmorControls.beltSwap,
				ArmorControls.zoomKey,
				null, null
		};

	}

	public void registerKeys() {
		// Register bindings
		for (KeyBinding key : this.keys) {
			if (key != null)
				ClientRegistry.registerKeyBinding(key);
		}
		if (Loader.isModLoaded("notenoughkeys"))
			Api.registerMod(TConstruct.modID, ArmorControls.keyDescs);
		// Add mc keys
		this.keys[4] = ArmorControls.jumpKey;
		this.keys[5] = ArmorControls.invKey;
	}

	private static KeyBinding[] getVanillaKeyBindings() {
		mc = Minecraft.getMinecraft();
		jumpKey = mc.gameSettings.keyBindJump;
		invKey = mc.gameSettings.keyBindInventory;
		return new KeyBinding[] { jumpKey, invKey };
	}

	@SubscribeEvent
	public void mouseEvent(MouseEvent event) {
		if (!Loader.isModLoaded("notenoughkeys")) this.checkKeys(event.button + 100);
	}

	@SubscribeEvent
	public void keyEvent(KeyInputEvent event) {
		if (!Loader.isModLoaded("notenoughkeys")) this.checkKeys(-1);
	}

	@Optional.Method(modid = "notenoughkeys")
	@SubscribeEvent
	public void keyEventSpecial(KeyBindingPressedEvent event) {
		this.sendPressed(event.keyBinding, event.isKeyBindingPressed);
	}

	private void checkKeys(int keycode) {
		for (KeyBinding key : this.keys) {
			if (keycode < 0 || key.getKeyCode() == keycode)
				this.checkKeyAndSendPress(key);
		}
	}
	
	private void checkKeyAndSendPress(KeyBinding key) {
		this.sendPress(key, this.isKeyActive(key.getKeyCode()));
	}
	
	private void sendPress(KeyBinding key, boolean isPressed) {
		if (isPressed) this.keyPressed(key);
	}

	private boolean isKeyActive(int keyCode) {
		if (keyCode < 0)
			return Mouse.isButtonDown(keyCode + 100);
		else
			return Keyboard.isKeyDown(keyCode);
	}

	private void keyPressed(KeyBinding key) {
		if (key == ArmorControls.armorKey) {
			openArmorGui();
		}
		if (key == ArmorControls.jumpKey) {
			if (mc.thePlayer.capabilities.isCreativeMode)
				return;

			if (jumping && midairJumps > 0) {
				mc.thePlayer.motionY = 0.42D;
				mc.thePlayer.fallDistance = 0;

				if (mc.thePlayer.isPotionActive(Potion.jump)) {
					mc.thePlayer.motionY += (double) (
							(float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier()
									+ 1) * 0.1F);
				}

				midairJumps--;
				resetFallDamage();
			}

			if (!jumping) {
				jumping = mc.thePlayer.isAirBorne;
				ItemStack shoes = mc.thePlayer.getCurrentArmor(0);
				if (shoes != null && shoes.hasTagCompound() && shoes.getTagCompound()
						.hasKey("TinkerArmor")) {
					NBTTagCompound shoeTag = shoes.getTagCompound().getCompoundTag("TinkerArmor");
					midairJumps += shoeTag.getInteger("Double-Jump");
				}
				ItemStack wings = mc.thePlayer.getCurrentArmor(1);
				if (wings != null && wings.hasTagCompound() && wings.getTagCompound()
						.hasKey("TinkerArmor")) {
					NBTTagCompound shoeTag = wings.getTagCompound().getCompoundTag("TinkerArmor");
					midairJumps += shoeTag.getInteger("Double-Jump");
				}
			}
		}
		if (mc.currentScreen == null) {
			if (key == ArmorControls.toggleGoggles) {
				ItemStack goggles = mc.thePlayer.getCurrentArmor(3);
				if (goggles != null && goggles.getItem() instanceof TravelGear) //TODO: Genericize this
				{
					if(goggles.hasTagCompound() && goggles.getTagCompound().getCompoundTag(((TravelGear) goggles.getItem()).getBaseTagName()).getBoolean("Night Vision")) {
						activeGoggles = !activeGoggles;
						PlayerAbilityHelper.toggleGoggles(mc.thePlayer, activeGoggles);
						toggleGoggles();
					}
				}
			}
			if (key == ArmorControls.beltSwap) {
				if (ArmorProxyClient.armorExtended.inventory[3] != null) {
					PlayerAbilityHelper.swapBelt(mc.thePlayer, ArmorProxyClient.armorExtended);
					toggleBelt();
				}
			}
			if (key == ArmorControls.zoomKey) {
				zoom = !zoom;
			}
		}
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

	private void toggleBelt() {
		AbstractPacket packet = new BeltPacket();
		updateServer(packet);
	}

	static void updateServer(AbstractPacket abstractPacket) {
		TConstruct.packetPipeline.sendToServer(abstractPacket);
	}

}
