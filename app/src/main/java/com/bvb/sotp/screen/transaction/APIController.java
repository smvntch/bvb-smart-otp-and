package com.bvb.sotp.screen.transaction;//package vn.nsn.app.ocb.screen.transaction;
//
////import org.apache.commons.codec.binary.Hex;
//
//import org.apache.commons.codec.binary.Hex;
//
//import java.io.UnsupportedEncodingException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.SignatureException;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//
///**
// * @author auyong
// */
//public class APIController {
//
//
//    public static String calculateHmac256(String secret, String message) {
//        String hash = null;
//        try {
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//            hash = new String(Hex.encodeHex(sha256_HMAC.doFinal(message.getBytes())));
////            hash = Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));
//            System.out.println("com.securemetric.web.api.APIController.calculateHmac256()" + hash);
//            StringBuilder sb = new StringBuilder();
//            for (byte b : sha256_HMAC.doFinal(message.getBytes())) {
//                sb.append(String.format("%02X ", b));
//            }
//            System.out.println("com.securemetric.web.api.APIController.calculateHmac256()" + sb.toString().replaceAll(" ", "").toLowerCase());
//        } catch (Exception e) {
//            System.out.println("Error");
//            System.out.println("DEBUG Message[calculateHmac256] : Failed with exception " + e.getMessage());
//            e.printStackTrace();
//            return hash;
//        }
//        return hash;
////        finally {
////            return hash;
////        }
//    }
//
//
//    public static String generateCenToken(String key, String plainText) throws NoSuchAlgorithmException,
//            InvalidKeyException,
//            IllegalStateException,
//            UnsupportedEncodingException,
//            SignatureException,
//            NoSuchProviderException,
//            Exception {
//        final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
//        final Mac mac = Mac.getInstance("HmacSHA256");
//
//        mac.init(secretKey);
//
//        final byte[] bytes = mac.doFinal(plainText.getBytes("UTF-8"));
//
//        return Hex.encodeHexString(bytes);
//    }
//
//
//    //    public static void main() {
////
////        String user = "nurul_ca";
////        String details = "dGVzdGluZ01vYmlsZVB1c2g=";
////        String transactionId = "";
////        String otp = "";
////        String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
////        String secretKey = "p9ZkmsvbsQgG";
////        String integrationKey = "dc11c37d776c4afd56b3bc0f2d3be1f8236e36ae2a6d7870fbbd2d9160599e5e";
////
//////        String secretKey = "AsLwzUHtq8eA";
//////        String integrationKey = "e37743a026a882a3c9556f1a663ec42b62776df93f6baa664ec19555a338c9cc";
////        final String dmm = user + otp + transactionId + integrationKey + unixTimestamp;
////        System.out.println("com.securemetric.web.api.APIController.main() " + unixTimestamp);
////
////        System.out.println("com.securemetric.web.api.APIController.main() " + APIController.calculateHmac256(secretKey, dmm));
////    }
//    public static void main() {
//
//        //String user= "nurul_ca";
//        String user = "ndthanh123";
//        String details = "dGVzdGluZ01vYmlsZVB1c2g=";
//        String transactionId = "";
//        String otp = "";
//        String unixTimestamp = String.valueOf(System.currentTimeMillis() / 1000L);
//        //String secretKey = "p9ZkmsvbsQgG";
//        //String integrationKey = "dc11c37d776c4afd56b3bc0f2d3be1f8236e36ae2a6d7870fbbd2d9160599e5e";
//        String secretKey = "p98rjXh03qHX";
//        String integrationKey = "ee9228e350b10b744aff1135e01f3d1c858bcd25e84dd7038946bdb73144653d";
//
////        String secretKey = "AsLwzUHtq8eA";
////        String integrationKey = "e37743a026a882a3c9556f1a663ec42b62776df93f6baa664ec19555a338c9cc";
//        final String dmm = user + otp + transactionId + integrationKey + unixTimestamp;
//        System.out.println("com.securemetric.web.api.APIController.main() " + unixTimestamp);
//
//        System.out.println("com.securemetric.web.api.APIController.main() " + APIController.calculateHmac256(secretKey, dmm));
//    }
//}