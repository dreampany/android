package com.dreampany.word.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.dreampany.frame.data.enums.UiState;
import com.dreampany.frame.data.model.Response;
import com.dreampany.frame.misc.ActivityScope;
import com.dreampany.frame.misc.exception.EmptyException;
import com.dreampany.frame.misc.exception.ExtraException;
import com.dreampany.frame.misc.exception.MultiException;
import com.dreampany.frame.ui.activity.BaseActivity;
import com.dreampany.frame.ui.adapter.SmartAdapter;
import com.dreampany.frame.ui.callback.SearchViewCallback;
import com.dreampany.frame.ui.fragment.BaseMenuFragment;
import com.dreampany.frame.ui.listener.OnVerticalScrollListener;
import com.dreampany.frame.util.AndroidUtil;
import com.dreampany.frame.util.ColorUtil;
import com.dreampany.frame.util.DataUtil;
import com.dreampany.frame.util.MenuTint;
import com.dreampany.frame.util.TextUtil;
import com.dreampany.frame.util.ViewUtil;
import com.dreampany.word.R;
import com.dreampany.word.data.model.Definition;
import com.dreampany.word.data.model.Word;
import com.dreampany.word.databinding.ContentDefinitionBinding;
import com.dreampany.word.databinding.ContentMediumWordBinding;
import com.dreampany.word.databinding.ContentRecyclerBinding;
import com.dreampany.word.databinding.ContentTopStatusBinding;
import com.dreampany.word.databinding.FragmentHomeBinding;
import com.dreampany.word.ui.activity.ToolsActivity;
import com.dreampany.word.ui.adapter.WordAdapter;
import com.dreampany.word.ui.enums.UiSubtype;
import com.dreampany.word.ui.enums.UiType;
import com.dreampany.word.ui.model.UiTask;
import com.dreampany.word.ui.model.WordItem;
import com.dreampany.word.vm.SearchViewModel;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import cz.kinst.jakub.view.StatefulLayout;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import timber.log.Timber;

/**
 * Created by Hawladar Roman on 6/20/2018.
 * BJIT Group
 * hawladar.roman@bjitgroup.com
 */
@ActivityScope
public class HomeFragment extends BaseMenuFragment
        implements SmartAdapter.Callback<WordItem>,
        MaterialSearchView.OnQueryTextListener,
        MaterialSearchView.SearchViewListener {

    private final String SEARCH = "search";
    private final String EMPTY = "empty";

    @Inject
    ViewModelProvider.Factory factory;
    private FragmentHomeBinding binding;
    private ContentTopStatusBinding bindStatus;
    private ContentRecyclerBinding bindRecycler;
    private ContentMediumWordBinding bindWord;
    private ContentDefinitionBinding bindDef;

    private OnVerticalScrollListener scroller;
    private MaterialSearchView searchView;

    SearchViewModel searchVm;
    WordAdapter adapter;

    String query;

    @Inject
    public HomeFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public int getMenuId() {
        return R.menu.menu_home;
    }

    @Override
    public int getSearchMenuItemId() {
        return R.id.item_search;
    }

    @Override
    protected void onStartUi(@Nullable Bundle state) {
        initView();
        initRecycler();
        AndroidUtil.initTts(getApp());
        searchVm.loadLastSearchWord(true);
    }

    @Override
    protected void onStopUi() {
        AndroidUtil.stopTts();
        processUiState(UiState.HIDE_PROGRESS);
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
    }

    @Override
    public void onMenuCreated(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onMenuCreated(menu, inflater);
        BaseActivity activity = getParent();

        if (activity instanceof SearchViewCallback) {
            SearchViewCallback searchCallback = (SearchViewCallback) activity;
            searchView = searchCallback.getSearchView();
            MenuItem searchItem = getSearchMenuItem();
            initSearchView(searchView, searchItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                //searchView.open(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.toggle_definition:
                toggleDefinition();
                break;
            case R.id.button_favorite:
                searchVm.toggleFavorite(binding.getItem().getItem());
                break;
            case R.id.fab:
                processFabAction();
                break;
            case R.id.imageSpeak:
                speak();
                break;
            case R.id.text_word:
                openUi(bindWord.getItem().getItem());
                break;
        }
    }

    @Override
    public boolean onItemClick(View view, int position) {
        if (position != RecyclerView.NO_POSITION) {
            WordItem item = adapter.getItem(position);
            openUi(item.getItem());
            return true;
        }
        return false;
    }

    @Override
    public void onSearchViewShown() {
        toSearchMode();
    }

    @Override
    public void onSearchViewClosed() {
        toScanMode();
    }

    @Override
    public boolean onQueryTextSubmit(@NotNull String query) {
        Timber.v("onQueryTextSubmit %s", query);
        searchVm.find(query, true);
        return super.onQueryTextSubmit(query);
    }

    @Override
    public boolean onQueryTextChange(@NotNull String newText) {
        Timber.v("onQueryTextChange %s", newText);
        this.query = newText.toLowerCase();
        return super.onQueryTextChange(newText);
    }

    @Override
    public List<WordItem> getVisibleItems() {
        return adapter.getVisibleItems();
    }

    @Override
    public WordItem getVisibleItem() {
        return adapter.getVisibleItem();
    }

    @Override
    public List<WordItem> getItems() {
        return null;
    }

    @Override
    public boolean getEmpty() {
        return false;
    }

    @Override
    public boolean hasBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        }
        return super.hasBackPressed();
    }

    private void initView() {
        setTitle(R.string.home);
        binding = (FragmentHomeBinding) super.binding;
        bindStatus = binding.layoutTopStatus;
        bindWord = binding.layoutWord;
        bindRecycler = binding.layoutRecycler;
        bindDef = bindWord.layoutDefinition;

        binding.stateful.setStateView(SEARCH, LayoutInflater.from(getContext()).inflate(R.layout.item_search, null));
        binding.stateful.setStateView(EMPTY, LayoutInflater.from(getContext()).inflate(R.layout.item_empty, null));
        processUiState(UiState.SEARCH);
        //ViewUtil.setText(this, R.id.text_empty, R.string.empty_search_words);

        ViewUtil.setSwipe(binding.layoutRefresh, this);
        bindDef.toggleDefinition.setOnClickListener(this);
        bindWord.buttonFavorite.setOnClickListener(this);
        bindWord.textWord.setOnClickListener(this);
        bindWord.imageSpeak.setOnClickListener(this);
        binding.fab.setOnClickListener(this);


        searchVm = ViewModelProviders.of(this, factory).get(SearchViewModel.class);
        searchVm.setUiCallback(this);
        searchVm.observeUiState(this, this::processUiState);
        searchVm.observeOutputsOfString(this, this::processResponseOfString);
        searchVm.observeOutputs(this, this::processResponse);
        searchVm.observeOutput(this, this::processSingleResponse);
    }

    private void initRecycler() {
        binding.setItems(new ObservableArrayList<>());
        adapter = new WordAdapter(this);
        adapter.setStickyHeaders(false);
        scroller = new OnVerticalScrollListener() {
            @Override
            public void onScrollingAtEnd() {

            }
        };
        ViewUtil.setRecycler(
                adapter,
                bindRecycler.recycler,
                new SmoothScrollLinearLayoutManager(getContext()),
                new FlexibleItemDecoration(getContext())
                        .addItemViewType(R.layout.item_word, searchVm.getItemOffset())
                        .withEdge(true),
                null,
                scroller,
                null
        );
    }

    private void initSearchView(MaterialSearchView searchView, MenuItem searchItem) {
        MenuTint.colorMenuItem(searchItem, ColorUtil.getColor(getContext(), R.color.material_white), null);
        searchView.setMenuItem(searchItem);
        searchView.setSubmitOnClick(true);

        searchView.setOnSearchViewListener(this);
        searchView.setOnQueryTextListener(this);

        searchVm.suggests(false);
    }

    private void processUiState(UiState state) {
        switch (state) {
            case SHOW_PROGRESS:
                if (!binding.layoutRefresh.isRefreshing()) {
                    binding.layoutRefresh.setRefreshing(true);
                }
                break;
            case HIDE_PROGRESS:
                if (binding.layoutRefresh.isRefreshing()) {
                    binding.layoutRefresh.setRefreshing(false);
                }
                break;
            case OFFLINE:
                bindStatus.layoutExpandable.expand();
                break;
            case ONLINE:
                bindStatus.layoutExpandable.collapse();
                break;
            case EXTRA:
                processUiState(adapter.isEmpty() ? UiState.EMPTY : UiState.CONTENT);
                break;
            case SEARCH:
                binding.stateful.setState(SEARCH);
                break;
            case EMPTY:
                binding.stateful.setState(SEARCH);
                break;
            case ERROR:
                break;
            case CONTENT:
                binding.stateful.setState(StatefulLayout.State.CONTENT);
                break;
        }
    }

    public void processResponseOfString(Response<List<String>> response) {
        if (response instanceof Response.Progress) {
            Response.Progress result = (Response.Progress) response;
            processProgress(result.getLoading());
        } else if (response instanceof Response.Failure) {
            Response.Failure result = (Response.Failure) response;
            processFailure(result.getError());
        } else if (response instanceof Response.Result) {
            Response.Result<List<String>> result = (Response.Result<List<String>>) response;
            processSuccessOfString(result.getType(), result.getData());
        }
    }

    public void processResponse(Response<List<WordItem>> response) {
        if (response instanceof Response.Progress) {
            Response.Progress result = (Response.Progress) response;
            processProgress(result.getLoading());
        } else if (response instanceof Response.Failure) {
            Response.Failure result = (Response.Failure) response;
            processFailure(result.getError());
        } else if (response instanceof Response.Result) {
            Response.Result<List<WordItem>> result = (Response.Result<List<WordItem>>) response;
            processSuccess(result.getType(), result.getData());
        }
    }

    public void processSingleResponse(Response<WordItem> response) {
        if (response instanceof Response.Progress) {
            Response.Progress result = (Response.Progress) response;
            processProgress(result.getLoading());
        } else if (response instanceof Response.Failure) {
            Response.Failure result = (Response.Failure) response;
            processFailure(result.getError());
        } else if (response instanceof Response.Result) {
            Response.Result<WordItem> result = (Response.Result<WordItem>) response;
            processSingleSuccess(result.getData());
        }
    }

    private void toScanMode() {
        //query = null;
        //binding.fab.setImageResource(R.drawable.ic_filter_center_focus_black_24dp);
    }

    private void toSearchMode() {
        binding.fab.setImageResource(R.drawable.ic_search_black_24dp);
    }

    private void processFabAction() {
        if (searchView.isSearchOpen()) {
            searchView.clearFocus();
            searchVm.find(query, true);
            return;
        }
    }

    private void processProgress(boolean loading) {
        if (loading) {
            searchVm.updateUiState(UiState.SHOW_PROGRESS);
        } else {
            searchVm.updateUiState(UiState.HIDE_PROGRESS);
        }
    }

    private void processFailure(Throwable error) {
        if (error instanceof IOException || error.getCause() instanceof IOException) {
            searchVm.updateUiState(UiState.OFFLINE);
        } else if (error instanceof EmptyException) {
            searchVm.updateUiState(UiState.EMPTY);
        } else if (error instanceof ExtraException) {
            searchVm.updateUiState(UiState.EXTRA);
        } else if (error instanceof MultiException) {
            for (Throwable e : ((MultiException) error).getErrors()) {
                processFailure(e);
            }
        }
    }

    private void processSuccessOfString(Response.Type type, List<String> items) {
        Timber.v("Result Type[%s] Size[%s]", type.name(), items.size());

        if (type == Response.Type.SUGGESTS) {
            String[] result = DataUtil.toStringArray(items);
            searchView.setSuggestions(result);
            return;
        }
    }

    private void processSuccess(Response.Type type, List<WordItem> items) {
        Timber.v("Result Type[%s] Size[%s]", type.name(), items.size());

        if (type == Response.Type.SUGGESTS) {
            if (!DataUtil.isEmpty(items)) {
                String[] suggests = new String[items.size()];
                for (int index = 0; index < items.size(); index++) {
                    suggests[index] = items.get(index).getItem().getId();
                }
                searchView.setSuggestions(suggests);
            }
            return;
        }
        adapter.clear();
        adapter.addItems(items);
        ex.postToUi(() -> processUiState(UiState.EXTRA), 500);
    }

    private void processSingleSuccess(WordItem item) {
        Timber.v("Result Single Word[%s]", item.getItem().getId());
        binding.setItem(item);
        processDefinitions(item.getItem().getDefinitions());
        processUiState(UiState.CONTENT);
    }

    private void processDefinitions(List<Definition> definitions) {
        StringBuilder singleBuilder = new StringBuilder();
        StringBuilder multipleBuilder = new StringBuilder();

        if (!DataUtil.isEmpty(definitions)) {
            for (int index = 0; index < definitions.size(); index++) {
                Definition def = definitions.get(index);
                if (index == 0) {
                    singleBuilder
                            .append(def.getPartOfSpeech())
                            .append(DataUtil.SEMI)
                            .append(DataUtil.SPACE)
                            .append(def.getText());
                    multipleBuilder
                            .append(def.getPartOfSpeech())
                            .append(DataUtil.SEMI)
                            .append(DataUtil.SPACE)
                            .append(def.getText());
                    continue;
                }
                multipleBuilder
                        .append(DataUtil.NewLine2)
                        .append(def.getPartOfSpeech())
                        .append(DataUtil.SEMI)
                        .append(DataUtil.SPACE)
                        .append(def.getText());
            }
        }

        if (singleBuilder.length() > 0) {
            String text = singleBuilder.toString();
            bindDef.textSingleDefinition.setText(text);
            setSpan(bindDef.textSingleDefinition, text, null);

            text = multipleBuilder.toString();
            bindDef.textMultipleDefinition.setText(text);
            setSpan(bindDef.textMultipleDefinition, text, null);
            bindDef.layoutDefinition.setVisibility(View.VISIBLE);

            if (definitions.size() > 1) {
                bindDef.toggleDefinition.setVisibility(View.VISIBLE);
            } else {
                bindDef.toggleDefinition.setVisibility(View.GONE);
            }

        } else {
            bindDef.layoutDefinition.setVisibility(View.GONE);
        }
    }

    private void setSpan(TextView view, String text, String bold) {
        List<String> items = TextUtil.getWords(text);
        TextUtil.setSpan(view, items, bold, this::searchWord, this::searchWord);
    }

    private void toggleDefinition() {
        if (bindDef.layoutSingleExpandable.isExpanded()) {
            bindDef.layoutSingleExpandable.collapse(true);
            bindDef.layoutMultipleExpandable.expand(true);
            bindDef.toggleDefinition.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        } else {
            bindDef.layoutMultipleExpandable.collapse(true);
            bindDef.layoutSingleExpandable.expand(true);
            bindDef.toggleDefinition.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        }
    }

    private void searchWord(String word) {
        searchView.clearFocus();
        searchVm.find(word, true);
    }

    private void openUi(Word item) {
        UiTask<Word> task = new UiTask<>(false);
        task.setInput(item);
        task.setUiType(UiType.WORD);
        task.setSubtype(UiSubtype.VIEW);
        openActivity(ToolsActivity.class, task);
    }

    private void speak() {
        WordItem item = bindWord.getItem();
        if (item != null) {
            AndroidUtil.speak(item.getItem().getId());
        }
    }
}
