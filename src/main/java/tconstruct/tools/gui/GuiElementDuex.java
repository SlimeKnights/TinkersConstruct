package tconstruct.tools.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

/*
 * Taken from Mantle 1.12-1.3.3.49 under the MIT License
 *
 * The MIT License (MIT) Copyright (c) 2013-2014 Slime Knights (mDiyo, fuj1n, Sunstrike, progwml6, pillbox, alexbegt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

@SideOnly(Side.CLIENT)
public class GuiElementDuex {

    // this is totally completely ugly but it's a simple solution that doesn't clutter everything too much >_>
    public static int defaultTexW = 256;
    public static int defaultTexH = 256;

    public final int x;
    public final int y;
    public final int w;
    public final int h;

    public int texW;
    public int texH;

    public GuiElementDuex(int x, int y, int w, int h, int texW, int texH) {
        this(x, y, w, h);
        setTextureSize(texW, texH);

        defaultTexW = texW;
        defaultTexH = texH;
    }

    public GuiElementDuex(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        setTextureSize(defaultTexW, defaultTexH);
    }

    public GuiElementDuex setTextureSize(int w, int h) {
        texW = w;
        texH = h;

        return this;
    }

    public GuiElementDuex shift(int xd, int yd) {
        return new GuiElementDuex(this.x + xd, this.y + yd, this.w, this.h, this.texW, this.texH);
    }

    /**
     * Draws the element at the given x/y coordinates
     *
     * @param xPos X-Coordinate on the screen
     * @param yPos Y-Coordinate on the screen
     */
    public int draw(int xPos, int yPos) {
        // drawModalRectWithCustomSizedTexture
        GuiScreen.func_146110_a(xPos, yPos, x, y, w, h, texW, texH);
        return w;
    }

    public static class Builder {

        public int w;
        public int h;

        public Builder(int w, int h) {
            this.w = w;
            this.h = h;
        }

        public GuiElementDuex get(int x, int y, int w, int h) {
            return new GuiElementDuex(x, y, w, h, this.w, this.h);
        }
    }
}