package com.github.bikeholik.ads.indexer;

import java.util.Collection;

public interface Indexer {

    <M, D> void index(Collection<DataSample<M, D>> data);
}
