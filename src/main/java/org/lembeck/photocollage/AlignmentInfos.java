package org.lembeck.photocollage;

import java.awt.*;
import java.util.Map;

public class AlignmentInfos {

    private final TreeNode treeNode;

    private final CollageSettings settings;

    public AlignmentInfos(TreeNode treeNode, CollageSettings settings) {
        this.treeNode = treeNode;
        this.settings = settings;
    }

    public Map<ImageRef, Rectangle> calcImagePositions(int dx, int dy) {
        return AlignmentTree.alignImages(dx, dy, treeNode, settings);
    }
}