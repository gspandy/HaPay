package com.hengaiw.test;
import java.io.*;
import java.lang.reflect.Method;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

import javax.crypto.Cipher;

public class TestEncryp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//PrivateKey pri=getPriKey("/Users/cqx/bh_pkcs8_rsa_private_key_2048.pem","RSA");
		PublicKey pub=getPubKey("/Users/jianhuizhang/Desktop/publicKey.pem","RSA");
		System.out.println("hahhahah11");
		String str="我是需要传递的字符串";
		byte[] estr=encrypt(str.getBytes(),pub,2048, 11,"RSA/ECB/PKCS1Padding");
		System.out.println(new String(estr));
		System.out.println("hahhahah12");
//		byte[] dstr=decrypt(estr, pri, 2048, 11, "RSA/ECB/PKCS1Padding");
//		System.out.println(new String(dstr));
		
	}
	public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8;
        int decryptBlockSize = keyByteSize - reserveSize;
        int nBlock = encryptedBytes.length / keyByteSize;
        ByteArrayOutputStream outbuf = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }
                byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                outbuf.write(decryptedBlock);
            }
            outbuf.flush();
            return outbuf.toByteArray();
        } catch (Exception e) {
            throw new Exception("DEENCRYPT ERROR:", e);
        } finally {
            try{
                if(outbuf != null){
                    outbuf.close();
                }
            }catch (Exception e){
                outbuf = null;
                throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
            }
        }
    }
	public static byte[] encrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8;
        int encryptBlockSize = keyByteSize - reserveSize;
        int nBlock = plainBytes.length / encryptBlockSize;
        if ((plainBytes.length % encryptBlockSize) != 0) {
            nBlock += 1;
        }
        ByteArrayOutputStream outbuf = null;
        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                outbuf.write(encryptedBlock);
            }
            outbuf.flush();
            return outbuf.toByteArray();
        } catch (Exception e) {
            throw new Exception("ENCRYPT ERROR:", e);
        } finally {
            try{
                if(outbuf != null){
                    outbuf.close();
                }
            }catch (Exception e){
                outbuf = null;
                throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
            }
        }
    }
	public static PrivateKey getPriKey(String privateKeyPath,String keyAlgorithm){
        PrivateKey privateKey = null;
        InputStream inputStream = null;
        try {
            if(inputStream==null){
            	System.out.println("hahhah1!");
            }

            inputStream = new FileInputStream(privateKeyPath);
            System.out.println("hahhah2!");
            privateKey = getPrivateKey(inputStream,keyAlgorithm);
            System.out.println("hahhah3!");
        } catch (Exception e) {
        	System.out.println("加载私钥出错!");
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                }catch (Exception e){
                    System.out.println("加载私钥,关闭流时出错!");
                }
            }
        }
        return privateKey;
    }
	public static PublicKey getPubKey(String publicKeyPath,String keyAlgorithm){
        PublicKey publicKey = null;
        InputStream inputStream = null;
        try {
        	System.out.println("hahhahah8");
            inputStream = new FileInputStream(publicKeyPath);
            System.out.println("hahhahah9");
            publicKey = getPublicKey(inputStream,keyAlgorithm);
            System.out.println("hahhahah10");
        } catch (Exception e) {
            System.out.println("加载公钥出错!");
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                }catch (Exception e){
                	System.out.println("加载公钥,关闭流时出错!");
                }
            }
        }
        return publicKey;
    }
	public static PublicKey getPublicKey(InputStream inputStream, String keyAlgorithm) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PublicKey publicKey = keyFactory.generatePublic(pubX509);

            return publicKey;
        } catch (Exception e) {
            throw new Exception("READ PUBLIC KEY ERROR:", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                inputStream = null;
                throw new Exception("INPUT STREAM CLOSE ERROR:", e);
            }
        }
    }
	 public static PrivateKey getPrivateKey(InputStream inputStream, String keyAlgorithm) throws Exception {
	        try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	            StringBuilder sb = new StringBuilder();
	            String readLine = null;
	            while ((readLine = br.readLine()) != null) {
	                if (readLine.charAt(0) == '-') {
	                    continue;
	                } else {
	                    sb.append(readLine);
	                    sb.append('\r');
	                }
	            }
	            System.out.println("hahhah4!"+decodeBase64(sb.toString()));
	            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decodeBase64(sb.toString()));
	            System.out.println("hahhah5!");
	            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
	            System.out.println("hahhah6!");
	            PrivateKey privateKey = keyFactory.generatePrivate(priPKCS8);
	            System.out.println("hahhah7!");
	            return privateKey;
	        } catch (Exception e) {
	            throw new Exception("READ PRIVATE KEY ERROR:" ,e);
	        }  finally {
	            try {
	                if (inputStream != null) {
	                    inputStream.close();
	                }
	            } catch (IOException e) {
	                inputStream = null;
	                throw new Exception("INPUT STREAM CLOSE ERROR:", e);
	            }
	        }
	    }
	 //一下面是base64的编码和解码
	 public static String encodeBase64(byte[]input) throws Exception{  
	        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");  
	        Method mainMethod= clazz.getMethod("encode", byte[].class);  
	        mainMethod.setAccessible(true);  
	         Object retObj=mainMethod.invoke(null, new Object[]{input});  
	         return (String)retObj;  
	    }  
	    /*** 
	     * decode by Base64 
	     */  
	    public static byte[] decodeBase64(String input) throws Exception{  
	        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");  
	        Method mainMethod= clazz.getMethod("decode", String.class);  
	        mainMethod.setAccessible(true);  
	         Object retObj=mainMethod.invoke(null, input);  
	         return (byte[])retObj;  
	    }  
}
