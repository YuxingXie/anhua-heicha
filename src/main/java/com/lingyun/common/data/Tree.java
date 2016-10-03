package com.lingyun.common.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;


public class Tree<T> {

    private Node<T> root;

    public Tree() {
    }

    public Tree(Node<T> root) {
        this.root = root;
    }

    //创建二叉树
    public void buildTree() {

        Scanner scn = null;
        try {
            scn = new Scanner(new File("D:/input.txt"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        root = createTree(root,scn);
    }
    //先序遍历创建二叉树
    private Node<T> createTree(Node<T> node,Scanner scn) {

        String temp  = scn.next();

        if (temp.trim().equals("#")) {
            return null;
        } else {
            node = new Node<T>((T)temp);
            node.setLeft(createTree(node.getLeft(), scn));
            node.setRight(createTree(node.getRight(), scn));
            return node;
        }

    }



    //中序遍历(递归)
    public void inOrderTraverse() {
        inOrderTraverse(root);
    }

    public void inOrderTraverse(Node<T> node) {
        if (node != null) {
            inOrderTraverse(node.getLeft());
            System.out.println(node.getValue());
            inOrderTraverse(node.getRight());
        }
    }


    //中序遍历(非递归)
    public void nrInOrderTraverse() {

        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> node = root;
        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }
            node = stack.pop();
            System.out.println(node.getValue());
            node = node.getRight();

        }

    }
    //先序遍历(递归)
    public void preOrderTraverse() {
        preOrderTraverse(root);
    }

    public void preOrderTraverse(Node<T> node) {
        if (node != null) {
            System.out.println(node.getValue());
            preOrderTraverse(node.getLeft());
            preOrderTraverse(node.getRight());
        }
    }


    //先序遍历（非递归）
    public void nrPreOrderTraverse() {

        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> node = root;

        while (node != null || !stack.isEmpty()) {

            while (node != null) {
                System.out.println(node.getValue());
                stack.push(node);
                node = node.getLeft();
            }
            node = stack.pop();
            node = node.getRight();
        }

    }

    //后序遍历(递归)
    public void postOrderTraverse() {
        postOrderTraverse(root);
    }

    public void postOrderTraverse(Node<T> node) {
        if (node != null) {
            postOrderTraverse(node.getLeft());
            postOrderTraverse(node.getRight());
            System.out.println(node.getValue());
        }
    }

    //后续遍历(非递归)
    public void nrPostOrderTraverse() {

        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> node = root;
        Node<T> preNode = null;//表示最近一次访问的节点

        while (node != null || !stack.isEmpty()) {

            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }

            node = stack.peek();

            if (node.getRight() == null || node.getRight() == preNode) {
                System.out.println(node.getValue());
                node = stack.pop();
                preNode = node;
                node = null;
            } else {
                node = node.getRight();
            }

        }




    }

    //按层次遍历
    public void levelTraverse() {
        levelTraverse(root);
    }

    public void levelTraverse(Node<T> node) {

        Queue<Node<T>> queue = new LinkedBlockingQueue<Node<T>>();
        queue.add(node);
        while (!queue.isEmpty()) {

            Node<T> temp = queue.poll();
            if (temp != null) {
                System.out.println(temp.getValue());
                queue.add(temp.getLeft());
                queue.add(temp.getRight());
            }

        }

    }




}



//树的节点

class Node<T> {

    private Node<T> left;
    private Node<T> right;
    private T value;

    public Node() {
    }
    public Node(Node<T> left,Node<T> right,T value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public Node(T value) {
        this(null,null,value);
    }
    public Node<T> getLeft() {
        return left;
    }
    public void setLeft(Node<T> left) {
        this.left = left;
    }
    public Node<T> getRight() {
        return right;
    }
    public void setRight(Node<T> right) {
        this.right = right;
    }
    public T getValue() {
        return value;
    }
    public void setValue(T value) {
        this.value = value;
    }
    public static void main(String[] args) {
        Tree<Integer> tree = new Tree<Integer>();
        tree.buildTree();
        System.out.println("中序遍历");
        tree.inOrderTraverse();
        tree.nrInOrderTraverse();
        System.out.println("后续遍历");
        //tree.nrPostOrderTraverse();
        tree.postOrderTraverse();
        tree.nrPostOrderTraverse();
        System.out.println("先序遍历");
        tree.preOrderTraverse();
        tree.nrPreOrderTraverse();

//
    }

}