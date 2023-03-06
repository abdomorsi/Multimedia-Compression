import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VectorQuantization {
    private static BufferedImage image = null,outImage=null;
    private static int height=6 , width=6;
    private static int[] vectorSize = {4,4};
    private static int numOfVectsInCodeBook = 16;
    private static int numOfBlocks=0;
    private static int blockSize = vectorSize[0]*vectorSize[1];
    private static HashMap<Block,ArrayList<Block>> nearestVectors = new HashMap<>();
    private static ArrayList<Block> codeBook = new ArrayList<>();
    private static ArrayList<Integer> outArray = new ArrayList<>();
    public static ArrayList<Integer> encodedArray = new ArrayList<>();
    public static ArrayList<Block> encodedBlocks = new ArrayList<>();
    public static ArrayList<Block> originalBlocks = new ArrayList<>();
    /*private static int pixels [][] = new int[][]{ { 1, 2, 7, 9, 4,11},
                                                  { 3, 4, 6, 6,12,12},
                                                  { 4, 9,15,14, 9, 9},
                                                  {10,10,20,18, 8, 8},
                                                  { 4, 3,17,16, 1, 4},
                                                  { 4, 5,18,18, 5, 6}};*/
    private static double pixels [][];
    private static double dpixels [][];
    private static int imageInArray [][];


    private static void getImage(){
        try {
            //how to read image to 2-d array
            image = ImageIO.read(new File("cat1.jpg"));
            //outImage = ImageIO.read(new File("pic(1).jpg"));
            height = image.getHeight();
            width = image.getWidth();
            //int imageWidth = image.getWidth();
            //int imageHeight = image.getHeight();
            //System.out.println(imageHeight);
            pixels = new double[height][height];
            //dpixels = new double[imageWidth][imageHeight];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int rgbPixel = image.getRGB(i,j);
                    int alphaRGB = (rgbPixel>>24) &  0xff;
                    int redRGB = (rgbPixel>>16) &  0xff;
                    int greenRGB = (rgbPixel>>8) &  0xff;
                    int blueRGB = (rgbPixel>>0) &  0xff;
                    pixels[i][j] =  (greenRGB+redRGB+blueRGB)/3;
                    //dpixels[i][j] = image.getRGB(i,j);
                    //System.out.println(pixels[i][j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void divideIntoBlocks() {
        int c = blockSize;
        numOfBlocks = (width*height)/(blockSize);
        System.out.println(" original blocks ");
        for (int i = 0; i < height; i+=vectorSize[0]) {
            for (int j = 0; j < width; j+=vectorSize[1]) {
                Block b = new Block(vectorSize[0],vectorSize[1]);
                //System.out.println(b.rows+" "+b.columns);
                //b.fillBlock(b.rows,b.columns);
                for (int x = i , bi = 0 ; x < i+b.rows; x++ , bi++) {
                    //bi++;
                    for (int y = j , bj=0; y < j+b.columns; y++ , bj++) {
                        //System.out.println(arr[i][j]);
                        b.array[bi][bj] = pixels[x][y];
                    }
                }
                originalBlocks.add(b);
                b.printBlock();
                System.out.println();
            }
        }
        System.out.println(" original blocks ");
        //System.out.println(result.size());
        //return result;
    }

    private static Block getAverage(ArrayList<Block> blockss){
        //System.out.println(blockss.size());
        Block b = new Block(vectorSize[0],vectorSize[1]);
        double sum = 0 ;
        //int n = blockss.size();
        //int blocksIndex = 0;
        for (int i = 0; i < b.rows; i++)
            for (int j = 0; j < b.columns; j++) {
                for (Block bb : blockss) {
                    sum += (bb.array[i][j]);
                }
                //System.out.println(sum + " /*/*/*/*");
                b.array[i][j] = (int) (sum / blockss.size());
                sum = 0;
            }
        //b.printBlock();
        return b;
    }


    private static int getDifference (Block b1 , Block b2) {
        int result = 0;
        for (int i = 0; i < b1.rows; i++) {
            for (int j = 0; j < b2.columns; j++) {
                result += (int) Math.abs(b1.array[i][j]-b2.array[i][j]);
            }
        }
        return result;
    }

    private static void applySplitting() {
        ArrayList<Block> splittedBlocks = new ArrayList<>();
        for (int c = 0; c < codeBook.size(); c++) {
           // System.out.println("avgBlocks size= "+ avgBlocks.size() + " ---------------- ");
            Block b1 = new Block(codeBook.get(c).rows,codeBook.get(c).columns);
            Block b2 = new Block(codeBook.get(c).rows,codeBook.get(c).columns);
            for (int i = 0; i < b1.rows; i++) {
                for (int j = 0; j < b1.columns; j++) {
                    /*if (avgBlocks.get(c).array[i][j] == 0) {
                        System.out.println("Stop");
                        return splittedBlocks;
                    }*/
                    b1.array[i][j] = Math.ceil(codeBook.get(c).array[i][j]-1);
                    b2.array[i][j] = Math.floor(codeBook.get(c).array[i][j]+1);
                }
            }
            splittedBlocks.add(b1);
            splittedBlocks.add(b2);
        }
        /*System.out.println("-----  splitted Blocks ------ ");
        for (Block b : splittedBlocks) {
            b.printBlock();
            System.out.println("-----");
        }
        System.out.println("------------------------------ ");
        */
        codeBook = splittedBlocks;
    }

    private static HashMap<Block,ArrayList<Block>> assignNearestVectorss(ArrayList<Block> splittedVectors,ArrayList<Block> originalVectors ) {
        HashMap<Block,ArrayList<Block>> map = new HashMap<>();
        int minDiff = (int) 1e9;
        for (int i = 0; i < splittedVectors.size(); i++) {
            map.put(splittedVectors.get(i),new ArrayList<>());
        }

        for (int i = 0; i < originalVectors.size(); i++) {
            minDiff = (int) 1e9;
            Block tembBlock = new Block(vectorSize[0],vectorSize[1]);
            int index = 0;
            for (int j = 0; j < splittedVectors.size(); j++) {
                int d = getDifference(originalVectors.get(i), splittedVectors.get(j));
                if (minDiff > d) {
                    minDiff=getDifference(originalVectors.get(i), splittedVectors.get(j));
                    index = j;
                    tembBlock.fillBlock(splittedVectors.get(j).array);
                }
            }
            map.get(splittedVectors.get(index)).add(originalVectors.get(i));
        }
        //nearestVectors.clear();
        nearestVectors = map;
        return map;
    }



    public static void run(){
        //ArrayList<Block> originalBlocks = divideIntoBlocks();
        ArrayList<Block> tempCodeBook = new ArrayList<>();
        HashMap<Block,ArrayList<Block>> tempMap = new HashMap<>();
        Block avgBlock = getAverage(originalBlocks);
        codeBook.add(avgBlock);
        for (;codeBook.size() < numOfVectsInCodeBook;) {
            applySplitting();
            tempMap = assignNearestVectorss(codeBook,originalBlocks);
            //System.out.println(codeBook.size());
            for (int i =0 ;i<codeBook.size();i++) {
                //System.out.println("AAA"+tempMap.get(codeBook.get(i)).size());
                codeBook.set(i,getAverage(tempMap.get(codeBook.get(i))));
            }
        }

        boolean rebuild = true;
        tempCodeBook = codeBook;
        for (;;) {
            tempMap = assignNearestVectorss(codeBook,originalBlocks);
            for (int i =0 ;i<codeBook.size();i++) {
                //System.out.println("AAA"+tempMap.get(i).size());;
                codeBook.set(i,getAverage(tempMap.get(codeBook.get(i))));
            }
            if (tempCodeBook.equals(codeBook)) {
                break;
            }
        }
    }

    public static void showCodeBlock() {
        System.out.println("=========================");
        for (int i =0 ;i<codeBook.size();i++) {
            codeBook.get(i).printBlock();
            //System.out.println(nearestVectors.get(codeBook.get(i)).size());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
        System.out.println("=========================");
    }
    public static void showNearestVectors() {
        for (Block b : nearestVectors.keySet()) {
            b.printBlock();
            System.out.println();
            System.out.println("^^^^^^^^^^^^^^^^^^^");
            for (Block k : nearestVectors.get(b)) {
                k.printBlock();
                System.out.println("---------");
            }
            System.out.println();
        }
    }
    public static void showEncodeArr() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(dpixels[i][j] + " ");
            }
            System.out.println();
        }
    }
    private static void encoding() throws IOException {
        divideIntoBlocks();
        run();
        for (Block b1: originalBlocks) {
            int i = 0 , index=0;
            int diff = (int) 1e9;
            for (Block b2 : codeBook) {
                if (diff > getDifference(b2, b1)) {
                    diff = getDifference(b2,b1);
                    index = i;
                }
                i++;
            }
            encodedArray.add(index);
            /*Block newB = new Block(codeBook.get(index).rows,codeBook.get(index).columns);
            for (int x = 0; x < codeBook.get(index).rows; x++) {
                for (int y = 0; y < codeBook.get(index).columns; y++) {
                    newB.array[x][y] = codeBook.get(index).array[x][y];
                }
            }
            encodedBlocks.add(newB);*/
        }
        WriteEncodedArrayToFile();
        //System.out.println("A");
        //getEncodedArray();
    }

    private static void WriteEncodedArrayToFile() throws IOException {
        ArrayList<String> content = new ArrayList<>();
        content.add(String.valueOf(height));
        content.add(String.valueOf(width));
        content.add(String.valueOf(vectorSize[0]));
        content.add(String.valueOf(vectorSize[1]));
        content.add(String.valueOf(codeBook.size()));
        //System.out.println(originalBlocks.size()+" >>>>");
        content.add(String.valueOf(originalBlocks.size()));
        for (Block b : codeBook) {
            for (int i = 0; i < b.rows; i++) {
                for (int j = 0; j < b.columns; j++) {
                    //wr.write((int) b.array[i][j]);
                    content.add(String.valueOf(b.array[i][j]));
                }
            }
        }
        for (int indx : encodedArray) {
            //wr.write(indx);
            content.add(String.valueOf(indx));
        }
        writeInFile(content,"file.txt");
    }
    private static void readEncodedInfoFromFile() throws IOException {
        ArrayList<String> content = readLineByLine("file.txt");
        int cbSize = 0 , obSize=0;
        int Index = -1;
        height = Integer.parseInt(content.get(++Index));
        width = Integer.parseInt(content.get(++Index));
        vectorSize[0] =Integer.parseInt(content.get(++Index));
        vectorSize[1] = Integer.parseInt(content.get(++Index));
        cbSize = Integer.parseInt(content.get(++Index));
        obSize = Integer.parseInt(content.get(++Index));
        Block b = new Block(vectorSize[0],vectorSize[1]);
        for (int indx =0;indx<cbSize;indx++) {
            for (int i = 0; i < b.rows; i++) {
                for (int j = 0; j < b.columns; j++) {
                    //System.out.println(">>" + vectorSize[0] + vectorSize[1]);
                    b.array[i][j] = Double.parseDouble(content.get(++Index));
                    codeBook.add(b);
                }
            }
        }
        for (int i=0;i<obSize;i++) {
            encodedArray.add(Integer.parseInt(content.get(++Index)));
            int index = encodedArray.get(i);
            Block newB = new Block(vectorSize[0],vectorSize[1]);
            for (int x = 0; x < codeBook.get(index).rows; x++) {
                for (int y = 0; y < codeBook.get(index).columns; y++) {
                    newB.array[x][y] = codeBook.get(index).array[x][y];
                }
            }
            System.out.println("BB"+ encodedBlocks.size());
            encodedBlocks.add(newB);
        }
        //int index = 0;

    }

    private static void decoding(){
        try {
            readEncodedInfoFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int indx = 0;
        dpixels = new double[height][width];
        for (int i = 0; i < height; i+=vectorSize[0]) {
            for (int j = 0; j < width; j+=vectorSize[1]) {
                System.out.println(indx+" <><><>");
                Block b = encodedBlocks.get(indx);
                for (int x = i , bi = 0 ; x < i+b.rows; x++ , bi++) {
                    //bi++;
                    for (int y = j , bj=0; y < j+b.columns; y++ , bj++) {
                        dpixels[x][y] = b.array[bi][bj];
                        //wr.write((int)b.array[bi][bj]);
                    }
                }
                indx++;
                //if (numOfBlocks>indx)indx++;
                System.out.println();
            }
        }
        writeImage();
    }

    private static void writeImage() {
        //imgInArray();
        String path = "E:\\A .. Morsi\\FCAI\\level 3\\1st\\MM\\VectorQuantizationAssignment\\pic(1).jpg";
        BufferedImage image2 = new BufferedImage(height,width, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int pxl = (int) dpixels[x][y];
                image2.setRGB(x, y,(pxl<<16)|(pxl<<8)| pxl);
            }
        }
        outImage = image2;
        File ImageFile = new File(path);
        try {
            ImageIO.write(outImage, "jpg", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> readLineByLine(String fileName) throws IOException {
        List<String> list1 = Files.readAllLines(Paths.get(fileName));
        ArrayList<String> content = new ArrayList<String>();
        for (String s : list1) {
            content.add(s);
        }
        return content;
    }


    private static void writeInFile(ArrayList<String> content, String fileName) throws IOException {
        BufferedWriter BW = new BufferedWriter(new FileWriter(fileName, true));
        for (String s : content) {
            BW.write(s);
            System.out.print(s);
            BW.newLine();
            // System.out.print("\n");
            // Files.write(Paths.get(fileName), s.getBytes(),StandardOpenOption.APPEND);
        }
        BW.close();
    }

    public static  void  main(String[] args) throws IOException {
       // File f = new File("elst.jpg");
       // getImage(f);
       //ArrayList<Block> bb = divideIntoBlocks();
       //Block b = getAverage(bb);
        getImage();
        //divideIntoBlocks();
        //run();
        encoding();
        //showCodeBlock();
        //getEncodedArray();
        //showEncodeArr();
        decoding();
       // imgInArray();
        //writeImage();
    }
}
