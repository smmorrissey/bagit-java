
package gov.loc.repository.bagit.domain;

import java.io.File;
import java.util.HashMap;

/**
 * A manifest is a list of files and their corresponding checksum with the algorithm used to generate that checksum
 */
public class Manifest {
  private final SupportedAlgorithm algorithm;
  private HashMap<File, String> fileToChecksumMap = new HashMap<>();
  
  public Manifest(SupportedAlgorithm algorithm){
    this.algorithm = algorithm;
  }

  public HashMap<File, String> getFileToChecksumMap() {
    return fileToChecksumMap;
  }

  public void setFileToChecksumMap(HashMap<File, String> fileToChecksumMap) {
    this.fileToChecksumMap = fileToChecksumMap;
  }

  public SupportedAlgorithm getAlgorithm() {
    return algorithm;
  }

  @Override
  public String toString() {
    return "Manifest [algorithm=" + algorithm + ", fileToChecksumMap=" + fileToChecksumMap + "]";
  }
}
