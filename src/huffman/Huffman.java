package huffman;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.*;

import static java.util.Arrays.setAll;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;


/**
 *
 * @author Toka
 */
public class Huffman {
    
    //To save characters and their number of appearence
    public static HashMap<Character, Integer> char_freq = new HashMap<Character, Integer>();
    public static HashMap<Byte, Integer> byte_freq = new HashMap<Byte, Integer>();
    public static HashMap<Byte, String> bcodes = new HashMap<Byte, String>();
    public static HashMap<String, Character> codeDecompression = new HashMap<String, Character>();
    public static HashMap<String, Byte> bcodeDecompression = new HashMap<String, Byte>();
    public static HashMap<String,ArrayList<String>> filenames = new HashMap<>();

    public static HashMap<Character, String> codes = new HashMap<Character, String>();
    // priority queue to store elements in ascending order accorging to their frequency to build huffman tree later
    public static PriorityQueue<Node> q = new PriorityQueue<Node>(new comparator());
    
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\t\t\t\t Huffman Compression/Decompression\t\n");
        System.out.println("Enter file name followed by -c for compression or -d for decompression followed by -t for text files, -b for binary files or -dir for folders");
        while(true) {
            clearMaps();
            System.out.print(">");
            String cmd = scanner.nextLine();
            String cmdarr[] = cmd.split(" ");
            String fname = null, arg1 = null, arg2 = null;
            if (cmd.equals("exit")) {
                System.exit(0);
            }
            if (cmdarr.length != 3) {

                System.out.println("Invalid Arguments");
                System.out.println("Enter file name followed by -c for compression or -d for decompression followed by -t for text or -b for binary");
                continue;
            } else {
                fname = cmdarr[0];
                arg1 = cmdarr[1];
                arg2 = cmdarr[2];
            }
            if (arg1.equals("-d")) {
                String namearr[] = fname.split("\\.");
                if (!namearr[namearr.length - 1].equals("cmp")) {
                    System.out.println("Invalid file type!");
                    System.out.println("File type must be .cmp to decompress");
                    continue;
                }
            }
            try {
                if (arg2.equals("-t")) {
                    if (arg1.equals("-c")) {
                        String namearr[] = fname.split("\\.");
                        if (!namearr[namearr.length - 1].equals("txt")) {
                            System.out.println("Please enter a .txt file!");
                            continue;
                        }
                        System.out.println("Currently compressing text file: " + fname);
                        if (compressedfile(fname)) {
                            System.out.println("File compressed successfully");
                            continue;
                        } else {
                            System.out.println("File can't be compressed");
                            continue;
                        }
                    }
                    if (arg1.equals("-d")) {
                        System.out.println("Currently decompressing text file: " + fname);

                        if (decompression(fname, fname.split("\\.")[0] + "-decomp.txt")) {
                            System.out.println("File decompressed successfully");
                            continue;
                        } else {
                            System.out.println("File can't be decompressed");
                            continue;
                        }
                    } else {
                        System.out.println("Invalid Argument");
                        continue;
                    }

                } else if (arg2.equals("-b")) {
                    if (arg1.equals("-c")) {
                        System.out.println("Currently compressing binary file: " + fname);
                        if (compressBinary(fname)) {
                            System.out.println("File compressed successfully");
                            continue;
                        } else {
                            System.out.println("File can't be compressed");
                            continue;
                        }
                    } else if (arg1.equals("-d")) {
                        System.out.println("Currently decompressing binary file: " + fname);
                        String namearr[] = fname.split("\\.");
                        String ext = namearr[1];
                        String name = namearr[0];
                        if (decompressBinary(fname, name + "-decomp." + ext)) {
                            System.out.println("File decompressed successfully");
                            continue;
                        }
                        else{
                            System.out.println("File can't be decompressed");
                            continue;
                        }
                    } else {
                        System.out.println("Invalid Argument");
                        continue;
                    }

                }
                else if(arg2.equals("-dir"))
                {
                        if(arg1.equals("-c")) {
                            if(compressFolder(fname, filenames))
                            {
                                System.out.println("Folder compressed successfully");
                                continue;
                            }
                            else{
                                System.out.println("Cannot compress folder");
                                continue;
                            }
                        }
                        if(arg1.equals("-d")) {
                            if(decompressFolder(fname, filenames)) {
                                System.out.println("Folder decompressed successfully");
                            }
                            else
                            {
                                System.out.println("Folder decompress failed");
                            }
                            continue;
                        }
                }
                else {
                    System.out.println("Invalid Argument");
                    continue;
                }

            }catch (IOException e)
            {
                System.out.println("File not found");
                continue;
            }
            catch (ArrayIndexOutOfBoundsException ae)
            {
                System.out.println("Decompression may have failed, exit and try again.");
                continue;
            }
            catch (NullPointerException ne){
                System.out.println("Cannot compress an empty file.");
                continue;
            }
        }

    }
    public static void clearMaps()
    {
        q.clear();
        char_freq.clear();
        byte_freq.clear();
        bcodes.clear();
        codes.clear();
        bcodeDecompression.clear();
        codeDecompression.clear();
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
        // do this until there is only one node in the queue "root node" to traverse the tree
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
            bcodes.put(root.getB(),code);
        }
        getCodes(root.getLeft(),code+"0");
        getCodes(root.getRight(),code +"1");
    }
    // writing to the binary file 
    // first write code size in 4 bytes
    // map size in 4 bytes
    // each character in 4 bytes ???? kan z3lan lma b3ml allocate le 1 byte
    
    public static boolean compressedfile (String filename) throws  IOException{
        // Reading File content
        StringBuffer stringBuffer = readFile(filename);
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
//        System.out.println(Arrays.asList(char_freq));

        // adding elements to the priority queue for elements
        for (Character c: char_freq.keySet()){
            q.add(new Node(c, char_freq.get(c)));
        }
        // passing priority queue to buildHuffman function
        Node root = buildHuffman(q);
        // assign codes after building the tree
        getCodes(root,"");
        File fout = new File(filename.toString()+".cmp");
        FileOutputStream fos = new FileOutputStream(fout);
        //getting code size to store in in the header of the file
        int codeSize =0;
        int mapSize =0;
        for (Character c: codes.keySet()){

            codeSize += codes.get(c).length() * char_freq.get(c);
        }
//        System.out.println("code size "+ codeSize );
        int codesbyte =(int) Math.ceil((double) codeSize/8) ;
//        System.out.println("code size in bytes" + codesbyte );
        //allocating the first 4 bytes in the file to the file size
        byte[] sizeofcode = ByteBuffer.allocate(4).putInt(codesbyte).array();
        fos.write(sizeofcode);
        // map size

        byte[] sizeofmap = ByteBuffer.allocate(4).putInt(codes.size()).array();
        fos.write(sizeofmap);
        for (Character c: codes.keySet()){
//            System.out.println("key "+ c +" code " + codes.get(c) );
            //putting character
            String k = c.toString();
            byte[] w = k.getBytes();
            // byte[] character = ByteBuffer.allocate(2).putChar(c).array();
            fos.write(w);
            // number of bits
            byte[] numofbits = ByteBuffer.allocate(4).putInt(codes.get(c).length()).array();
            fos.write(numofbits);
            // putting ASQUII code ex 1 -> 110001
            byte[] bits = codes.get(c).getBytes();
            fos.write(bits);

        }
        String remainder ="";
        for(Character c : stringBuffer.toString().toCharArray())
        {
            String code = codes.get(c);
            // string of codes this may be greater that 8 in lenght
            String s = remainder + code;

            int len = s.length();
            int max = len-len%8;
            //System.out.println("len "+len);
            if (len<8){
                remainder = s;
            }else{
                int start =0;
                int end=8;
                while (end<=max){
                    byte binary = (byte)Integer.parseInt(s.substring(start, end),2);
                    start=end;
                    end+=8;
                    fos.write(binary);
                }
                remainder = s.substring(max,len);
            }
        }

        // زعل هنا فشلت اخر كام حرف
        if(!remainder.equals("")) {
            byte binary = (byte) Integer.parseInt(remainder, 2);
            fos.write(binary);
        }
        if((int)fout.length()> stringBuffer.length())
        {
            fout.delete();
            return false;
        }
        fos.close();
        return true;

    }

    public static byte[] readBinary(String filename) throws IOException {
        InputStream inputStream = new FileInputStream(filename);
        long fileSize = new File(filename).length();
        byte[] allBytes = new byte[(int) fileSize];

            inputStream.read(allBytes);

    return  allBytes;

    }

    public static boolean compressBinary(String filename) throws IOException {
        q.clear();
        File fout = new File(filename.toString()+ ".cmp");
        FileOutputStream fos = new FileOutputStream(fout);
        byte[] fileBytes = readBinary(filename);

        for (Byte b : fileBytes) {
                if (byte_freq.containsKey(b))
                    byte_freq.put(b, byte_freq.get(b) + 1);
                else
                    byte_freq.put(b, 1);

        }

        for (Byte b : byte_freq.keySet()) {
            q.add(new Node(b, byte_freq.get(b)));
        }

        Node root = buildHuffman(q);
        getCodes(root, "");

        //getting code size to store in in the header of the file
        int codeSize =0;
        int mapSize =0;

        for (Byte b: bcodes.keySet()){
            try {
                codeSize += bcodes.get(b).length() * byte_freq.get(b);
            }
            catch (Exception e)
            {
                System.out.println(e.getCause());
            }
        }
//        System.out.println("code size "+ codeSize );
        int codesbyte =(int) Math.ceil((double) codeSize/8) ;
//        System.out.println("code size in bytes" + codesbyte );
        //allocating the first 4 bytes in the file to the file size
        byte[] sizeofcode = ByteBuffer.allocate(4).putInt(codesbyte).array();
        fos.write(sizeofcode);
        // map size

        byte[] sizeofmap = ByteBuffer.allocate(4).putInt(bcodes.size()).array();
        fos.write(sizeofmap);
        for (Byte b: bcodes.keySet()){
//            System.out.println("key "+ b +" code " + bcodes.get(b) );
            //putting character
            // byte[] character = ByteBuffer.allocate(2).putChar(c).array();
            fos.write(b);
            donothing();
            // number of bits
            byte[] numofbits = ByteBuffer.allocate(4).putInt(bcodes.get(b).length()).array();
            fos.write(numofbits);
            // putting ASQUII code ex 1 -> 110001
            byte[] bits = bcodes.get(b).getBytes();
            fos.write(bits);
        }

        String remainder = "";
    try {

        for (Byte b : fileBytes) {
            String code = bcodes.get(b);
            String s = remainder + code;
            int len = s.length();
            int max = len - len % 8;
            if (len < 8) {
                remainder = s;
                donothing();
            } else {
                int start = 0;
                int end = 8;
                while (end <= max) {
                    byte binary = (byte) Integer.parseInt(s.substring(start, end), 2);
                    start = end;
                    end += 8;
                    fos.write(binary);
                }
                remainder = s.substring(max, len);
            }
        }
        if (!remainder.equals("")) {
            byte binary = (byte) Integer.parseInt(remainder, 2);
            fos.write(binary);
        }
    }
    catch (Exception e)
    {
        System.out.println(e.getCause());
    }
            if(fout.length()>fileBytes.length)
            {
                fout.delete();
                return false;
            }
            fos.close();
            return true;


    }
    public static boolean decompression (String filename ,String dfname) throws FileNotFoundException, IOException{
        File file = new File(filename);
        FileInputStream stream =null;
        stream = new FileInputStream(file);
        byte fileContent[] = new byte[(int) file.length()];
        stream.read(fileContent);
        stream.close();
        int i=0,index=0,codesize,mapsize;
        String s= "";
        char c;
        // first 4 bytes as code size
        for (i=0;i<4;i++){
            s += String.format("%02x", fileContent[i]);
        }
        codesize = Integer.parseInt(s, 16);
//        System.out.println(codesize);
        s="";
        // map size 2nd four bytes
        for (i=4;i<8;i++){
            s += String.format("%02x", fileContent[i]);
        }
        mapsize= Integer.parseInt(s, 16);
//        System.out.println(mapsize);
        index = i;
        s="";
        // looping throught the map to extract characters and their code
        for (int j=0;j<mapsize;j++){
            int size =0;
            //First byte for character
            c = (char) fileContent[index];
            index++;
            // size of huffman code of each character
            for (i = index; i < index + 4; i++) {
                s += String.format("%02x", fileContent[i]);
            }
            size = Integer.parseInt(s, 16);
//            System.out.println("size" + size);
            s = "";
            index = i;
            String code = new String();
            //huffman code
            for (i = index; i < index + size; i++) {
                code += (char) fileContent[i];
            }
//            System.out.println("char "+c+ " code "+code);
            codeDecompression.put(code, c);
            index = i;
        }
        s = "";
        s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
//        System.out.println("==s " +s);
        s = "";
        String token = "";

        FileWriter decompressedFile = new FileWriter(dfname) ;
        BufferedWriter writer =  new BufferedWriter(decompressedFile);
        int remainder = 0;
        for (i=index ;i<codesize+index-1;i++){
            s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
            for (int k=0;k<s.length() ;k++){
                token += s.charAt(k);
                //System.out.println("token"+token);
                if(codeDecompression.containsKey(token))
                {
                    //System.out.println("here "+codeDecompression.get(token));
                    writer.write(codeDecompression.get(token));
                    remainder = k;
                    token ="";
                }
            }
            if (remainder != 0) {
                s = s.substring(remainder + 1, s.length());
                remainder = 0;
            }
            token = "";
        }
        writer.close();
        return  true;
    }
    public static boolean decompressBinary(String filename, String dfname) throws IOException, ArrayIndexOutOfBoundsException
    {
        File file = new File(filename);
        FileInputStream stream =null;
        stream = new FileInputStream(file);
        byte fileContent[] = new byte[(int) file.length()];
        stream.read(fileContent);
        stream.close();
        int i=0,index=0,codesize,mapsize;
        String s= "";
        byte b;
        // first 4 bytes as code size
        for (i=0;i<4;i++){
            s += String.format("%02x", fileContent[i]);
        }
        codesize = Integer.parseInt(s, 16);
//        System.out.println(codesize);
        s="";
        // map size 2nd four bytes
        for (i=4;i<8;i++){
            s += String.format("%02x", fileContent[i]);
        }
        donothing();
        mapsize= Integer.parseInt(s, 16);
//        System.out.println(mapsize);
        index = i;
        s="";
        // looping throught the map to extract characters and their code
        for (int j=0;j<mapsize;j++){
            int size =0;
            //First byte for character
            b =  fileContent[index];
            index++;
            // size of huffman code of each character
            for (i = index; i < index + 4; i++) {
                s += String.format("%02x", fileContent[i]);
            }
            size = Integer.parseInt(s, 16);
//            System.out.println("size" + size);
            s = "";
            index = i;
            String code = new String();
            donothing();
            //huffman code
            for (i = index; i < index + size; i++) {
                code += (char) fileContent[i];
            }
//            System.out.println("byte "+b+ " code "+code);
            bcodeDecompression.put(code, b);
            index = i;
        }
        s = "";
        s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
//        System.out.println("==s " +s);
        s = "";
        String token = "";
        File decompressedFile = new File(dfname) ;
        OutputStream fos = new FileOutputStream(decompressedFile);
        int remainder = 0;
        for (i=index;i<codesize+index-1;i++){
            s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
            for (int k=0;k<s.length() ;k++){
                token += s.charAt(k);
//                System.out.println("token="+token);
                if(bcodeDecompression.containsKey(token))
                {
                    //System.out.println("here "+codeDecompression.get(token));
                    fos.write(bcodeDecompression.get(token));
                    remainder = k;
                    donothing();
                    token ="";
                }
            }
            if (remainder != 0) {
                s = s.substring(remainder + 1, s.length());
                remainder = 0;
            }
            token = "";
        }
        fos.close();
        return true;
    }
    public static boolean compressFolder(String foldername ,HashMap<String,ArrayList<String> >results) throws IOException {
        File fout = new File(foldername.toString() + ".cmp");
        FileOutputStream fos = new FileOutputStream(fout);
        int foldersize = 0;
        ArrayList<String> fname = new ArrayList<>();
        File[] files = new File(foldername).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.
        for (File file : files) {
            if (file.isFile()) {
                fname.add(file.getName());
                foldersize += file.length();
            }
        }
        results.put(foldername,fname);
        for(File f:files){
            clearMaps();
            System.out.println("Currently decompressing file: "+f.getName()+" in folder: "+foldername+".");
            byte[] fileBytes=null;
            fileBytes = readBinary(f.getPath());
            for (Byte b : fileBytes) {
            if (byte_freq.containsKey(b))
                byte_freq.put(b, byte_freq.get(b) + 1);
            else
                byte_freq.put(b, 1);

        }

        for (Byte b : byte_freq.keySet()) {
            q.add(new Node(b, byte_freq.get(b)));
        }

        Node root = buildHuffman(q);
        getCodes(root, "");

        //getting code size to store in in the header of the file
        int codeSize = 0;
        int mapSize = 0;

        for (Byte b : bcodes.keySet()) {
            try {
                codeSize += bcodes.get(b).length() * byte_freq.get(b);
            } catch (Exception e) {
                System.out.println(e.getCause());
            }
        }
//        System.out.println("code size "+ codeSize );
        int codesbyte = (int) Math.ceil((double) codeSize / 8);
//        System.out.println("code size in bytes" + codesbyte );
        //allocating the first 4 bytes in the file to the file size
        byte[] sizeofcode = ByteBuffer.allocate(4).putInt(codesbyte).array();
        fos.write(sizeofcode);
        // map size

        byte[] sizeofmap = ByteBuffer.allocate(4).putInt(bcodes.size()).array();
        fos.write(sizeofmap);
        for (Byte b : bcodes.keySet()) {
//            System.out.println("key "+ b +" code " + bcodes.get(b) );
            //putting character
            // byte[] character = ByteBuffer.allocate(2).putChar(c).array();
            fos.write(b);
            // number of bits
            byte[] numofbits = ByteBuffer.allocate(4).putInt(bcodes.get(b).length()).array();
            fos.write(numofbits);
            // putting ASQUII code ex 1 -> 110001
            byte[] bits = bcodes.get(b).getBytes();
            fos.write(bits);
        }

        String remainder = "";
        try {

            for (Byte b : fileBytes) {
                String code = bcodes.get(b);
                String s = remainder + code;
                int len = s.length();
                int max = len - len % 8;
                if (len < 8) {
                    remainder = s;
                } else {
                    int start = 0;
                    int end = 8;
                    donothing();
                    while (end <= max) {
                        byte binary = (byte) Integer.parseInt(s.substring(start, end), 2);
                        start = end;
                        end += 8;
                        fos.write(binary);
                    }
                    remainder = s.substring(max, len);
                }
            }
            if (!remainder.equals("")) {
                byte binary = (byte) Integer.parseInt(remainder, 2);
                fos.write(binary);
            }

        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        }
        if(fout.length()>foldersize)
        {
            fout.delete();
            return false;
        }
        fos.close();
        return true;
    }
    public static boolean decompressFolder(String foldername,HashMap<String,ArrayList<String> >filenames) throws IOException {
        File file = new File(foldername);
        FileInputStream stream =null;
        stream = new FileInputStream(file);
        byte fileContent[] = new byte[(int) file.length()];
        stream.read(fileContent);
        stream.close();
        String decompname = foldername.split("\\.")[0];
        File theDir = new File(decompname+"-decomp");
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            theDir.mkdir();
        }
        int i=0,index=0,codesize,mapsize;
        ArrayList<String> fnames = filenames.get(foldername.split("\\.")[0]);
        for(String string: fnames)
            {
                System.out.println("Currently compressing file: "+string+" in folder: "+decompname+".");
                clearMaps();
                String s= "";
                byte b;
                // first 4 bytes as code size
                for (int l=i;l<i+4;l++){
                    s += String.format("%02x", fileContent[l]);
                }
                i+=4;
                codesize = Integer.parseInt(s, 16);
                s="";
                // map size 2nd four bytes
                for (int l =i;l<i+4;l++){
                    s += String.format("%02x", fileContent[l]);

                }
                i+=4;
                donothing();
                mapsize= Integer.parseInt(s, 16);
                index = i;
                s="";
                // looping throught the map to extract characters and their code
                for (int j=0;j<mapsize;j++){
                    int size =0;
                    //First byte for character
                    b =  fileContent[index];
                    index++;
                    // size of huffman code of each character
                    for (i = index; i < index + 4; i++) {
                        s += String.format("%02x", fileContent[i]);
                    }
                    size = Integer.parseInt(s, 16);
                    s = "";
                    index = i;
                    String code = new String();
                    //huffman code
                    for (i = index; i < index + size; i++) {
                        code += (char) fileContent[i];
                    }
                    bcodeDecompression.put(code, b);
                    index = i;
                }
                s = "";
                s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
                s = "";
                String token = "";
                File decompressedFile = new File(decompname+"-decomp"+"/"+string) ;
                OutputStream fos = new FileOutputStream(decompressedFile);
                int remainder = 0;
                for (i=index;i<codesize+index-1;i++){
                    s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
                    for (int k=0;k<s.length() ;k++){
                        token += s.charAt(k);
                        if(bcodeDecompression.containsKey(token))
                        {
                            fos.write(bcodeDecompression.get(token));
                            donothing();
                            remainder = k;
                            token ="";
                        }
                    }
                    if (remainder != 0) {
                        s = s.substring(remainder + 1, s.length());
                        remainder = 0;
                    }
                    token = "";
                }
                fos.close();
                i++;
            }
            return true;
    }
    public static void donothing()
    {

    }

}
