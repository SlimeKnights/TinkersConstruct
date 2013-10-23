package mods.battlegear2.api.heraldry;

import mods.battlegear2.utils.BattlegearUtils;

import java.io.*;
import java.util.Arrays;

public class Crest {

    public static int dataSize = 4 + 4 + 2 + 1 + 1 + 1;
    private short imageIndex;
    private int[] crestColours;
    private byte size;
    private byte x;
    private byte y;
    private byte[] byteArray;


    public Crest(int[] crestColours, int imageIndex, byte size, byte x, byte y) {
        this.crestColours = crestColours;
        this.imageIndex = (short) imageIndex;
        this.size = size;
        this.x = x;
        this.y = y;
    }

    public Crest(byte[] crestData){
        DataInputStream input = null;

        try{
            input = new DataInputStream(new ByteArrayInputStream(crestData));

            crestColours = new int[]{input.readInt(), input.readInt()};
            imageIndex = input.readShort();
            size = input.readByte();
            x = input.readByte();
            y = input.readByte();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(input != null){
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getByteArray(){
        if(byteArray != null){
            return byteArray;
        }else{
            DataOutputStream output = null;

            ByteArrayOutputStream bos = null;

            try{
                bos = new ByteArrayOutputStream();
                output = new DataOutputStream(bos);

                output.writeInt(crestColours[0]);
                output.writeInt(crestColours[1]);

                output.writeShort(imageIndex);
                output.writeInt(size);
                output.writeByte(x);
                output.writeByte(y);

                byteArray = bos.toByteArray();

                return byteArray;
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                try{
                    if(output != null){
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
