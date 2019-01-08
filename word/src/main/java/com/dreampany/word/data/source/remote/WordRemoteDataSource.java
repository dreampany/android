package com.dreampany.word.data.source.remote;

import android.graphics.Bitmap;

import com.annimon.stream.Stream;
import com.dreampany.frame.data.model.State;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.network.NetworkManager;
import com.dreampany.word.api.wordnik.WordnikManager;
import com.dreampany.word.api.wordnik.WordnikWord;
import com.dreampany.word.data.enums.ItemState;
import com.dreampany.word.data.enums.ItemSubstate;
import com.dreampany.word.data.enums.ItemSubtype;
import com.dreampany.word.data.misc.WordMapper;
import com.dreampany.word.data.model.Word;
import com.dreampany.word.data.source.api.WordDataSource;
import com.dreampany.word.misc.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Maybe;
import timber.log.Timber;

/**
 * Created by Hawladar Roman on 2/9/18.
 * Dreampany Ltd
 * dreampanymail@gmail.com
 */
@Singleton
public class WordRemoteDataSource implements WordDataSource {

    private final NetworkManager network;
    private final WordMapper mapper;
    private final WordnikManager wordnik;

    public WordRemoteDataSource(NetworkManager network,
                                WordMapper mapper,
                                WordnikManager wordnik) {
        this.network = network;
        this.mapper = mapper;
        this.wordnik = wordnik;
    }

    @Override
    public boolean hasState(Word word, ItemState state) {
        return false;
    }

    @Override
    public boolean hasState(Word word, ItemState state, ItemSubstate substate) {
        return false;
    }

    @Override
    public int getStateCount(ItemState state, ItemSubstate substate) {
        return 0;
    }

    @Override
    public List<State> getStates(Word word) {
        return null;
    }

    @Override
    public List<State> getStates(Word word, ItemState state) {
        return null;
    }

    @Override
    public long putItem(Word word, ItemState state) {
        return 0;
    }

    @Override
    public long putItem(Word word, ItemState state, ItemSubstate substate) {
        return 0;
    }

    @Override
    public long putItem(Word word, ItemState state, boolean replaceable) {
        return 0;
    }

    @Override
    public long putState(Word word, ItemState state) {
        return 0;
    }

    @Override
    public long putState(Word word, ItemState state, ItemSubstate substate) {
        return 0;
    }

    @Override
    public Maybe<Long> putItemRx(Word word, ItemState state) {
        return null;
    }

    @Override
    public Maybe<Long> putItemRx(Word word, ItemState state, ItemSubstate substate) {
        return null;
    }

    @Override
    public Maybe<Long> putStateRx(Word word, ItemState state) {
        return null;
    }

    @Override
    public Word getTodayItem() {
        return null;
    }

    @Override
    public Maybe<Word> getTodayItemRx() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Maybe<Boolean> isEmptyRx() {
        return Maybe.fromCallable(this::isEmpty);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Maybe<Integer> getCountRx() {
        return null;
    }

    @Override
    public boolean isExists(Word word) {
        return false;
    }

    @Override
    public Maybe<Boolean> isExistsRx(Word word) {
        return null;
    }

    @Override
    public long putItem(Word word) {
        return 0;
    }

    @Override
    public Maybe<Long> putItemRx(Word word) {
        return null;
    }

    @Override
    public List<Long> putItems(List<Word> words) {
        return null;
    }

    @Override
    public Maybe<List<Long>> putItemsRx(List<Word> words) {
        return null;
    }

    @Override
    public Word getItem(long id) {
        return null;
    }

    @Override
    public Maybe<Word> getItemRx(long id) {
        return null;
    }

    @Override
    public List<Word> getItems() {
        return null;
    }

    @Override
    public Maybe<List<Word>> getItemsRx() {
        return null;
    }

    @Override
    public List<Word> getItems(int limit) {
        return null;
    }

    @Override
    public Maybe<List<Word>> getItemsRx(int limit) {
        return null;
    }

    @Override
    public Word getItem(String word) {
        WordnikWord item = wordnik.getWord(word, Constants.Limit.WORD_RESOLVE);
        Timber.v("Wordnik Result %s", item.getWord());
        return mapper.toItem(word, item, true);
    }

    @Override
    public Maybe<Word> getItemRx(String word) {
        return Maybe.fromCallable(() -> getItem(word));
    }

    @Override
    public Maybe<List<Word>> getSearchItemsRx(String query) {
        return null;
    }

    @Override
    public Maybe<List<Word>> getSearchItemsRx(String query, int limit) {
        return Maybe.fromCallable(() -> {
            List<WordnikWord> items = wordnik.search(query, limit);
            List<Word> result = new ArrayList<>();
            if (!DataUtil.isEmpty(items)) {
                Stream.of(items).forEach(word -> result.add(mapper.toItem(word, false)));
            }
            return result;
        });
    }

    @Override
    public List<Word> getCommonItems() {
        return null;
    }

    @Override
    public List<Word> getAlphaItems() {
        return null;
    }

    @Override
    public Maybe<List<Word>> getItemsRx(Bitmap bitmap) {
        return null;
    }
}
