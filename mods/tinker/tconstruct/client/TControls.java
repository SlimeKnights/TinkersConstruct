package mods.tinker.tconstruct.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import mods.tinker.tconstruct.client.gui.InventoryTab;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TControls extends TKeyHandler
{
	//static KeyBinding grabKey = new KeyBinding("key.grab", 29);
	//static KeyBinding stiltsKey = new KeyBinding("key.stilts", 46);
    public static KeyBinding armorKey = new KeyBinding("key.armor", 23);
	static KeyBinding jumpKey;
	static KeyBinding invKey;
	static Minecraft mc;
	boolean jumping;
	boolean doubleJump = true;
	boolean climbing = false;
	boolean onGround = false;
	boolean onStilts = false;
	
	//boolean onStilts = false;

	public TControls()
	{
		super(new KeyBinding[] { armorKey }, new boolean[] { false }, getVanillaKeyBindings(), new boolean[] { false, false });
		//System.out.println("Controls registered");
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
		        openArmorGui(mc.thePlayer.username);
		    }
		    if (kb == invKey && mc.currentScreen != null && mc.currentScreen.getClass() == GuiInventory.class)// && !mc.playerController.isInCreativeMode())
		    {
		        TProxyClient.addTabsToInventory();

		    }
			/*if (kb == jumpKey) //Double jump
			{
				if (jumping && !doubleJump)
				{
					//if (player == null)
						//player = mc.thePlayer;

					mc.thePlayer.motionY = 0.42D;
					mc.thePlayer.fallDistance = 0;

					if (mc.thePlayer.isPotionActive(Potion.jump))
					{
						mc.thePlayer.motionY += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
					}

					doubleJump = true;
					resetFallDamage(mc.thePlayer.username);
				}

				if (!jumping)
					jumping = mc.thePlayer.isAirBorne;
			}*/
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
		doubleJump = false;
		jumping = false;
	}
	
	public void resetControls()
	{
		doubleJump = false;
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
	
	public static void openArmorGui(String username)
	{
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(3);
            outputStream.writeUTF(username);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
	}
	
	static void updateServer(ByteArrayOutputStream bos)
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "TConstruct";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToServer(packet);
	}
}
