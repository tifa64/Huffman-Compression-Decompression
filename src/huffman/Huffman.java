package huffman;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import static java.util.Arrays.stream;
import java.util.HashMap;
import java.util.PriorityQueue;



/**
 *
 * @author Toka
 */
public class Huffman {
    
    //To save characters and their number of appearence
    public static HashMap<Character, Integer> char_freq = new HashMap<Character, Integer>();
    //to save code of each character
    public static HashMap<Character, String> codes = new HashMap<Character, String>();
    // priority queue to store elements in ascending order accorging to their frequency to build huffman tree later
    public static PriorityQueue<Node> q = new PriorityQueue<Node>(new comparator());
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Reading File content
        StringBuffer stringBuffer = readFile("test.txt");
        
        //Assign frequency to each character
        for (int i=0;i<stringBuffer.length();i++)
        {
            char a = stringBuffer.charAt(i);
            if(char_freq.containsKey(a))
                char_freq.put(a, char_freq.get(a)+1);
            // first appearence of the character
            else
                char_freq.put(a, 1);           
        }
        //printing elemets and thier frequencies
         System.out.println(Arrays.asList(char_freq));
         
        // adding elements to the priority queue for elements
        for (Character c: char_freq.keySet()){
            q.add(new Node(c, char_freq.get(c)));
        } 
        // passing priority queue to buildHuffman function 
        Node root = buildHuffman(q);
        // assign codes after building the tree
        getCodes(root,"");
        //building compressed file
        compressedfile("test.txt");
        decompression();
    }
    // reading file 
    public static StringBuffer readFile(String filename) throws FileNotFoundException, IOException
    {
        File file = new File(filename);
	FileReader fileReader = new FileReader(file);
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	StringBuffer stringBuffer = new StringBuffer();
	String line;
	while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append("\n");
	}
	fileReader.close();
        return stringBuffer;
    }
    // build huffman tree
    public static Node buildHuffman(PriorityQueue<Node> q)
    {
        while(q.size()!=1)
        {
        // polling the two nodes with least frequency , adding them together then put their sum in a new node 
        // do this untill there is only one node in the queue "root node" to traverse the tree 
        Node leftNode = q.poll();
        Node rightNode = q.poll();
        Node sumNode = new Node();
        sumNode.setLeft(leftNode);
        sumNode.setRight(rightNode);
        sumNode.setF(leftNode.getF()+rightNode.getF());
        q.add(sumNode);
        }
        
        Node root = q.poll();   
        return root;
}
    // traversing the tree and assign code to each character
    public static void getCodes(Node root, String code){
        if(root == null)
        {
            return;
        }
        if(root.getLeft()==null&&root.getRight()==null)
        {
            codes.put(root.getC(),code);
        }
        getCodes(root.getLeft(),code+"0");
        getCodes(root.getRight(),code +"1");
    }
    // writing to the binary file 
    // first write code size in 4 bytes
    // map size in 4 bytes
    // each character in 4 bytes ???? kan z3lan lma b3ml allocate le 1 byte
    
    public static void compressedfile (String filename) throws FileNotFoundException, IOException{ 
        File fout = new File("out");
	FileOutputStream fos = new FileOutputStream(fout);
        StringBuffer stringBuffer = readFile(filename);
        //getting code size to store in in the header of the file
        int codeSize =0;
        int mapSize =0;
        for (Character c: char_freq.keySet()){
            System.out.println("key "+ c +" value " + char_freq.get(c) );
            codeSize += char_freq.get(c);
        }
        System.out.println("code size "+ codeSize );
        //allocating the first 4 bytes in the file to the file size
        byte[] sizeofcode = ByteBuffer.allocate(4).putInt(codeSize).array();
        fos.write(sizeofcode);
        //calculating map size
        int mapsize=0;
        for (Character c: codes.keySet()){
            System.out.println("key "+ c +" code " + codes.get(c) );
            // 1 byte for the character and 4 bits for number of byts of the code
            //String a =codes.get(c);
            //int x= (int) Math.ceil(a.length()/8.0) ;
           // mapsize=mapsize+ 5+ x;
             mapsize+=12;
        }
        System.out.println("map size "+ mapsize );
        byte[] sizeofmap = ByteBuffer.allocate(4).putInt(mapsize).array();
        fos.write(sizeofmap);       
        for (Character c: codes.keySet()){
            System.out.println("key "+ c +" code " + codes.get(c) );
            //putting character 
           // String k = c.toString();
            //byte[] w = k.getBytes();
            byte[] character = ByteBuffer.allocate(4).putInt(c).array(); 
            fos.write(character);
            // number of bits
            byte[] numofbits = ByteBuffer.allocate(4).putInt(codes.get(c).length()).array();
            fos.write(numofbits);          
            //String a =codes.get(c);
            //int x= (int) Math.ceil(a.length()/8.0) ;           
            byte[] bits = ByteBuffer.allocate(4).putInt(Integer.parseInt(codes.get(c))).array();
            fos.write(bits);
        } 
        String reminder ="";      
        for(Character c : stringBuffer.toString().toCharArray())
        {
             String code = codes.get(c);
             // string of codes this may be greater that 8 in lenght
             String s = reminder + code;
             
             int len = s.length();
             int max = len-len%8;
             //System.out.println("len "+len);          
             if (len<8){
                 reminder = s;
             }else{
                 int start =0;
                 int end=8;
                 while (end<=max){
                    byte binary = (byte)Integer.parseInt(s.substring(start, end),2);
                    start=end;
                    end+=8;
                    fos.write(binary);                     
                 }
                 reminder = s.substring(max,len);               
             }
        }
        byte binary = (byte)Integer.parseInt(reminder,2);
        fos.write(binary); 
        System.out.println("++++++++reminder " + reminder);
    }
    public static void decompression () throws FileNotFoundException, IOException{
        File file = new File("out");
        FileInputStream stream =null;
        stream = new FileInputStream(file);
        byte fileContent[] = new byte[(int) file.length()];
        stream.read(fileContent);
        stream.close();
        
     /* File file = new File(inputFile);
        byte[] buffer = new byte[4];
        InputStream is = new FileInputStream(filename);
        System.out.println(buffer);
        if (is.read(buffer) != buffer.length) { 
            // do something 
            int i = buffer[0];
            System.out.println(" i am here");
            System.out.println("i = " + i);
        }else {
            System.out.println("sssss i am here");
        }
        is.close();
            }*/   
    }
       
}    
