package org.github.gitswarm.avatar;

public interface AvatarFetcher {

   String fetchUserImage(String username);   

   int getSize();
}
