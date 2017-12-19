/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

/**
 *
 * @author Toka
 */
public class Node {
    //storing frequency , character ,left and right for each node 
    private char c;
    private int f;
    private Node left=null,right=null;

    public Node(char c, int f) {
        this.c = c;
        this.f = f;
        this.right=null;
        this.left=null;
    }
    public Node ()
    {
        this.right=null;
         this.left=null;
    }


    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Node getLeft() {
        return this.left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return this.right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
    
    public void print ()
    {
        System.out.println(this.c+"="+this.f);
    }

    
}
