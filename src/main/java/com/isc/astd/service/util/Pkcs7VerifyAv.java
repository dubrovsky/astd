package com.isc.astd.service.util;

import by.avest.crypto.cert.verify.CertVerify;
import by.avest.crypto.cert.verify.CertVerifyResult;
import by.avest.crypto.ocsp.*;
import by.avest.crypto.pkcs.pkcs7.PKCS7;
import by.avest.crypto.pkcs.pkcs7.SignerInfo;
import by.avest.crypto.pkcs.pkcs9.PKCS9Attribute;
import by.avest.crypto.pkcs.pkcs9.attributes.AttributeFactory;
import by.avest.crypto.pkcs.pkcs9.attributes.RevocationValuesAttribute;
import by.avest.crypto.pkcs.pkcs9.attributes.SignatureTimestampTokenAttribute;
import by.avest.crypto.pkcs.pkcs9.attributes.SigningTimeAttribute;
import by.avest.crypto.timestamp.TimestampTokenInfo;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.X500Name;

import java.io.IOException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;

public final class Pkcs7VerifyAv {

  private PKCS7 pkcs7;
  private byte[] data;
//  private String rootAlias;
//  private KeyStore ks;
//  private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

//  public void setBuilderParameters(PKIXBuilderParameters builderParameters) {
//    this.builderParameters = builderParameters;
//  }

  private PKIXBuilderParameters builderParameters;

/*
  private void initCertStore(String certStorePath) throws Exception {
//    certStore = CertStore.getInstance("AvDir", new DirectoryCertStoreParameters(), new DirectoryCertStoreProvider());
    CertStore certStore = CertStore.getInstance("AvDir", new DirectoryCertStoreParameters(certStorePath));

//    builderParameters = new PKIXBuilderParameters(ks, null);
//    builderParameters.addCertStore(certStore);
//    builderParameters.setRevocationEnabled(true);

    // load trust certificates from certstorage and keystore
    Set<TrustAnchor> trustAnchros = new HashSet<>();
    loadTrustAnchors(trustAnchros, certStorePath);

    // create parameters for certificate verification
    builderParameters = new PKIXBuilderParameters(trustAnchros, null);
    // specifiy where to search for certificates
    builderParameters.addCertStore(certStore);
  }
*/

  public Pkcs7VerifyAv(PKIXBuilderParameters builderParameters, PKCS7 pkcs7, byte[] data) throws Exception {
//    this.rootAlias = rootAlias;
//    this.ks = ks;
    this.pkcs7 = pkcs7;
    this.data = data;
    this.builderParameters = builderParameters;
//    initCertStore(certStorePath);
  }

  public Pkcs7VerifyAv(PKIXBuilderParameters builderParameters, PKCS7 pkcs7) throws Exception {
//    this.rootAlias = rootAlias;
//    this.ks = ks;
    this.pkcs7 = pkcs7;
    this.data = pkcs7.getContentInfo().getContentBytes();
    this.builderParameters = builderParameters;
//    initCertStore(certStorePath);
  }

/*
  public boolean verify2() {
    try {
      SignerInfo[] allInfos = pkcs7.getSignerInfos();
      SignerInfo[] verInfos = pkcs7.verify();
      if(verInfos == null) verInfos = new SignerInfo[0];
      if (allInfos.length != verInfos.length) {
        throw new SignatureException("Only " + verInfos.length + " from " + allInfos.length + " signature(s) are valid");
      }

      return true;
    }
    catch (NoSuchAlgorithmException | SignatureException ex) {
      System.out.println(ex.getMessage());
      return false;
    }
  }
*/

  private StringBuffer mess = new StringBuffer();
  public String messFail = null;
  public StringBuffer CN = new StringBuffer();

  private final static Object o1 = new Object();

  //  public String SN = "";
  public boolean verify() throws Exception {
    synchronized (o1) {
      try {
//      AuthProvider avestProvider = (AuthProvider) Security.getProvider("AvJCEProvider");
//      avestProvider.login(null, null);
        by.avest.crypto.pkcs.pkcs7.SignerInfo[] signerInfos = pkcs7.getSignerInfos();
        if ((signerInfos == null) || (signerInfos.length == 0)) {
          messFail = "No signer info found.";
          mess.append(messFail);
          return false;
        }
        X509Certificate[] c = pkcs7.getCertificates();
        if (c.length > 0) {
          mess.append("verify certificate subject:\n");
//        SN = c[c.length-1].getSerialNumber().toString(16);
        }
        for (int i = 0; i < c.length; i++) {
          sun.security.x509.X500Name x500 = (X500Name) c[i].getSubjectDN();
          if (i > 0) CN.append(", ");
          CN.append(x500.getCommonName());
          mess.append(x500.getName());
          mess.append("\n");
        }

        boolean[] verify = new boolean[signerInfos.length];
        int izValid = 0;
        for (int i = 0; i < signerInfos.length; i++) {
          verify[i] = verifyPkcs7(signerInfos[i]);
          if (verify[i]) izValid++;
        }
        boolean ret = (izValid == signerInfos.length);
        if (!ret) {
          mess.append("Is not valid\n");
          messFail = "Only " + izValid + " from " + signerInfos.length + " signature(s) are valid";
          mess.append(messFail);
        } else {
          mess.append("Is valid");
        }
        return ret;
      } catch (Exception ex) {
        mess.append("\nIs not valid\n");
        messFail = "" + ex.getMessage();
        mess.append(messFail);
        ex.printStackTrace();
      }
    }
    return false;
  }

  private boolean verifyPkcs7(SignerInfo signer) throws Exception {
    //System.out.println("signer info");
    X509Certificate cert = signer.getCertificate(pkcs7);
    String s = getCertificateName(cert);
    mess.append("Certificate Name: ").append(s).append("\n");

    boolean isValid = pkcs7.verify(signer, data) != null;
    if (!isValid) {
      messFail = "Signature not valid";
      mess.append(messFail);
      return false;
    }
    else {
      mess.append("Signature Valid\n");
    }
//    vr.setSignatureValid(isValid);
//        PKCS9Attribute staAttr = si.getAuthenticatedAttributes().
//                                 getAttribute(PKCS9Attribute.SIGNING_TIME_OID);
//        SigningTimeAttribute sta = null;
//        if(staAttr != null) {
//          sta = AttributeFactory.newAttribute(staAttr);
//        }


//    KeyStore keyStore = KeyStore.getInstance("AvJCE");
//    keyStore.load(null, "1".toCharArray());


/*



if (cvr.getResultCode() != CertVerifyResult.AVCVR_CERT_VALID &&
        cvr.getResultCode() != CertVerifyResult.AVCVR_CERT_EXPIRED &&
        cvr.getResultCode() != CertVerifyResult.AVCVR_CERT_PATH_NOT_BUILT) {
//        certValidationText = "Invalid";
//        vr.setCertificateValidationText(certValidationText);
      } else
*/

    CertVerify cv = new CertVerify(builderParameters, false);
    Date nowDate = new Date();

    // unfinished code
    PKCS9Attribute timestampAttr = signer.getUnauthenticatedAttributes().
      getAttribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID);
    TimestampTokenInfo info;
    if (timestampAttr != null) {
      /** @todo  TimestampToken Certificate -- Проверка
       *
       */
      SignatureTimestampTokenAttribute stta = AttributeFactory.newAttribute(timestampAttr);

      PKCS7 p7 = stta.getToken();

      SignerInfo[] si_tt = pkcs7.verify();
      boolean isTValid = si_tt != null && si_tt.length > 0;
      mess.append("Timestamp Token Signature Valid: ").append(isTValid).append("\n");
      if(isTValid) {
        info = new TimestampTokenInfo(p7.getContentInfo().getContentBytes());
        nowDate = info.getGenTime();
        mess.append("Token Date: ").append(nowDate).append("\n");
      }
//      vr.setTimestampTokenDate(info.toString());
    }
    else {
      by.avest.crypto.pkcs.pkcs9.attributes.SigningTimeAttribute tt = (SigningTimeAttribute) signer.getAuthenticatedAttributes().getAttribute(PKCS9Attribute.SIGNING_TIME_OID);
      if(tt != null) {
        nowDate = tt.getTime();
        mess.append("SIGNING_TIME: ").append(nowDate).append("\n");
      }
    }

    CertVerifyResult cvr = cv.verify(cert, nowDate);

    PKCS9Attribute ocspAttr = signer.getUnauthenticatedAttributes().getAttribute(PKCS9Attribute.REVOCATION_VALUES_OID);
    if (ocspAttr != null) {
        CertId cId = new CertId(cert, cvr.getIssuerCert());
        RevocationValuesAttribute rva = AttributeFactory.newAttribute(ocspAttr);

        for (Iterator<BasicOCSPResponse> responses = rva.getOCSPResponses(); responses.hasNext(); ) {

          BasicOCSPResponse basicResponse = responses.next();
          if (basicResponse.verifySignature()) {
            BasicResponseTBS tbs = basicResponse.getTbs();
            if (tbs.singleResponsesSize() > 0) {
              SingleResponse sr = tbs.getSingleResponse(0);
              if (cId.equals(sr.getCertId())) {
//                CertVerify cv = createCertVerifyAv();
                Iterator<X509Certificate> it = basicResponse.certificatesIterator();
                boolean isCertValid = true;
                while (it.hasNext()) {
                  CertVerifyResult r = cv.verify(it.next(), sr.getThisUpdate());
                  isCertValid &= r.isCertValid();
                }
                if (isCertValid) {
                  CertStatus status = sr.getCertStatus();
                  String cs = "CertStatus - " + status.getStatus() + " (" + status.getCertStatusMsg() + " : " + status.toString() + ")\n";
                  mess.append(cs);
                  if (status.getStatus() != CertStatus.GOOD) {
                    messFail = cs;
                    return false;
                  }
                }
                else {
                  messFail = "OCSP Cert is not valid";
                  mess.append(messFail);
                  return false;
                }
              }
              else {
                messFail = "Wrong CertId: " + cId + ", " + sr.getCertId();
                mess.append(messFail);
                return false;
              }
            }
            else {
              messFail = "Empty response";
              mess.append(messFail);
              return false;
            }
          }
          else {
            messFail = "Signature failed";
            mess.append(messFail);
            return false;
          }

/*
          BasicOCSPResponse ocspResponse = responses.next();

          if (!ocspResponse.verifySignature()) {
            mess.append("ocsp response signature invalid\n");
            continue;
          }

          X509Certificate ocspCert = ocspResponse.getCertificate(0);
          if (ocspCert == null) {
            mess.append("ocsp certificate could not be found\n");
            continue;
          }

          CertStatus status = null;
          for (Iterator<SingleResponse> signles = ocspResponse.getTbs().singleResponsesIterator(); signles.hasNext(); ) {
            SingleResponse sr = signles.next();

            if (sr.getCertId().equals(certId)) {
              status = sr.getCertStatus();
              cvr = cv.verify(cert, sr.getThisUpdate());

              by.avest.crypto.pkcs.pkcs9.attributes.SigningTimeAttribute tt = (SigningTimeAttribute) signer.getAuthenticatedAttributes().getAttribute(PKCS9Attribute.SIGNING_TIME_OID);
              Date nowDate = tt.getTime();
              Date thisUpdate = sr.getThisUpdate();
              Date nextUpdate = sr.getNextUpdate();
              mess.append("SIGNING_TIME: " + df.format(nowDate) + ", thisUpdate: " + df.format(thisUpdate) + ", nextUpdate: " + df.format(nextUpdate) + "\n");
              //System.out.println(thisUpdate);
              //System.out.println(nextUpdate);

              if (thisUpdate != null && nowDate.before(thisUpdate)) {
                mess.append("ocsp response not yet valid\n");
                break;
              }
              if (nextUpdate != null && nowDate.after(nextUpdate)) {
                mess.append("ocsp response already not valid\n");
                break;
              }

              break;
            }
          }

          if (status != null) {
            ocspResponseStatus = status.getCertStatusName();
            mess.append("ocspResponseStatus: " + ocspResponseStatus + "\n");
//            vr.setOCSPResponseStatus(ocspResponseStatus);
            if (!(status instanceof UnknownInfo)) {
              try {
                ocspCert.verify(cvr.getIssuerCert().getPublicKey());
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
            break;
          }
*/
        }
    }
    else {
      if (!cvr.isCertValid()) {
        messFail = "Cert is not valid for date = " + nowDate;
        mess.append(messFail);
        return false;
      }
    }

    mess.append("Cert verification: true\n");


//    if(certValidation) resultMsg = "Подписанное сообщение действительно.";
    return true;
  }

  public static final int SIGNATURE_SIZE_IN_BYTES = 4096;

  public static String getCertificateName(X509Certificate certificate)
    throws IOException {
    if (certificate == null) {
      return "Undefined";
    }

    X500Name name = new X500Name(certificate.getSubjectDN().getName());
    DerValue attr = name.findMostSpecificAttribute(ObjectIdentifier
      .newInternal(new int[]{2, 5, 4, 41}));
    String firstName = attr == null ? "" : attr.getAsString();

    attr = name.findMostSpecificAttribute(X500Name.SURNAME_OID);
    String surName = attr == null ? "" : attr.getAsString();

    if ("".equals(firstName) && "".equals(surName)) {
      String cn = name.getCommonName();
      return cn == null ? name.toString() : cn;
    } else {
      return surName + " " + firstName;
    }
  }

/*
  public static CertVerify createCertVerifyAv() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidAlgorithmParameterException {
    // create certstore
    String storageDir = ProjProperties.getProperty("CAStorage");
    CertStore certStore = CertStore.getInstance("AvDir", new DirectoryCertStoreParameters(storageDir));

    // load trust certificates from certstorage and keystore
    Set<TrustAnchor> trustAnchros = new HashSet<>();
    loadTrustAnchors(trustAnchros, storageDir);

    // create parameters for certificate verification
    PKIXBuilderParameters pbp = new PKIXBuilderParameters(trustAnchros, null);
    // specifiy where to search for certificates
    pbp.addCertStore(certStore);
    // create certverify with revocation enabled
    CertVerify cv = new CertVerify(pbp, false);
    return cv;
  }
*/

/*
  private static void loadTrustAnchors(Set<TrustAnchor> trustAnchros, String storage) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
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
*/

  public String toString() {
    return mess.toString();
  }

}
