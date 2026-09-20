package edu.caltech.cs2.helpers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.util.Preconditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;


public class FileSourceProvider implements ArgumentsProvider, AnnotationConsumer<FileSource> {
  private String[] inputs;
  private String[] outputFiles;


  @Override
  public void accept(FileSource source) {
    this.inputs = source.inputs();//Stream.of(source.inputs()).map((x) -> Arrays.asList(x.split("\\|"))).collect(Collectors.toList());
    this.outputFiles = source.outputFiles();
  }

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    Arguments[] args = new Arguments[this.outputFiles.length];
    for (int i = 0; i < this.outputFiles.length; i++) {
      Object inputArgs = null;
      String sArgs = this.inputs[i];
      if (sArgs.startsWith("{")) {
         sArgs = sArgs.substring(1, sArgs.length() - 1);
         String[] argMaps = sArgs.split(", ");
         Map<String, String> m = new HashMap<>();
         for (String s : argMaps) {
           String[] pieces = s.split(" = ");
           m.put(pieces[0], pieces[1]);
         }
         inputArgs = m;
      }
      else {
        inputArgs = sArgs;
      }
      Scanner in = getScanner(this.outputFiles[i]);
      String output = in.useDelimiter("\\Z").next();
      args[i] = Arguments.arguments(inputArgs, output);
    }
    return Stream.of(args);
  }

  private Scanner getScanner(String resource) {
    Preconditions.notBlank(resource, "Test file " + resource + " must not be null or blank");
    try {
      return new Scanner(new File("tests/data/" + resource));
    } catch (FileNotFoundException e) {
      fail("Test file " + resource + " does not exist");
    }
    return null;
  }
}
