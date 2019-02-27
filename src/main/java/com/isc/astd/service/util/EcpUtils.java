package com.isc.astd.service.util;

import by.avest.crypto.pkcs.pkcs7.PKCS7;
import by.avest.crypto.pkcs.pkcs7.SignerInfo;
import by.avest.crypto.util.ByteArrayUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X500Name;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;


/**
 * Created by LAN on 03.10.2014.
 */
public class EcpUtils {
    private static final Logger log = Logger.getLogger(EcpUtils.class);

    public static String getHash(byte[] src, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(src);
        byte[] digest = md.digest();
        return Hex.encodeHexString( digest );
    }

    public static String getSerial(byte[] pkcs7Bin) {
        String serial = "";
        try {
            if (pkcs7Bin == null || pkcs7Bin.length == 0)
                throw new Exception("Empty sign");

            if (Base64.isArrayByteBase64(pkcs7Bin))
                pkcs7Bin = Base64.decodeBase64(pkcs7Bin);

            PKCS7 pkcs7 =  new PKCS7(pkcs7Bin);
            SignerInfo[] signerInfos = pkcs7.getSignerInfos();
            if (signerInfos.length != 0) {
                serial = signerInfos[signerInfos.length-1].getCertificateSerialNumber().toString(16).toLowerCase();
            }
            else
                throw new Exception("No SignerInfo found in sign");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return serial;
    }

    public static String getSerial(HttpServletRequest request) {
        String serial = "";
        try {
            X509Certificate x509Certificate = getClientCertificate(request);
            if (x509Certificate != null)
                serial = x509Certificate.getSerialNumber().toString(16);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return serial;
    }

    public static String getTitleName(X509Certificate cert) {
        try {
            X500Name name = new X500Name(cert.getSubjectDN().getName());
            DerValue derTitle =  name.findMostSpecificAttribute(X500Name.title_oid);
            return (derTitle != null ? derTitle.getAsString() : "");
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static String getTitleName(HttpServletRequest request) {
        try {
            return getTitleName(getClientCertificate(request)) ;
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static String getSurName(X509Certificate cert) {
        try {
            String result = "";
            X500Name name = new X500Name(cert.getSubjectDN().getName());
            String IO =  name.findMostSpecificAttribute(EcpOIDs.I_O_OID).getAsString();
            String[] I_O = IO.split(" ");
            if (I_O.length == 2) {
                result = I_O[0].substring(0, 1) + ". "+ I_O[1].substring(0, 1) + ". ";
            }

            return (result + name.getSurname());
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static String getSurName(HttpServletRequest request) {
        try {
            return getSurName(getClientCertificate(request)) ;
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static X509Certificate getClientCertificate(HttpServletRequest request) {
        Object obj = request.getAttribute("javax.servlet.request.X509Certificate");
        if (obj != null) {
            if (obj instanceof X509Certificate) {
                return (X509Certificate)obj;

            } else if (obj instanceof X509Certificate[]) {
                X509Certificate[] x509certArray = (X509Certificate[])obj;
                if(x509certArray != null && (x509certArray.length > 0)) {
                    return x509certArray[0];
                }
            }
        }
        return null;
    }


    public static  String getBase64ClientCertificate(HttpServletRequest request) {
      X509Certificate cert = getClientCertificate(request);
      String Base64cert = "";
      try {
        Base64cert = fromBinaryToBase64(cert.getEncoded());
      }catch (Exception ex) {

      }
      return Base64cert;
    }

    public static String fromBinaryToBase64(byte[] data) {
         if (data != null) {
              BASE64Encoder encoder = new BASE64Encoder();
              return encoder.encode(data);
         }

         return null;
       }

    public static byte[] fromBase64ToBinary(String data) throws Exception{
         if (data != null) {
              BASE64Decoder decoder = new BASE64Decoder();
              return decoder.decodeBuffer(data);
         }
         return null;
       }


    public String getClientCertHash(HttpServletRequest request) throws IOException, NoSuchAlgorithmException, CertificateEncodingException
    {
            X509Certificate clientCert = getClientCertificate(request);

            if (clientCert != null) {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    byte[] certSha1Hash = md.digest(clientCert.getEncoded());
                    return ByteArrayUtil.toHexString(certSha1Hash);
            }

            return null;
    }

    private static String parseUNPExtension(X509Certificate cert) throws CertificateParsingException {
          return parseSimpleBMPStringExtension(
                          cert, EcpOIDs.EXTENSION_UNP_OID);
    }



    private static String parseSimpleBMPStringExtension(X509Certificate cert, ObjectIdentifier extensionOid) throws CertificateParsingException
    {
          try {
                  byte[] subjectKeyIdExtValue = cert.getExtensionValue(
                                  extensionOid.toString());

                  if (subjectKeyIdExtValue != null) {
                          SubjectKeyIdentifierExtension subjectKeyIdExt = new SubjectKeyIdentifierExtension(subjectKeyIdExtValue);

                          DerValue derValue1 = new DerValue(subjectKeyIdExt.getExtensionValue());
                          byte[] octetValue1 = derValue1.getOctetString();

                          DerValue derValue2 = new DerValue(octetValue1);
                          byte[] octetValue2 = derValue2.getOctetString();

                          DerValue derValue3 = new DerValue(octetValue2);
                          return derValue3.getBMPString();
                  } else {
                          return null;
                  }
          } catch (IOException e) {
                  throw new CertificateParsingException(
                                  "Could not parse SubjectKeyIdentifierExtension. " + e.getMessage());
          }
    }

}
