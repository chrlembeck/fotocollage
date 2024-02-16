package org.lembeck.photocollage;

import org.lembeck.photocollage.event.ImageComposeProgressListener;
import java.awt.image.BufferedImage;

public class AlignmentInfos {

    private final TreeNode treeNode;

    private final CollageSettings settings;

    public AlignmentInfos(TreeNode treeNode, CollageSettings settings) {
        this.treeNode = treeNode;
        this.settings = settings;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public BufferedImage createImage(ImageComposeProgressListener progressListener) {
        return AlignmentTree.paintImage(treeNode, settings, progressListener);
    }
}