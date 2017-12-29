/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;
import java.util.Comparator;

/**
 *
 * @author Toka
 */
public class comparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
            if (o1.getF() < o2.getF()){
                return -1;
            }
            if (o1.getF() > o2.getF()){
                return 1;
            }
            return 0;
            
    }   
}
