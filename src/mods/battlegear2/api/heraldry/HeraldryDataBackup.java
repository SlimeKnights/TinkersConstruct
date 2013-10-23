package mods.battlegear2.api.heraldry;

import mods.battlegear2.utils.BattlegearUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class HeraldryDataBackup {


    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;

    private byte pattern;
    private short crest;
    private byte crestPosition;
    private int[] colours = new int[5];

    private byte helm;
    private byte banner;

    private byte[] byteArray = null;

    public HeraldryDataBackup(int pattern, int pattern_col_1, int pattern_col_2, int pattern_col_3, int crest, int crest_col_1, int crest_col_2, int crest_position, int helm, int banner) {
        this.pattern = (byte) pattern;
        this.crest = (short) crest;
        colours = new int[]{pattern_col_1, pattern_col_2, pattern_col_3, crest_col_1, crest_col_2};
        this.crestPosition = (byte) crest_position;
        this.helm = (byte) helm;
        this.banner = (byte) banner;
    }

    public HeraldryDataBackup(byte pattern, int pattern_col_1, int pattern_col_2, int pattern_col_3){
        this(pattern, pattern_col_1, pattern_col_2, pattern_col_3, 0, 0, 0,0, 0, 0);
    }

    public HeraldryDataBackup(byte[] data){
        DataInputStream input = null;

        try{
            input = new DataInputStream(new ByteArrayInputStream(data));

            pattern = input.readByte();;
            crest = input.readShort();
            crestPosition = input.readByte();
            colours= new int[5];
            for(int i = 0; i < colours.length; i++){
                colours[i] = input.readInt();
            }

            helm = input.readByte();
            banner = input.readByte();

            byteArray = Arrays.copyOf(data, data.length);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BattlegearUtils.closeStream(input);
        }
    }

    public static HeraldryDataBackup getDefault() {
        return new HeraldryDataBackup(10, 0xFF000000, 0xFFFFFFFF, 0xFFFFFF00, 0, 0xFF000000, 0xFF000000, 0, 0, 0);
    }

    public byte[] getByteArray(){

        if(byteArray != null){
            return byteArray;
        }

        DataOutputStream output = null;

        ByteArrayOutputStream bos = null;

        try{
            bos = new ByteArrayOutputStream();
            output = new DataOutputStream(bos);

            output.writeByte(pattern);
            output.writeShort(crest);
            output.writeByte(crestPosition);
            for(int i = 0; i < 5; i++){
                output.writeInt(colours[i]);
            }

            output.writeByte(helm);
            output.writeByte(banner);

            return bos.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(output);
        }

        return null;
    }

    public int getColourChanel(int colour, int chanelNo){
        switch (chanelNo){
            case RED:
                return (colours[colour] >> 16) & 0xFF;
            case GREEN:
                return (colours[colour] >> 8) & 0xFF;
            case BLUE:
                return (colours[colour] >> 0) & 0xFF;
            default:
                return (colours[colour] >> 16) & 0xFF;
        }
    }

    public byte getPattern() {
        return pattern;
    }

    public int getColour(int i) {
        return colours[i];
    }

    @Override
    public String toString() {
        return byteArrayToHex(getByteArray());
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    public short getCrest() {
        return crest;
    }
}
