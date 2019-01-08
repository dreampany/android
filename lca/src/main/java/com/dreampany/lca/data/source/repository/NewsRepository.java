package com.dreampany.lca.data.source.repository;

import com.dreampany.frame.data.source.repository.Repository;
import com.dreampany.frame.misc.Room;
import com.dreampany.frame.misc.Remote;
import com.dreampany.frame.misc.ResponseMapper;
import com.dreampany.frame.misc.RxMapper;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.frame.util.TimeUtil;
import com.dreampany.lca.data.model.News;
import com.dreampany.lca.data.source.api.NewsDataSource;
import com.dreampany.lca.data.source.pref.Pref;
import com.dreampany.lca.misc.Constants;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import io.reactivex.Maybe;

/**
 * Created by Hawladar Roman on 6/22/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
@Singleton
public class NewsRepository extends Repository<Long, News> implements NewsDataSource {

    private final Pref pref;
    private final NewsDataSource local;
    private final NewsDataSource remote;

    @DebugLog
    @Inject
    NewsRepository(RxMapper rx,
                   ResponseMapper rm,
                   Pref pref,
                   @Room NewsDataSource local,
                   @Remote NewsDataSource remote) {
        super( rx, rm);
        this.pref = pref;
        this.local = local;
        this.remote = remote;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public Maybe<Boolean> isEmptyRx() {
        return Maybe.fromCallable(this::isEmpty);
    }

    @Override
    public int getCount() {
        return local.getCount();
    }

    @Override
    public Maybe<Integer> getCountRx() {
        return null;
    }

    @Override
    public boolean isExists(News news) {
        return false;
    }

    @Override
    public Maybe<Boolean> isExistsRx(News news) {
        return null;
    }

    @Override
    public long putItem(News news) {
        return local.putItem(news);
    }

    @Override
    public Maybe<Long> putItemRx(News news) {
        return local.putItemRx(news);
    }

    @Override
    public List<Long> putItems(List<News> news) {
        return local.putItems(news);
    }

    @Override
    public Maybe<List<Long>> putItemsRx(List<News> news) {
        return local.putItemsRx(news);
    }

    @Override
    public News getItem(long id) {
        return null;
    }

    @Override
    public Maybe<News> getItemRx(long id) {
        return null;
    }

    @Override
    public List<News> getItems() {
        return null;
    }

    @Override
    public Maybe<List<News>> getItemsRx() {
        return null;
    }

    @Override
    public List<News> getItems(int limit) {
        return null;
    }

    @Override
    public Maybe<List<News>> getItemsRx(int limit) {
        return null;
    }

    public Maybe<List<News>> getItemsRx(int limit, boolean fresh) {
        Maybe<List<News>> local = this.local.getItemsRx(limit);
        Maybe<List<News>> remote = getRemoteItemsIfRx(limit);
        return concatFirstRx(remote, local);
    }

    private Maybe<List<News>> getRemoteItemsIfRx(int limit) {
        long lastTime = pref.getNewsTime();
        if (TimeUtil.isExpired(lastTime, Constants.Delay.INSTANCE.getNews())) {
            return this.remote.getItemsRx(limit)
                    .filter(items -> !(DataUtil.isEmpty(items)))
                    .doOnSuccess(items -> {
                        rx.compute(putItemsRx(items)).subscribe();
                        pref.commitNewsTime();
                    });
        }
        return null;
    }
}
