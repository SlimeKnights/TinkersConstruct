package tinker.tconstruct.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public abstract class TKeyHandler implements ITickHandler
{
    protected KeyBinding[] keyBindings;
    protected boolean[] keyDown;
    protected boolean[] repeatings;
    private boolean isDummy;

    /**
     * Pass an array of keybindings and a repeat flag for each one
     *
     * @param keyBindings
     * @param repeatings
     */
    public TKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings)
    {
        assert keyBindings.length == repeatings.length : "You need to pass two arrays of identical length";
        this.keyBindings = keyBindings;
        this.repeatings = repeatings;
        this.keyDown = new boolean[keyBindings.length];
    }


    /**
     * Register the keys into the system. You will do your own keyboard management elsewhere. No events will fire
     * if you use this method
     *
     * @param keyBindings
     */
    public TKeyHandler(KeyBinding[] keyBindings)
    {
        this.keyBindings = keyBindings;
        this.isDummy = true;
    }

    public KeyBinding[] getKeyBindings()
    {
        return this.keyBindings;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        keyTick(type, false);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        keyTick(type, true);
    }

    private void keyTick(EnumSet<TickType> type, boolean tickEnd)
    {
        for (int i = 0; i < keyBindings.length; i++)
        {
            KeyBinding keyBinding = keyBindings[i];
            int keyCode = keyBinding.keyCode;
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != keyDown[i] || (state && repeatings[i]))
            {
                if (state)
                {
                    keyDown(type, keyBinding, tickEnd, state!=keyDown[i]);
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
    }

    /**
     * Called when the key is first in the down position on any tick from the {@link #ticks()}
     * set. Will be called subsequently with isRepeat set to true
     *
     * @see #keyUp(EnumSet, KeyBinding, boolean)
     *
     * @param types the type(s) of tick that fired when this key was first down
     * @param tickEnd was it an end or start tick which fired the key
     * @param isRepeat is it a repeat key event
     */
    public abstract void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat);
    /**
     * Fired once when the key changes state from down to up
     *
     * @see #keyDown(EnumSet, KeyBinding, boolean, boolean)
     *
     * @param types the type(s) of tick that fired when this key was first down
     * @param tickEnd was it an end or start tick which fired the key
     */
    public abstract void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd);


    /**
     * This is the list of ticks for which the key binding should trigger. The only
     * valid ticks are client side ticks, obviously.
     *
     * @see cpw.mods.fml.common.ITickHandler#ticks()
     */
    public abstract EnumSet<TickType> ticks();
}
