package org.github.codeswarm.avatar;

import java.io.File;

public class LocalAvatar extends AvatarFetcher {

   public String localAvatarDirectory = "";

   public String localAvatarDefaultPic = null;

   public LocalAvatar() {
      super();
   }

   public void setLocalAvatarDirectory(String localAvatarDirectory) {
      this.localAvatarDirectory = localAvatarDirectory;
   }

   public void setLocalAvatarDefaultPic(String localAvatarDefaultPic) {
      this.localAvatarDefaultPic = localAvatarDefaultPic;
   }

   @Override
   public String fetchUserImage(String username) {
      String filename = localAvatarDirectory + username + ".png";
      File f = new File(filename);
      if (f.exists()) {
         return filename;
      }
      return localAvatarDirectory + localAvatarDefaultPic;
   }
}
