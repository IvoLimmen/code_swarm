package org.github.gitswarm.model;

import java.util.List;

public interface HistoryRepository {

   List<Commit> getHistory();
}
