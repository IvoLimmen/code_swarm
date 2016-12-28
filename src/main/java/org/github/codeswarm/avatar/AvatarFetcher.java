package org.github.codeswarm.avatar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AvatarFetcher {

   public int size;

   public AvatarFetcher() {
   }
   
   protected static String getFilename(String key) {
      return "image_cache/" + key;
   }

   protected static boolean imageCached(String key) {
      return new File(getFilename(key)).exists();
   }

   protected static String getImage(String key, URL url) {
      String filename = getFilename(key);
      if (!imageCached(key)) {
         boolean successful = fetchImage(filename, url);
         if (!successful) {
            return null;
         }
      }

      return filename;
   }

   protected static boolean fetchImage(String filename, URL url) {
      try {
         new File("image_cache").mkdirs();
         URLConnection con = url.openConnection();
         try (InputStream input = con.getInputStream(); FileOutputStream output = new FileOutputStream(filename)) {

            int length = con.getContentLength();
            if (length == -1) {
               //read until exhausted
               while (true) {
                  int val = input.read();
                  if (val == -1) {
                     break;
                  }
                  output.write(input.read());
               }
            } else {
               //read length bytes
               for (int i = 0; i < length; i++) {
                  output.write(input.read());
               }
            }

         }
         return true;
      }
      catch (IOException e) {
         return false;
      }
   }

   //these two methods taken from http://en.gravatar.com/site/implement/java
   private static String hex(byte[] array) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < array.length; ++i) {
         sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
   }

   protected static String md5Hex(String message) {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         return hex(md.digest(message.getBytes("CP1252")));
      }
      catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      }
      return null;
   }
   
   public void setSize(int size) {
      this.size = size;
   }

   public int getSize() {
      return size;
   }

   public String fetchUserImage(String username) {
      throw new RuntimeException("Override fetchUserImage in your Avatar Fetcher");
   }

}
