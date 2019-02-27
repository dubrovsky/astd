package com.isc.astd.service.util;

import by.avest.certstore.dir.DirectoryCertStoreParameters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.security.cert.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by volkva on 25.08.2017.
 */
public class AvDirPKIXBuilderParameters {

  private PKIXBuilderParameters builderParameters;

  public PKIXBuilderParameters get() {
    return builderParameters;
  }

  public AvDirPKIXBuilderParameters(String certStorePath) throws Exception {
    CertStore certStore = CertStore.getInstance("AvDir", new DirectoryCertStoreParameters(certStorePath));

    // load trust certificates from certstorage and keystore
    Set<TrustAnchor> trustAnchros = new HashSet<>();
    loadTrustAnchors(trustAnchros, certStorePath);

    // create parameters for certificate verification
    builderParameters = new PKIXBuilderParameters(trustAnchros, null);
    // specifiy where to search for certificates
    builderParameters.addCertStore(certStore);
  }

  private void loadTrustAnchors(Set<TrustAnchor> trustAnchros, String storage) throws Exception {
    // load certificates from trusted directory
    File dir = new File(storage, "trusted");
    if (!(dir.exists() && dir.isDirectory())) {
      throw new RuntimeException("trusted directory not found");
    }

    // read files from directory

    File[] certFiles = dir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".cer");
      }
    });

    CertificateFactory cf = CertificateFactory.getInstance("X.509");

    for (File certFile : certFiles) {
      Certificate cert = cf.generateCertificate(new BufferedInputStream(new FileInputStream(certFile)));
      trustAnchros.add(new TrustAnchor((X509Certificate) cert, null));
    }
  }

}
