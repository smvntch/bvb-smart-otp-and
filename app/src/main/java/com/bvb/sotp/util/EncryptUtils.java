package com.bvb.sotp.util;

import android.util.Base64;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Nguyen Anh Vu on 11/3/2014.
 */
public class EncryptUtils {
    public static String MD5(String s) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static String EncryptUsingRSA(String data, String XmlPublicKey) {
        try {
            /*
            <RSAKeyValue>
                <Modulus>
                    #publickey#
                </Modulus>
                <Exponent>
                    #expo#
                </Exponent>
            </RSAKeyValue>
             */
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XmlPublicKey));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("RSAKeyValue");
            Element element = (Element) nodes.item(0);

            NodeList Modulus = element.getElementsByTagName("Modulus");
            NodeList Exponent = element.getElementsByTagName("Exponent");
            Element modulusElem = (Element) Modulus.item(0);
            Element exponentElem = (Element) Exponent.item(0);

            byte[] expBytes = Base64.decode(getCharacterDataFromElement(exponentElem), Base64.DEFAULT);
            byte[] modBytes = Base64.decode(getCharacterDataFromElement(modulusElem), Base64.DEFAULT);

            BigInteger modules = new BigInteger(1, modBytes);
            BigInteger exponent = new BigInteger(1, expBytes);

            // Get an instance of the Cipher for RSA encryption/decryption
            KeyFactory factory = KeyFactory.getInstance("RSA");
            Cipher c = Cipher.getInstance("RSA");

            RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules, exponent);
            PublicKey pubKey = factory.generatePublic(pubSpec);

            // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
            c.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encrypted = c.doFinal(data.getBytes("UTF-8"));
            return new String(encrypted);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String DencryptUsingRSA(String data, String XmlPrivateKey) {
        try {
            /*
            <RSAKeyValue>
                <Modulus>
                    #modules#
                </Modulus>
                <Exponent>
                    #expo#
                </Exponent>
                <P>
                    #P#
                </P>
                <Q>
                    #Q#
                </Q>
                <DP>
                    #DP#
                </DP>
                <DQ>
                    #DQ#
                </DQ>
                <InverseQ>
                    #InverseQ#
                </InverseQ>
                <D>
                    #D#
                </D>
            </RSAKeyValue>
             */
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XmlPrivateKey));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("RSAKeyValue");
            Element element = (Element) nodes.item(0);

            NodeList Modulus = element.getElementsByTagName("Modulus");
            NodeList D = element.getElementsByTagName("D");

            Element modulusElem = (Element) Modulus.item(0);
            Element dElem = (Element) D.item(0);

            byte[] modBytes = Base64.decode(getCharacterDataFromElement(modulusElem), Base64.DEFAULT);
            byte[] dBytes = Base64.decode(getCharacterDataFromElement(dElem), Base64.DEFAULT);

            BigInteger modules = new BigInteger(1, modBytes);
            BigInteger d = new BigInteger(1, dBytes);

            // Get an instance of the Cipher for RSA encryption/decryption
            KeyFactory factory = KeyFactory.getInstance("RSA");
            Cipher c = Cipher.getInstance("RSA");

            RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, d);
            PrivateKey privKey = factory.generatePrivate(privSpec);

            // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
            c.init(Cipher.DECRYPT_MODE, privKey);
            byte[] decrypted = c.doFinal(data.getBytes("UTF-8"));
            return new String(decrypted);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    /*
    Some options of DES formats:
        DESede/ECB/Nopadding
        DESede/CBC/ZeroBytePadding
        DESede/CBC/PKCS5Padding
        DESede/ECB/PKCS7Padding


        DESede/ECB/ZeroBytePadding
    */


    public static String EncryptUsing3DES(String sKey, String sData) throws Exception {
        final byte[] digestOfPassword = sKey.getBytes("utf-8");
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 16);
//        for (int j = 0, k = 16; j < 8;)
//        {
//            keyBytes[k++] = keyBytes[j++];
//        }

        final byte[] byteArray = EncryptUtils.hexStringToByteArray(sData);

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final Cipher cipher = Cipher.getInstance("DESede/ECB/ZeroBytePadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        final byte[] cipherText = cipher.doFinal(byteArray);

        String sString = bytesToHex(cipherText);
        return sString;
    }

    public static String DencryptUsing3DES(String sKey, String sData, boolean hex) throws Exception {
        final byte[] digestOfPassword = sKey.getBytes("utf-8");
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 16);
//        for (int j = 0, k = 16; j < 8;)
//        {
//            keyBytes[k++] = keyBytes[j++];
//        }

        final byte[] byteArray = EncryptUtils.hexStringToByteArray(sData);

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final Cipher decipher = Cipher.getInstance("DESede/ECB/ZeroBytePadding");
        decipher.init(Cipher.DECRYPT_MODE, key);

        final byte[] plainText = decipher.doFinal(byteArray);
        if (hex) {
            return EncryptUtils.bytesToHex(plainText);
        } else {
            return new String(plainText, "UTF-8");
        }
    }

    public static String String2Hex(String sString) {
        try {
            char[] chars = sString.toCharArray();
            StringBuffer hex = new StringBuffer();
            for (int i = 0; i < chars.length; i++) {
                hex.append(Integer.toHexString((int) chars[i]));
            }

            return hex.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] StringToByteArray(String s) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        return b;
    }

    public static String Hex2String(String sHex) {
        try {
            StringBuilder sb = new StringBuilder();
            StringBuilder temp = new StringBuilder();

            //49204c6f7665204a617661 split into two characters 49, 20, 4c...
            for (int i = 0; i < sHex.length() - 1; i += 2) {

                //grab the hex in pairs
                String output = sHex.substring(i, (i + 2));
                //convert hex to decimal
                int decimal = Integer.parseInt(output, 16);
                //convert the decimal to character
                sb.append((char) decimal);

                temp.append(decimal);
            }

            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] Hex2ByteArray(String sHex) {
        int len = sHex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) (sHex.charAt(i) + sHex.charAt(i + 1));
        }
        return data;
    }

}
