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
public class GuiElementScalable extends GuiElementDuex {

    public GuiElementScalable(int x, int y, int w, int h, int texW, int texH) {
        super(x, y, w, h, texW, texH);
    }

    public GuiElementScalable(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public int drawScaledX(int xPos, int yPos, int width) {
        for(int i = 0; i < width / w; i++) {
            draw(xPos + i * w, yPos);
        }
        // remainder that doesn't fit total width
        int remainder = width % w;
        if(remainder > 0) {
            GuiScreen.func_146110_a(xPos + width - remainder, yPos, x, y, remainder, h, texW, texH);
        }

        return width;
    }

    public int drawScaledY(int xPos, int yPos, int height) {
        for(int i = 0; i < height / h; i++) {
            draw(xPos, yPos + i * h);
        }
        // remainder that doesn't fit total width
        int remainder = height % h;
        if(remainder > 0) {
            // drawModalRectWithCustomSizedTexture
            GuiScreen.func_146110_a(xPos, yPos + height - remainder, x, y, w, remainder, texW, texH);
        }

        return w;
    }

    public int drawScaled(int xPos, int yPos, int width, int height) {
        // we draw full height row-wise
        int full = height / this.h;
        for(int i = 0; i < full; i++) {
            drawScaledX(xPos, yPos + i * this.h, width);
        }

        yPos += full * this.h;

        // and the remainder is drawn manually
        int yRest = height % this.h;
        // the same as drawScaledX but with the remaining height
        for(int i = 0; i < width / w; i++) {
            drawScaledY(xPos + i * w, yPos, yRest);
        }
        // remainder that doesn't fit total width
        int remainder = width % w;
        if(remainder > 0) {
            // drawModalRectWithCustomSizedTexture
            GuiScreen.func_146110_a(xPos + width - remainder, yPos, x, y, remainder, yRest, texW, texH);
        }

        return width;
    }

    @Override
    public GuiElementScalable shift(int xd, int yd) {
        GuiElementScalable element = new GuiElementScalable(this.x + xd, this.y + yd, this.w, this.h);
        element.setTextureSize(texW, texH);
        return element;
    }
}