package org.github.gitswarm.avatar;

public class AvatarFetcherChainer implements AvatarFetcher {

   private final AvatarFetcher first;
   private final AvatarFetcher second;
   
   public AvatarFetcherChainer(AvatarFetcher first, AvatarFetcher second) {
      this.first = first;
      this.second = second;
   }   
   
   @Override
   public String fetchUserImage(String username) {
      String result = this.first.fetchUserImage(username);
      
      if (result == null) {
         result = this.second.fetchUserImage(username);
      }
      
      return result;
   }

   @Override
   public int getSize() {
      int result = this.first.getSize();
      
      if (result == 0) {
         result = this.second.getSize();
      }
      
      return result;
   }
}
