package huffman;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;



/**
 *
 * @author Toka
 */
public class Huffman {
    
    public static HashMap<Character, Integer> char_freq = new HashMap<Character, Integer>();
    public static HashMap<Character, String> codes = new HashMap<Character, String>();
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
            else
                char_freq.put(a, 1);           
        }
        //printing elemets and thier frequencies
         System.out.println(Arrays.asList(char_freq));
         
        // adding elements to the priority queue for elements
        int n = 0;
        for (Character c: char_freq.keySet()){
        //c = char_freq.keySet();
            q.add(new Node(c, char_freq.get(c)));
        }    
        for(Node node : q)
        {
            System.out.println(node.getC() + "="+ node.getF());
        }
        Node root = buildHuffman(q);
        System.out.println("Root node = " + root.getF());
        System.out.println("Left node = " + root.getLeft().getF());
        System.out.println("Right node = " + root.getRight().getF());
        
        getCodes(root,"");
        compressedfile("test.txt");
    }
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
        
	//System.out.println("Contents of file:");
	//System.out.println(stringBuffer.toString());
        return stringBuffer;
    }
    public static Node buildHuffman(PriorityQueue<Node> q)
    {
        while(q.size()!=1)
        {
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
    public static void getCodes(Node root, String code){
        if(root == null)
        {
            return;
        }
        if(root.getLeft()==null&&root.getRight()==null)
        {
            System.out.println(root.getC() +"="+ code);
            codes.put(root.getC(),code);
        }
        getCodes(root.getLeft(),code+"0");
        getCodes(root.getRight(),code +"1");
    }
    public static void compressedfile (String filename) throws FileNotFoundException, IOException{
        File fout = new File("out");
       // byte[] array;
	FileOutputStream fos = new FileOutputStream(fout);
	//BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        //StringBuilder bw = new StringBuilder();
        String s = new String();
        StringBuffer stringBuffer = readFile(filename);
        for(Character c : stringBuffer.toString().toCharArray())
        {
             String code = codes.get(c);
            // System.out.println(code);
             s += code;
             //bw.append(code);
             if(s.length()%8 == 0)
             {       
                 byte[] arr  = new BigInteger(s,2).toByteArray();
                 fos.write(arr);
                 s="";
                 //bw.setLength(0);
              }
            //bw.close();
        }
    }
}    
