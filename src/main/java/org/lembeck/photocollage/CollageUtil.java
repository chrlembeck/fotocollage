package org.lembeck.photocollage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class CollageUtil {

    public static List<ImageRef> ladeBilder(Path path) throws IOException {
        BiPredicate<Path, BasicFileAttributes> matcher = (file, attrib) -> {
            String filenameLower = file.getFileName().toString().toLowerCase();
            return attrib.isRegularFile() && (filenameLower.endsWith(".jpg") || filenameLower.endsWith(".jpeg") || filenameLower.endsWith(".png"));
        };
        try (Stream<Path> files = Files.find(path, 1, matcher)) {
            return files.parallel().map(ImageRef::new).toList();
        }
    }
}