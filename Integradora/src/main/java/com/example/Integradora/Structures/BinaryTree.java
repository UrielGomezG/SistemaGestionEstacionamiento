package com.example.Integradora.Structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de datos Árbol Binario de Búsqueda (BST) implementada manualmente.
 * Se utiliza para almacenar los tickets generados de manera ordenada.
 * Permite insertar elementos y obtenerlos en orden ascendente (InOrder
 * traversal).
 * 
 * @param <T> Tipo de dato a almacenar, debe ser Comparable
 */
public class BinaryTree<T extends Comparable<T>> {
    private TreeNode<T> root;

    public BinaryTree() {
        this.root = null;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private TreeNode<T> insertRec(TreeNode<T> root, T data) {
        if (root == null) {
            return new TreeNode<>(data);
        }
        if (data.compareTo(root.data) < 0) {
            root.left = insertRec(root.left, data);
        } else {
            root.right = insertRec(root.right, data);
        }
        return root;
    }

    public List<T> getInOrder() {
        List<T> list = new ArrayList<>();
        inOrderRec(root, list);
        return list;
    }

    private void inOrderRec(TreeNode<T> root, List<T> list) {
        if (root != null) {
            inOrderRec(root.left, list);
            list.add(root.data);
            inOrderRec(root.right, list);
        }
    }

    private static class TreeNode<T> {
        T data;
        TreeNode<T> left;
        TreeNode<T> right;

        public TreeNode(T data) {
            this.data = data;
            left = null;
            right = null;
        }
    }
}
