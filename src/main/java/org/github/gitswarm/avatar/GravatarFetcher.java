package org.github.gitswarm.avatar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GravatarFetcher extends AvatarFetcher {

   private final Pattern emailPattern = Pattern.compile("<(.*?@.*?\\..*?)>");

   private String gravatarFallback;

   public GravatarFetcher() {
      super();
   }

   public void setGravatarFallback(String gravatarFallback) {
      this.gravatarFallback = gravatarFallback;
   }

   @Override
   public String fetchUserImage(String username) {
      String email = getEmail(username);
      String hash = md5Hex(email);
      try {
         return getImage(hash, new URL("http://www.gravatar.com/avatar/" + hash + "?d=" + gravatarFallback + "&s=" + size));
      }
      catch (MalformedURLException e) {
         return null;
      }
   }

   private String getEmail(String username) {
      Matcher emailMatcher = emailPattern.matcher(username);
      if (emailMatcher.find()) {
         return username.substring(emailMatcher.start(1), emailMatcher.end(1));
      }
      return username;
   }
}
