package com.chrzanowy.graphanalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileTestUtils {

    public static File createTempFileWithContent() throws IOException {
        var tempFile = File.createTempFile("test", ".csv");
        try (var fileWriter = new FileWriter(tempFile)) {
            try (var bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write("""
                    "source_vertex_id","target_vertex_id"
                    27,24
                    """);
            }
        }
        return tempFile;
    }
}
