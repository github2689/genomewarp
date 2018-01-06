/*
 * Copyright 2016 Verily Life Sciences LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.verily.genomewarp.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class GvcfToVcfAndBedTest {
  // constants
  private static final String SAMPLE_GVCF = "utils/smallNA12889_S1.genome_g.vcf";
  private static final String EXPECTED_VCF = "utils/smallNA12889_S1.genome.vcf";
  private static final String EXPECTED_BED = "utils/smallNA12889_S1.genome.bed";
  private static final String MALFORMED_GVCF = "utils/malformed_g.vcf";

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testSaveVcfAndBedFromGvcf() throws IOException {
    File sampleGvcf =
        new File(GvcfToVcfAndBedTest.class.getClassLoader().getResource(SAMPLE_GVCF).getFile());
    String rawQueryGvcf = sampleGvcf.getCanonicalPath();
    String tempFolderPath = tempFolder.getRoot().getCanonicalPath();
    String outVcf = tempFolderPath + File.separator + "from_gvcf.vcf";
    String outBed = tempFolderPath + File.separator + "from_gvcf.bed";
    assertTrue(GvcfToVcfAndBed.saveVcfAndBedFromGvcf(rawQueryGvcf, outVcf, outBed));

    // compare files line by line
    String line1, line2;

    // compare VCF files
    File expVcf =
        new File(GvcfToVcfAndBedTest.class.getClassLoader().getResource(EXPECTED_VCF).getFile());
    try (
        BufferedReader expVcfBR =
            Files.newBufferedReader(Paths.get(expVcf.getCanonicalPath()), UTF_8);
        BufferedReader gotVcfBR = Files.newBufferedReader(Paths.get(outVcf), UTF_8)) {
      while ((line1 = gotVcfBR.readLine()) != null) {
        line2 = expVcfBR.readLine();
        assertTrue(line2 != null);
        assertEquals(line1, line2);
      }
      assertTrue(expVcfBR.readLine() == null);
    }

    // compare BED files
    File expBed =
        new File(GvcfToVcfAndBedTest.class.getClassLoader().getResource(EXPECTED_BED).getFile());
    try (
        BufferedReader expBedBR =
            Files.newBufferedReader(Paths.get(expBed.getCanonicalPath()), UTF_8);
        BufferedReader gotBedBR = Files.newBufferedReader(Paths.get(outBed), UTF_8);) {
      while ((line1 = gotBedBR.readLine()) != null) {
        line2 = expBedBR.readLine();
        assertTrue(line2 != null);
        assertEquals(line1, line2);
      }
      assertTrue(expBedBR.readLine() == null);
    }
  }

  @Test
  public void testSaveVcfAndBedFromCanonicalVcf() throws IOException {
    File sampleGvcf =
        new File(GvcfToVcfAndBedTest.class.getClassLoader().getResource(EXPECTED_VCF).getFile());
    assertFalse(extractFromSampleGvcf(sampleGvcf));
  }

  @Test
  public void testSaveVcfAndBedMalformedGvcf() throws IOException {
    File sampleGvcf =
        new File(GvcfToVcfAndBedTest.class.getClassLoader().getResource(MALFORMED_GVCF).getFile());
    assertFalse(extractFromSampleGvcf(sampleGvcf));
  }

  /**
   * Auxiliary function
   * 
   * @param sampleGvcf sample gVCF
   * @return true if gVCF processed successfully
   * @throws IOException
   */
  private boolean extractFromSampleGvcf(File sampleGvcf) throws IOException {
    String rawQueryGvcf = sampleGvcf.getCanonicalPath();
    String tempFolderPath = tempFolder.getRoot().getCanonicalPath();
    String outVcf = tempFolderPath + File.separator + "from_gvcf.vcf";
    String outBed = tempFolderPath + File.separator + "from_gvcf.bed";
    return GvcfToVcfAndBed.saveVcfAndBedFromGvcf(rawQueryGvcf, outVcf, outBed);
  }
}
