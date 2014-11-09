package com.scmspain.adslistlibrary;

import java.util.ArrayList;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdsListPresenter<Search,AdModel> {
    private Scheduler subscribeOn;
    private Scheduler observeOn;
    private RXAdsListInteractor<Search,AdModel> adsListInteractor;
    private AdsListView<AdModel> nullView = new NullAdsListView();
    private AdsListView<AdModel> view = nullView;

    private boolean loading = false;

    public AdsListPresenter(RXAdsListInteractor<Search,AdModel> adsListInteractor) {
        this.subscribeOn = Schedulers.io();
        this.observeOn = AndroidSchedulers.mainThread();
        this.adsListInteractor = adsListInteractor;
    }
    public AdsListPresenter(RXAdsListInteractor<Search,AdModel> adsListInteractor, Scheduler subscribeOn, Scheduler observeOn) {
        this.subscribeOn = subscribeOn;
        this.observeOn = observeOn;
        this.adsListInteractor = adsListInteractor;
    }

    public void start(AdsListView<AdModel> view) {
        this.view = view;
    }

    public void stop(){
        view = nullView;
    }

    public void setNewSearch(Search search) {
        adsListInteractor.setNewSearch(search);
        rxload();
    }
/*
    private void load() {
        loading = true;
        view.showLoading();
        try {
            ArrayList<AdModel> ads = adsListInteractor.loadAds();
            if (ads!=null && ads.size()>0) {
                view.showContentList();
                view.addContent(ads);
            } else {
                view.showCeroResults();
            }
        } catch (Exception e) {
            view.showNetworkError();
        }
        loading = false;
    }
    private void loadMore() {
        loading = true;
        view.removeTryAgainRow();
        view.addLoadingMoreRow();
        try {
            ArrayList<AdModel> ads = adsListInteractor.loadAds();
            view.removeLoadingMoreRow();
            if (ads.size()>0) {
                view.addContent(ads);
            } else {
                // No more ads to show
            }
        } catch (Exception e) {
            view.removeLoadingMoreRow();
            view.addTryAgainRow();
        }
        loading = false;
    }
*/
    public void rxload() {
        loading = true;
        view.showLoading();
        adsListInteractor
                .loadAds()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe(adModels -> {
                    if (adModels != null && adModels.size() > 0) {
                        view.showContentList();
                        view.addContent(adModels);
                    } else {
                        view.showCeroResults();
                    }
                }, throwable -> {
                    view.showNetworkError();
                    loading = false;
                }, () -> loading = false);
    }
    public void rxloadMore() {
        loading = true;
        view.removeTryAgainRow();
        view.addLoadingMoreRow();
        adsListInteractor
                .loadAds()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe(adModels -> {
                    view.removeLoadingMoreRow();
                    if (adModels != null && adModels.size() > 0) {
                        view.addContent(adModels);
                    } else {
                        // No more ads to show
                    }
                }, throwable -> {
                    view.removeLoadingMoreRow();
                    view.addTryAgainRow();
                    loading = false;
                }, () -> loading = false);
    }

    public void restoreSearch(Search search, int nAdsAlreadyLoaded) {
        adsListInteractor.restoreSearch(search,nAdsAlreadyLoaded);
    }

    public void showingItemAtIndex(int position) {
        if (!loading && adsListInteractor.shouldLoadMoreOnShow(position)) {
            rxloadMore();
        }
    }

    public void retryLoad() {
        rxload();
    }
    public void retryLoadMore() {
        rxloadMore();
    }


    public interface AdsListView<AdModel> {
        void showLoading();
        void showNetworkError();
        void showContentList();
        void addContent(ArrayList<AdModel> contentList);
        void showCeroResults();
        void addTryAgainRow();
        void removeTryAgainRow();
        void addLoadingMoreRow();
        void removeLoadingMoreRow();
    }
    private class NullAdsListView implements AdsListView<AdModel> {
        @Override public void showLoading() {}
        @Override public void showNetworkError() {}
        @Override public void showContentList() {}
        @Override public void addContent(ArrayList<AdModel> contentList) {}
        @Override public void showCeroResults() {}
        @Override public void addTryAgainRow() {}
        @Override public void removeTryAgainRow() {}
        @Override public void addLoadingMoreRow() {}
        @Override public void removeLoadingMoreRow() {}
    }

    public interface AdsListInteractor<Search,AdModel> {
        public void setNewSearch(Search search);
        public void restoreSearch(Search search, int nAdsAlreadyLoaded);
        public ArrayList<AdModel> loadAds();
        public boolean shouldLoadMoreOnShow(int position);
    }

    public interface RXAdsListInteractor<Search,AdModel> {
        public void setNewSearch(Search search);
        public void restoreSearch(Search search, int nAdsAlreadyLoaded);
        public Observable<ArrayList<AdModel>> loadAds();
        public boolean shouldLoadMoreOnShow(int position);
    }

/*
    public void addFavorite(AdModel adModel) {
        AdsListWithFavoritesInteractor<Search,AdModel> adsListWithFavoritesInteractor;
        adsListWithFavoritesInteractor.addFavorite(adModel);
        view.setFavorite(adModel);
    }
    public interface AdsListWithFavoritesInteractor<Search,AdModel> extends AdsListInteractor<Search,AdModel> {
        public boolean addFavorite(AdModel adModel);
        public boolean removeFavorite(AdModel adModel);
    }
*/
}
