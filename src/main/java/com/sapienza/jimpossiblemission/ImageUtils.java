package com.sapienza.jimpossiblemission;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage flipHorizontal(BufferedImage img) {
        int w = img.getWidth();

        // Trasformazione: scala a -1 sull’asse X, poi trasla di w pixel
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-w, 0);

        // Applica la trasformazione
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
}
