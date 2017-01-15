package org.github.codeswarm.model;

import java.util.Collection;

public interface HistoryRepository {

   Collection<Commit> getHistory();
}
