
import java.io.*;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.lang.reflect.Array;


/**
 * A Huffman Encoding algorithm: compresses and decompresses textfiles using binary trees, treemaps and priority queues
 *
 * @author Isuru Abeysekara
 *
 */
public class HuffmanNode {
    char ch; // Data element one for each node
    int freq; // Data element two for each node
    HuffmanNode left = null; //Left child
    HuffmanNode right = null; // Right child

    // Constructor for leaves

    public HuffmanNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;

    }

    // Constructor for inner nodes

    public HuffmanNode(char ch, int freq, HuffmanNode left, HuffmanNode right) {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;

    }

    // Get methods that return the character and frequency of a node

    public char getChar() {
        return ch;
    }

    public int getFreq() {
        return freq;
    }
    // Traverses tree and prints the elements

    public void traverse() {
        System.out.println(ch + freq);
        if (left != null) left.traverse();
        if (right != null) right.traverse();
    }



    /** Builds a tree based based on the Huffman algorithm
     *
     * @param Queue: The priority queue used to build the final tree
     */

    public static HuffmanNode buildTree(PriorityQueue<HuffmanNode> Queue) throws NullPointerException{
        PriorityQueue<HuffmanNode> q1= Queue;
        try{
            while (q1.size()!=1){
                HuffmanNode firstMin= q1.poll();
                HuffmanNode secondMin= q1.poll();
                int sum= firstMin.getFreq()+ secondMin.getFreq();
                HuffmanNode innerNode= new HuffmanNode('\0', sum, firstMin, secondMin);
                q1.add(innerNode);
            }
            System.out.println(q1);

        }
        catch(NullPointerException  n){
            System.out.println("The file is empty!");
        }
        return q1.peek();
    }


    /** Encodes characters in the tree
     *
     * @param root: The tree
     * @param str: Stores values of edges of the tree
     * @param code: A map that stores the Huffman codes for the each character
     */

    public static String encode(HuffmanNode root, String str, Map<Character, String> code){
        {   if (root==null) {return "";};
            if (root.left==null & root.right==null){
                code.put(root.ch, str);

            }
        }
        encode(root.left, str + '0', code);
        encode(root.right, str + '1', code);
        return str;
    }

    /** Gets frequency map
     *
     * @param filename1: The filename of text to be input
     */

    public static Map<Character, Integer> getFrequencyMap(String filename1) throws IOException{
        BufferedReader input = new BufferedReader(new FileReader(filename1));
        String str = "", line;
        Map<Character, Integer> charFreq= new TreeMap<Character, Integer>();
        try{
            while ((line = input.readLine()) != null) {
                str += line;

            }
            char[] ch = str.toCharArray();
            for (char c: ch){
                if(charFreq.containsKey(c)){
                    charFreq.put(c, charFreq.get(c)+1);
                }
                else{
                    charFreq.put(c, 1);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                input.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        System.out.println(charFreq);
        return charFreq;
    }

    /** Compresses file based on tree map
     *
     * @param fname: The filename of the text to be input
     * @param hcode: Code map
     */

    public static String compress(String fname, Map<Character, String> hcode) throws IOException{
        BufferedReader input = new BufferedReader(new FileReader(fname));
        String str = "", line;
        String s= "";
        try{
            if (input != null){
                while ((line = input.readLine()) != null) {
                    str += line;

                }
                char[] ch = str.toCharArray();
                for(int i=0; i<ch.length; i++){
                    s+= hcode.get(Array.getChar(ch, i));
                }
            }
            else {
                System.out.println("File is empty");
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                input.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        System.out.println(s);
        return s;
    }

    /** Writing into compressed file
     *
     * @param filename: The filename of the text to be input
     * @param pathname: The pathname of the compressed file created
     * @param codeMap: A map that stores the Huffman codes for the each character
     */

    public static void writeBitFile(String filename, String pathname, Map<Character, String> codeMap) throws Exception {
        BufferedBitWriter bitOutput = new BufferedBitWriter(pathname);
        BufferedReader input= new BufferedReader(new FileReader(filename));
        System.out.println(codeMap);
        try{
            int curr;
            char c;
            String code;

            while ((curr=input.read()) != -1) {
                c = (char) curr;
                code = codeMap.get(c);
                if (code!= null){
                    for (int i=0; i<code.length(); i++) {
                        if (code.charAt(i) == '0') bitOutput.writeBit(false);
                        else if (code.charAt(i) == '1') bitOutput.writeBit(true);
                        else System.out.println("Invalid bit!");
                        }
                }
                else {
                    String temp= codeMap.get(' ');
                    for (int i=0; i<temp.length(); i++) {
                        if (temp.charAt(i) == '0') bitOutput.writeBit(false);
                        else if (temp.charAt(i) == '1') bitOutput.writeBit(true);
                        else System.out.println("Invalid bit!");
                    }
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            input.close();
            bitOutput.close();

        }
    }

    /** Reading a compressed file and writing the decoded string into a file
     *
     * @param filename: The filename of the text to be input
     * @param pathname: The pathname of the compressed file created
     * @param codeMap: A map that stores the Huffman codes for the each character
     */

    public static void decompressBitfile(String pathname, String filename, Map<Character, String> codeMap) throws Exception{
        BufferedBitReader bitInput = new BufferedBitReader(filename);
        BufferedWriter output = new BufferedWriter(new FileWriter(pathname));
        try {
            int curr;
            boolean bit;
            String code= "";
            Map<String, Character> newCodeMap= new TreeMap<>();

            for (Map.Entry<Character, String> entry : codeMap.entrySet()){
                newCodeMap.put(entry.getValue(), entry.getKey());
            }
            if (bitInput != null){
                while (bitInput.hasNext()){
                    bit= bitInput.readBit();
                    if (bit) code += '1';
                    else code += '0';

                    if(newCodeMap.containsKey(code)){
                        curr= (int) newCodeMap.get(code);
                        System.out.println(code);
                        System.out.println((char) curr);
                        output.write(curr);
                        code= "";
                    }
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            bitInput.close();
            output.close();
        }
    }

    public static void main(String[] args) throws Exception {

        // boundary case 1: Hello (l repeats twice)

        String test1= "inputs/Hello.txt";

        Map<Character, Integer> charCounts= getFrequencyMap(test1);
        System.out.println(charCounts);

        //Building HuffmanTree
        TreeComparator freqComparator= new TreeComparator();
        PriorityQueue<HuffmanNode> huffmanQueue= new PriorityQueue<HuffmanNode>(freqComparator);
        for (Map.Entry<Character, Integer> entry : charCounts.entrySet()){
            huffmanQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        System.out.println(huffmanQueue);

        HuffmanNode root= buildTree(huffmanQueue);
        System.out.println(root);

        //Encoding
        String str1= "";
        Map<Character, String> codeMap= new TreeMap<>();
        encode(root, str1, codeMap);
        System.out.println(codeMap);

        //Compressing
        String compressedString= compress(test1, codeMap);
        System.out.println(compressedString);
        writeBitFile(test1, "inputs/Hello_compressed.txt", codeMap);

        // Reading in Compressed file and writes decoded string into file
        decompressBitfile("inputs/Hello_decompressed.txt", "inputs/Hello_compressed.txt", codeMap);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // boundary case 2: A single character
        String test2= "inputs/a.txt";

        Map<Character, Integer> charCounts2= getFrequencyMap(test2);
        System.out.println(charCounts2);

        //Building HuffmanTree
        TreeComparator freqComparator2= new TreeComparator();
        PriorityQueue<HuffmanNode> huffmanQueue2= new PriorityQueue<HuffmanNode>(freqComparator2);
        for (Map.Entry<Character, Integer> entry : charCounts2.entrySet()){
            huffmanQueue2.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        System.out.println(huffmanQueue2);

        HuffmanNode root2= buildTree(huffmanQueue2);
        System.out.println(root2);

        //Encoding
        String str2= "";
        Map<Character, String> codeMap2= new TreeMap<>();
        encode(root2, str2, codeMap2);
        System.out.println(codeMap2);

        //Compressing
        String compressedString2= compress(test2, codeMap2);
        System.out.println(compressedString2);
        writeBitFile(test2, "inputs/a_compressed.txt", codeMap2);

        // Reading in Compressed file and writes decoded string into file
        decompressBitfile("inputs/a_decompressed.txt", "inputs/a_compressed.txt", codeMap2);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // boundary case 3: An empty file
        String test3= "inputs/empty.txt";

        Map<Character, Integer> charCounts3= getFrequencyMap(test3);
        System.out.println(charCounts3);

        //Building HuffmanTree
        TreeComparator freqComparator3= new TreeComparator();
        PriorityQueue<HuffmanNode> huffmanQueue3= new PriorityQueue<HuffmanNode>(freqComparator3);
        for (Map.Entry<Character, Integer> entry : charCounts3.entrySet()){
            huffmanQueue3.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        System.out.println(huffmanQueue3);

        HuffmanNode root3= buildTree(huffmanQueue3);
        System.out.println(root3);

        //Encoding
        String str3= "";
        Map<Character, String> codeMap3= new TreeMap<>();
        encode(root3, str3, codeMap3);
        System.out.println(codeMap3);

        //Compressing
        String compressedString3= compress(test3, codeMap3);
        System.out.println(compressedString3);
        writeBitFile(test3, "inputs/empty_compressed.txt", codeMap3);

        // Reading in Compressed file and writes decoded string into file
        decompressBitfile("inputs/empty_decompressed.txt", "inputs/empty_compressed.txt", codeMap3);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        String test4= "inputs/USConstitution.txt";

        Map<Character, Integer> charCounts4= getFrequencyMap(test4);

        //Building HuffmanTree
        TreeComparator freqComparator4= new TreeComparator();
        PriorityQueue<HuffmanNode> huffmanQueue4= new PriorityQueue<HuffmanNode>(freqComparator4);
        for (Map.Entry<Character, Integer> entry : charCounts4.entrySet()){
            huffmanQueue4.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        System.out.println(huffmanQueue4);

        HuffmanNode root4= buildTree(huffmanQueue4);
        System.out.println(root4);

        //Encoding
        String str4= "";
        Map<Character, String> codeMap4= new TreeMap<>();
        encode(root4, str4, codeMap4);
        System.out.println(codeMap4);

        //Compressing
        String compressedString4= compress(test4, codeMap4);
        System.out.println(compressedString4);
        writeBitFile(test4, "inputs/USConstitution_compressed.txt", codeMap4);

        // Reading in Compressed file and writes decoded string into file
        decompressBitfile("inputs/USConstitution_decompressed.txt", "inputs/USConstitution_compressed.txt", codeMap4);


    }

}