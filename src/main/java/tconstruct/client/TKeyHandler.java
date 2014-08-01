package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public abstract class TKeyHandler
{
    public KeyBinding[] keyBindings;
    public KeyBinding[] vKeyBindings;
    public boolean[] keyDown;
    public boolean[] repeatings;
    public boolean[] vRepeatings;
    public boolean isDummy;

    /**
     * Pass an array of keybindings and a repeat flag for each one
     * 
     * @param keyBindings
     * @param repeatings
     */
    public TKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings, KeyBinding[] vanillaKeys, boolean[] vanillaRepeatings)
    {
        assert keyBindings.length == repeatings.length : "You need to pass two arrays of identical length";
        assert vanillaKeys.length == vanillaRepeatings.length : "You need to pass two arrays of identical length";
        this.keyBindings = keyBindings;
        this.repeatings = repeatings;
        this.vKeyBindings = vanillaKeys;
        this.vRepeatings = vanillaRepeatings;
        this.keyDown = new boolean[keyBindings.length + vanillaKeys.length];
    }

    /**
     * Register the keys into the system. You will do your own keyboard
     * management elsewhere. No events will fire if you use this method
     * 
     * @param keyBindings
     */
    public TKeyHandler(KeyBinding[] keyBindings)
    {
        this.keyBindings = keyBindings;
        this.isDummy = true;
    }

    public KeyBinding[] getKeyBindings ()
    {
        return this.keyBindings;
    }

    @SubscribeEvent
    public void onTick (ClientTickEvent event)
    {
        if (event.side == Side.CLIENT)
        {
            if (event.phase == Phase.START)
                keyTick(event.type, false);
            else if (event.phase == Phase.END)
                keyTick(event.type, true);
        }

    }

    public void keyTick (Type type, boolean tickEnd)
    {
        for (int i = 0; i < keyBindings.length; i++)
        {
            KeyBinding keyBinding = keyBindings[i];
            int keyCode = keyBinding.getKeyCode();
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != keyDown[i] || (state && repeatings[i]))
            {
                if (state)
                {
                    keyDown(type, keyBinding, tickEnd, state != keyDown[i]);
                }
                else
                {
                    keyUp(type, keyBinding, tickEnd);
                }
                if (tickEnd)
                {
                    keyDown[i] = state;
                }
            }
        }
        for (int i = 0; i < vKeyBindings.length; i++)
        {
            KeyBinding keyBinding = vKeyBindings[i];
            int keyCode = keyBinding.getKeyCode();
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != keyDown[i + keyBindings.length] || (state && vRepeatings[i]))
            {
                if (state)
                {
                    keyDown(type, keyBinding, tickEnd, state != keyDown[i + keyBindings.length]);
                }
                else
                {
                    keyUp(type, keyBinding, tickEnd);
                }
                if (tickEnd)
                {
                    keyDown[i + keyBindings.length] = state;
                }
            }
        }
    }

    /**
     * Called when the key is first in the down position on any tick from the
     * {@link #ticks()} set. Will be called subsequently with isRepeat set to
     * true
     * 
     * @see #keyUp(EnumSet, KeyBinding, boolean)
     * 
     * @param types
     *            the type(s) of tick that fired when this key was first down
     * @param tickEnd
     *            was it an end or start tick which fired the key
     * @param isRepeat
     *            is it a repeat key event
     */
    public abstract void keyDown (Type types, KeyBinding kb, boolean tickEnd, boolean isRepeat);

    /**
     * Fired once when the key changes state from down to up
     * 
     * @see #keyDown(EnumSet, KeyBinding, boolean, boolean)
     * 
     * @param types
     *            the type(s) of tick that fired when this key was first down
     * @param tickEnd
     *            was it an end or start tick which fired the key
     */
    public abstract void keyUp (Type types, KeyBinding kb, boolean tickEnd);

}
