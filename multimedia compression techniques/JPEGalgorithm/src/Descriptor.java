public class Descriptor {
    private int numOfZeros;
    private int value;
    Descriptor() {
       numOfZeros = Integer.parseInt(null);
       value = Integer.parseInt(null);
    }
    Descriptor(int numOfZeros, int value) {
        this.numOfZeros = numOfZeros;
        this.value = value;
    }
    Descriptor(String desc) {
        numOfZeros = Integer.parseInt(String.valueOf(desc.charAt(0)));
        value = Integer.parseInt(String.valueOf(desc.charAt(2)));
    }
    public String descriptorToStrinG() {
        String d =(Integer.toString(numOfZeros) + "/" + Integer.toString(value));
        //System.out.println(d);
        return d ;
    }
    public int getNumOfZeros() {
        return numOfZeros;
    }

    public int getValue() {
        return value;
    }
}
