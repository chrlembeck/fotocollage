package org.lembeck.photocollage;

import org.junit.jupiter.api.Test;
import org.lembeck.photocollage.TreeNode.SplitType;
import java.awt.*;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllignTest {

    @Test
    public void verticalAlign() {
        ImageRef leftImage = new ImageRef(Paths.get("a"), 15, 10);
        ImageRef rightImage = new ImageRef(Paths.get("b"), 20, 11);
        Leaf left = new Leaf(leftImage);
        Leaf right = new Leaf(rightImage);
        InnerNode node = new InnerNode(left, right, 2.5f, SplitType.VERTICAL);
        Map<ImageRef, Rectangle> map = AlignmentTree.alignImages(0, 0, node, new CollageSettings(100, 43, 5, 5, Color.WHITE));
        assertEquals(5, map.get(leftImage).x);
        assertEquals(10 + map.get(leftImage).width, map.get(rightImage).x);
        assertEquals(5, map.get(leftImage).y);
        assertEquals(5, map.get(rightImage).y);
        assertEquals(39, map.get(leftImage).width);
        assertEquals(46, map.get(rightImage).width);
        assertEquals(100, map.get(leftImage).width + map.get(rightImage).width + 15);
        assertEquals(33, map.get(leftImage).height);
        assertEquals(33, map.get(rightImage).height);
    }
}