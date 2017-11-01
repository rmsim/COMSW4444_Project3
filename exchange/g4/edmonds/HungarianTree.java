/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange.g4.edmonds;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author raf
 */
public class HungarianTree {
    TreeNode root;

    public HungarianTree(TreeNode root) {
        this.root = root;
        root.treeRef = this;
    }

    int getSize() {
        return root.getSize();
    }

    public ArrayList<Dumbbell> breakToDumbbells(HashSet<TreeNode> alternatingPath) {
        return root.breakToDumbbells(alternatingPath);
    }

    public void zmena(double r) {
        root.zmena(r);
    }
}
