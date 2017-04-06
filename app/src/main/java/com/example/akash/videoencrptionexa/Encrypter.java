package com.example.akash.videoencrptionexa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import fr.maxcom.http.LocalSingleHttpServer;

/**
 * Created by akash on 21/5/16.
 */

public class Encrypter {
    private final static int DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024;
    private final static String ALGORITHM_VIDEO_ENCRYPTOR = "AES/CBC/PKCS5Padding";

    @SuppressWarnings("resource")
    public static void encrypt(SecretKey key, AlgorithmParameterSpec paramSpec, InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            Cipher c = Cipher.getInstance(ALGORITHM_VIDEO_ENCRYPTOR);
            c.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            out = new CipherOutputStream(out, c);
            int count;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
        } finally {
            out.close();
        }
    }
    @SuppressWarnings("resource")
    public static String decrypt(SecretKey key, AlgorithmParameterSpec paramSpec, InputStream in,String encrypted_path )
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        LocalSingleHttpServer mserver;
        String path;
        mserver= new LocalSingleHttpServer();
        try {
            Cipher c = Cipher.getInstance(ALGORITHM_VIDEO_ENCRYPTOR);
            c.init(Cipher.DECRYPT_MODE, key, paramSpec);
          //  out = new CipherOutputStream(out, c);
            int count;

            mserver.setCipher(c);
            mserver.start();
            path=mserver.getURL(encrypted_path);

//            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
//            while ((count = in.read(buffer)) >= 0) {
//                out.write(buffer, 0, count);
//            }
        }
        finally {
//            out.close();
        }




        return path;
    }

}