import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Character.isLetter;

public class ArithmeticCoding {
    private static HashMap<Character, Double> low_range = new HashMap<Character, Double>();
    private static HashMap<Character, Double> high_range = new HashMap<Character, Double>();

    public static ArrayList<String> readFileContent(String fileName) {

        ArrayList<String> fileContent = new ArrayList<>();
        try {
            //System.out.println(11 + " " + fileName);
            File file1 = new File();
            //System.out.println(fileName);
            Scanner inFile = new Scanner(file1);
            while (inFile.hasNextLine()) {
                String line = inFile.nextLine();
                fileContent.add(line);
                //System.out.println(line);
            }
            inFile.close();
        } catch (FileNotFoundException e) {
        }
        return fileContent;
    }

    public static double[] compressSymbol( double lower , double range , double lowSymbolRange ,double highSymbolRange ) {
        double lowerOfSymbol = lower + range * lowSymbolRange ;
        double upperOfSymbol = lower + range * highSymbolRange ;
        double[] R = new double[2];
        R[0] = lowerOfSymbol;
        R[1] = upperOfSymbol;
        return R;
    }

    public static double compressMessage(char[] msgToCompress) {
        double compressionCode = -0.10 , lower=0.0 , range=0.0;
        double[] Range = {0.0,1.0};
        for (char symbol : msgToCompress) {
            //System.out.println(symbol);
            lower = Range[0];
            range = Range[1]-Range[0];
            Range = compressSymbol(lower,range,low_range.get(symbol),high_range.get(symbol));
            System.out.println(Range[0] + " " +Range[1] + " " + symbol);
            compressionCode = (Range[1]+Range[0])/2;
        }
        return compressionCode;
    }

    public static void setSymbolRange(ArrayList<Character> charsOfMsg,ArrayList<Double> probs ) {
        double low = 0.0 , prob=0.0;
        //Arrays.sort(msg);
        //Scanner in = new Scanner(System.in);
        int len = charsOfMsg.size();
        for (int i = 0 ; i < len ; i++) {
            //if ((low_range.get(charsOfMsg.get(i)) == null)) {
                //System.out.print("probability of " + c + " = ");
                low_range.put(charsOfMsg.get(i),low);
                high_range.put(charsOfMsg.get(i),low+probs.get(i));
         //       System.out.println(low+ " " + (low+prob));
                low = low+probs.get(i);
            //}
        }
    }

    public static double recalculateCode (double code , double lower , double upper) {
        return (code - lower)/(upper -lower); ///decompress
    }

    public  static char getChar ( double code ) { ///decompress
        char decompressedChar = ' ';
        for (Character K : high_range.keySet()) {
            if (code <= high_range.get(K)) {
                decompressedChar = K;
                break;
            }
        }
        return decompressedChar;
    }

    public static String decompressMsg( double mainCode , int messageLenght) {
        String outResult = "";
        double[] Range = {0,1};
        double code = mainCode;
        for (int i = 0; i < messageLenght; i++) {
            char c = getChar(code);
            //System.out.println(c);
            //System.out.println(Range[0] + "  " + Range[1] + " " + code);
            outResult += c;
            Range = compressSymbol(Range[0] , Range[1]-Range[0],low_range.get(c) , high_range.get(c));
            code = recalculateCode(mainCode , Range[0] , Range[1]);
        }
        return outResult;
    }

    public static double readDataToCompress(String fileName) {
        Scanner inStr = new Scanner(System.in);
        Scanner inDbl = new Scanner(System.in);
        String messageToCompress="";
        char [] readyToCompress1, readyToCompress2;
        ArrayList<Character> charsOfMsg = new ArrayList<>();
        ArrayList<Double> probs = new ArrayList<>();
        double prob  = 0.0;
        if (fileName == " ") {
            System.out.print("Enter message:> ");
            messageToCompress = inStr.nextLine();
            readyToCompress1 = messageToCompress.toCharArray();
            readyToCompress2 = messageToCompress.toCharArray(); //
            Arrays.sort(readyToCompress2);
            for (char c : readyToCompress2) {
                if (!charsOfMsg.contains(c)) {
                    charsOfMsg.add(c);
                    System.out.print("probability of " + c + " = ");
                    prob = inDbl.nextDouble();
                    probs.add(prob);
                }
            }
        } else {
            ArrayList<String>fileContent = new ArrayList<>();
            fileContent = readFileContent(fileName);
            int i=0;
            messageToCompress = fileContent.get(i++);
            readyToCompress1 = messageToCompress.toCharArray();
            readyToCompress2 = messageToCompress.toCharArray();
            Arrays.sort(readyToCompress2);
            for (char c : readyToCompress2) {
                if (!charsOfMsg.contains(c)) {
                    charsOfMsg.add(c);
                    prob = Double.parseDouble(fileContent.get(i++));
                    probs.add(prob);
                }
            }
        }
        setSymbolRange(charsOfMsg,probs);
        System.out.println(compressMessage(readyToCompress1));
        return compressMessage(readyToCompress1);
    }

    public static String readDataToDecompress(String fileName) {
        Scanner in = new Scanner(System.in);
        Scanner inD = new Scanner(System.in);
        Scanner inI = new Scanner(System.in);
        double code ;
        String msg ;
        int lengthOftheOriginal;
        ArrayList<Double> probs = new ArrayList<>();
        ArrayList<Character> charsOfMsg = new ArrayList<Character>();
        if (fileName == " ") {
            System.out.print("Enter code :>  ");
            code = in.nextDouble();
            System.out.print("Enter the chars of messages = ");
            msg = inD.nextLine();
            for (int i = 0; i < msg.length(); i++) {
                if (isLetter(msg.charAt(i))) {
                    charsOfMsg.add(msg.charAt(i));
                }
            }
            System.out.print("Enter the length of the original message = ");
            lengthOftheOriginal = inI.nextInt();
            for (int i = 0; i < charsOfMsg.size(); i++) {
                double d = inD.nextDouble();
                probs.add(d);
            }
        } else {
            ArrayList<String> fileContent = new ArrayList<>();
            fileContent = readFileContent(fileName);
            int lineIndex=0, Nchars=0;
            System.out.println(fileContent.size()+"   size  //");
            code = Double.parseDouble(fileContent.get(lineIndex++));
            msg = fileContent.get(lineIndex++);
            System.out.println(msg);
            for (int i = 0; i < msg.length(); i++) {
                if (isLetter(msg.charAt(i))) {
                    charsOfMsg.add(msg.charAt(i));
                    Nchars++;
                    System.out.println("DDDDDDDDDDD");
                }
            }
            lengthOftheOriginal = Integer.parseInt(fileContent.get(lineIndex++));
            for (int i = 0; i < Nchars; i++) {
                probs.add(Double.parseDouble(fileContent.get(lineIndex++)));
            }
        }
        setSymbolRange(charsOfMsg,probs);
        System.out.println(decompressMsg(code,lengthOftheOriginal));
        return decompressMsg(code,lengthOftheOriginal);
        /*      System.out.print("Enter code = ");
                double code = dbl.nextDouble();
                System.out.print("Enter the chars of messages = ");
                String msg = str.nextLine();
                System.out.print("Enter the length of the original message = ");
                int lengthOftheOriginal = in.nextInt();
                char[] msgToCompress1 = msg.toCharArray();
                setSymbolRange(msgToCompress1);
                System.out.println(decompressMsg(code, lengthOftheOriginal));
                */
    }

    public static String checkFile (String fileName , char o) {
        Scanner inStr = new Scanner(System.in);
        System.out.println("*");
        if (!fileName.contains(".txt")) {
            fileName+=".txt";
        }
        System.out.println(fileName+"  PPP");
        if (!fileName.contains(":")) {
            if(o=='c')fileName = "E:\\A .. Morsi\\FCAI\\level 3\\1st\\MM\\Arithmetic coding\\compressed files\\"+fileName;
            if(o=='d')fileName = "E:\\A .. Morsi\\FCAI\\level 3\\1st\\MM\\Arithmetic coding\\deccompressed files\\"+fileName;
            //System.out.println(fileName+"    **");
        }
        //File file1 = new File(fileName);
        System.out.println(fileName);
        /*while(!file1.exists()) {
            //System.out.println(fileName+"  ***");
            System.out.println("file is not exist , please re-entering correct name");
            String s = inStr.nextLine();
            checkFile(s,o);
        }*/
        return fileName;
    }

    public static void saveAsFileDecompress(String fileName , String decompressedMessage) throws FileNotFoundException {
        String filePath = "E:\\A .. Morsi\\FCAI\\level 3\\1st\\MM\\Arithmetic coding\\compressed files\\" + fileName;
       // filePath += fileName;
        File saveFile = new File(filePath);
        PrintWriter writer = new PrintWriter(filePath);
        writer.println(decompressedMessage);
        writer.close();
    }
    public static void saveAsFileCompress(String fileName , double compressionCode) throws FileNotFoundException {
        String filePath = "E:\\A .. Morsi\\FCAI\\level 3\\1st\\MM\\Arithmetic coding\\decompressed files\\";
        filePath += fileName;
        File saveFile = new File(filePath);
        PrintWriter writer = new PrintWriter(filePath);
        writer.print(compressionCode);
        writer.close();
    }
    public static String saveOutput () {
        System.out.print("Enter the file name to save :");
        Scanner in = new Scanner(System.in);
        String fileName = in.nextLine();
        return fileName;
    }

    public static void main (String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
        Scanner str = new Scanner(System.in);
        while (true) {
            System.out.println("- Arithmetic coding - 1) compression , 2) decompression , 3) exit");
            System.out.print("# choice >> ");
            int choiceOperation = in.nextInt();
            System.out.println(" Reading method is : 1) from file , 2) from console  ");
            System.out.print("# choice >> ");
            int choiceReadeingMethod = in.nextInt();
            if (choiceOperation == 1) {
                String fileName = " ";
                if (choiceReadeingMethod == 1) {
                    System.out.print("Enter the file name : ");
                    fileName = str.nextLine();
                    fileName = checkFile(fileName,'c');
                }
                String fname = saveOutput();
                saveAsFileCompress(fname,readDataToCompress(fileName));
            } else if (choiceOperation == 2) {
                String fileName = " ";
                if (choiceReadeingMethod == 1) {
                    System.out.print("Enter the file name : ");
                    fileName = str.nextLine();
                    fileName = checkFile(fileName,'d');
                    //System.out.println(fileName+"  (9999");
                }
                String FName = in.nextLine();
                System.out.println(readDataToDecompress(fileName));
                //saveAsFileDecompress(FName,readDataToDecompress(fileName));
                System.out.println("Ppppppppppppppppppppp");
            } else if (choiceOperation == 3) {
                return;
            } else {
                System.out.println("Wrong input! \n choose 1 or 2 or 3 to exit");
            }
        }
    }


}
