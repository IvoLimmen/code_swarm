package org.github.codeswarm;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import org.github.codeswarm.avatar.AvatarFetcher;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLQueueLoader implements Runnable {

   private final String fullFilename;
   private BlockingQueue<FileEvent> queue;
   boolean isXMLSorted;
   private Set<String> peopleSeen = new TreeSet<>();
   private EndOfFileEvent endOfFileEvent;
   //used to ensure that input is sorted when we're told it is
   private long maximumDateSeenSoFar = 0;
   private final AvatarFetcher avatarFetcher;

   public XMLQueueLoader(String fullFilename, BlockingQueue<FileEvent> queue, boolean isXMLSorted, EndOfFileEvent endOfFileEvent, AvatarFetcher avatarFetcher) {
      this.fullFilename = fullFilename;
      this.queue = queue;
      this.isXMLSorted = isXMLSorted;
      this.endOfFileEvent = endOfFileEvent;
      this.avatarFetcher = avatarFetcher;
   }

   @Override
   public void run() {
      XMLReader reader = null;
      try {
         reader = XMLReaderFactory.createXMLReader();
      }
      catch (SAXException e) {
         System.out.println("Couldn't find/create an XML SAX Reader");
         System.exit(1);
      }

      reader.setContentHandler(new DefaultHandler() {
         public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
            if (name.equals("event")) {
               String eventFilename = atts.getValue("filename");
               String eventDatestr = atts.getValue("date");
               long eventDate = Long.parseLong(eventDatestr);

               //It's difficult for the user to tell that they're missing events,
               //so we should crash in this case
               if (isXMLSorted) {
                  if (eventDate < maximumDateSeenSoFar) {
                     System.out.println("Input not sorted, you must set IsInputSorted to false in your config file");
                     System.exit(1);
                  } else {
                     maximumDateSeenSoFar = eventDate;
                  }
               }

               String eventAuthor = atts.getValue("author");
               // int eventLinesAdded = atts.getValue( "linesadded" );
               // int eventLinesRemoved = atts.getValue( "linesremoved" );

               FileEvent evt = new FileEvent(eventDate, eventAuthor, "", eventFilename);

               //We want to pre-fetch images to minimize lag as images are loaded
               if (!peopleSeen.contains(eventAuthor)) {
                  avatarFetcher.fetchUserImage(eventAuthor);
                  peopleSeen.add(eventAuthor);
               }

               try {
                  queue.put(evt);
               }
               catch (InterruptedException e) {
                  System.out.println("Interrupted while trying to put into eventsQueue");
                  System.exit(1);
               }
            }
         }

         public void endDocument() {
            endOfFileEvent.endOfFile();
         }
      });
      try {
         reader.parse(fullFilename);
      }
      catch (Exception e) {
         System.out.println("Error parsing xml:");
         System.exit(1);
      }
   }
}
