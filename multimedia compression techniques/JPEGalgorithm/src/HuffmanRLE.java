import java.util.ArrayList;
import java.util.HashMap;

public class HuffmanRLE {
    //
    private static HashMap<String,String> HuffmanTable = new HashMap<>();
    private static ArrayList<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
    private static ArrayList<String> descriptors =  new ArrayList<>();
    private static ArrayList<String> additionalBits = new ArrayList<>();
    private static ArrayList<Integer> mainMessage = new ArrayList<>();

    private static String convertToBinary(int value , int length) {
        int absoluteValue = Math.abs(value);
        int numberOfBits = (int) (Math.log(absoluteValue)/Math.log(2))+1;
        StringBuilder padding  = new StringBuilder();
        for (int i = 0; i < length; i++) {
            padding.append('0');
        }
        if (value < 0) {
            for (int i = 0; i < numberOfBits; i++) {
                absoluteValue = (absoluteValue ^ (1<<i));
            }
        }
        StringBuilder sb = new StringBuilder(padding);
        String result =Integer.toBinaryString(absoluteValue);
        return  sb.substring(result.length())+result;
    }


    private static void divideTheStringIntoGroups(ArrayList<Integer> input) {
        ArrayList<Integer> group ;
        for (int i = 0; i < input.size(); i++) {
            group = new ArrayList<>();
            while (input.get(i) == 0) {
                group.add(input.get(i));
                i++;
            }
           group.add(input.get(i));
            groups.add(group);
        }
    }

    private static void getDescriptorForEachGroup() {
        for (ArrayList group : groups) {
            int numberOfZeros = 0;
            int value = 0;
            for (int i = 0; i < group.size(); i++) {
                if (group.get(i).equals(0)) {
                    numberOfZeros++;
                }
                else value = Integer.parseInt(group.get(i).toString());
            }
            int absoluteValue = Math.abs(value);
            int numberOfBits = (int) (Math.log(absoluteValue)/Math.log(2))+1;
            Descriptor d = new Descriptor(numberOfZeros,numberOfBits);
            additionalBits.add(convertToBinary(value,numberOfBits));
            descriptors.add(d.descriptorToStrinG());
        }
        descriptors.add("EOB");
        additionalBits.add("");
    }

    private static StringBuilder desriptorToShortCode() {
        HuffmanTable.put("0/2","10");
        HuffmanTable.put("1/1","010");
        HuffmanTable.put("2/1","11");
        HuffmanTable.put("2/2","00");
        HuffmanTable.put("EOB","011");
        StringBuilder outString =  new StringBuilder();
        int indexOfAddBits = -1;
        for (String s : descriptors) {
            String shortCode = HuffmanTable.get(s);
            outString.append(shortCode);
            outString.append(additionalBits.get(++indexOfAddBits));
        }
        return outString;
    }

    private static void getDescriptorsDecode(String input) {
        /*HuffmanTable.put("10","0/2");
        HuffmanTable.put("010","1/1");
        HuffmanTable.put("11","2/1");
        HuffmanTable.put("00","2/2");
        HuffmanTable.put("011","EOB");
        */String shortCode = "";
        ArrayList<String> descs = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            shortCode+=input.charAt(i);
            if (HuffmanTable.containsKey(shortCode)) {
                //System.out.println(shortCode + " ** ");
                String descroptor = HuffmanTable.get(shortCode);
                //System.out.println(descroptor+" /////////");
                if (descroptor != "EOB") {
                    Descriptor d = new Descriptor (descroptor);
                    descs.add(descroptor);

                    int lengthOfAddBits = d.getValue();
                    String addBits = "";
                    for (int j = i + 1; j < lengthOfAddBits + i + 1; j++) {
                        addBits+=input.charAt(j);
                    }
                    //System.out.println(addBits +  "BBBB");
                    i+=lengthOfAddBits;
                    constructMessage(d.getNumOfZeros(),d.getValue(),addBits);
                    shortCode = "";
                }
            }
            //shortCode="";
        }
        //return descs;
    }

    private static void constructMessage(int numberOfZeros,int nonZeroNumber,String addBits) {
        String bits = addBits;
        int flag = 1;
        if (addBits.charAt(0)=='0') {
            bits = "";
            flag = -1;
            for (int i = 0; i < addBits.length(); i++) {
                if(addBits.charAt(i)=='0') bits+='1';
                else bits+='0';
            }
        }
        int value = Integer.parseInt(bits,2)*flag;
        for (int i = 0; i < numberOfZeros; i++) {
            mainMessage.add(0);
        }
        mainMessage.add(value);
    }

    public static void showGroups() {
        for (ArrayList group : groups) {
            int c = 0;
            for (int i = 0; i < group.size(); i++) {
                System.out.print(group.get(i)+" ");
            }
            System.out.println();
        }
    }

    public static void showDescriptors() {
        for (String d : descriptors) {
            System.out.println(d);
        }
    }

    public static void showAddtionalBits() {
        for (String addBits: additionalBits) {
            System.out.println(addBits);
        }
    }

    public static void showMainMessage() {
        for (Integer x : mainMessage) {
            System.out.print(x);
        }
    }

    public static void run (String input) {
        ArrayList<Integer> data = new ArrayList<>();

        for (int i=0;i<input.length()-3;i++) {
            int number =  1 ;
            if (input.charAt(i) == '-') {
               i++;
               number = -1;
            }
            number = number * Integer.parseInt(String.valueOf(input.charAt(i)));
            data.add(number);
        }
        divideTheStringIntoGroups(data);
        getDescriptorForEachGroup();
        StringBuilder s = desriptorToShortCode();
        System.out.println(s);
        showGroups();
        showDescriptors();
        showAddtionalBits();
    }

    public static void runD(String in) {
        getDescriptorsDecode(in);
        showMainMessage();
    }



    public static void main(String[] args) {
//        String s = "-2,0,0,2,0,0,3,2,0,1,0,0,-2,0,-1,0,0,1,0,0,-1,EOB";
        //String s = "-200200320100-20-100100-1EOB";
        //run(s);
        //0/2
        runD("1001001000111010010100010100111110011");
        //1001001000111010010100010100111110011
    }

}
