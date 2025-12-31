package test;

import java.util.Date;

public class Message {

    public Message(String asText){
        this.asText =asText;
        this.data = asText.getBytes();
        this.date = new Date();
        double tempD;
        try {
            tempD= Double.parseDouble(asText);
        }catch (NumberFormatException e){
            tempD =Double.NaN;
        }
        this.asDouble = tempD;
    }

    public Message(byte[] data){
        this(new String(data));
    }

    public Message(double asDouble){
        this(String.valueOf(asDouble));
    }



    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;


}
