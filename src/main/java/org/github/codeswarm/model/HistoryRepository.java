package org.github.codeswarm.model;

import java.util.Collection;
import org.github.codeswarm.FileEvent;

public interface HistoryRepository {

   Collection<FileEvent> getHistory();
}
