package com.scmspain.adslistlibrary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk=18)
@RunWith(RobolectricTestRunner.class)
public class AdsListPresenterTest {
    private AdsListPresenter.RXAdsListInteractor<String,String> interactor;
    private AdsListPresenter.AdsListView<String> view;
    private AdsListPresenter<String,String> presenter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        interactor = mock(AdsListPresenter.RXAdsListInteractor.class);
        when(interactor.loadAds()).thenReturn(Observable.just(new ArrayList<>()));
        view = mock(AdsListPresenter.AdsListView.class);
        presenter = new AdsListPresenter<>(interactor, AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread());
        presenter.start(view);
    }

    @After
    public void tearDown() {
        presenter.stop();
    }

    @Test
    public void presenterShouldCallShowCeroResultsIfThereIsNoResults() {
        // WHEN
        presenter.setNewSearch(anyString());

        // THEN
        verify(view).showLoading();
        verify(view).showCeroResults();
    }
    @Test
    public void presenterShouldCallShowContentIfThereIsResults() {
        // GIVEN
        ArrayList<String> ads = new ArrayList<String>() {{
            add("one");
        }};
        given(interactor.loadAds()).willReturn(Observable.just(ads));

        // WHEN
        presenter.setNewSearch(anyString());

        // THEN
        verify(view).showLoading();
        verify(view).showContentList();
        verify(view).addContent(ads);
    }
    @Test
    public void presenterShouldCallShowNetworkErrorOnException() {
        // GIVEN
        given(interactor.loadAds()).willReturn(Observable.error(new RuntimeException()));

        // WHEN
        presenter.setNewSearch(anyString());

        // THEN
        verify(view).showLoading();
        verify(view).showNetworkError();
    }
    @Test
    public void presenterShouldShowContentIfRetryWorks() {
        // GIVEN
        ArrayList<String> ads = new ArrayList<String>() {{
            add("one");
        }};
        given(interactor.loadAds()).willReturn(Observable.just(ads));

        // WHEN
        presenter.retryLoad();

        // THEN
        verify(view).showLoading();
        verify(view).showContentList();
        verify(view).addContent(ads);
    }
    @Test
    public void presenterShouldShowAddContentIfRetryLoadMoreWorks() {
        // GIVEN
        ArrayList<String> ads = new ArrayList<String>() {{
            add("one");
        }};
        given(interactor.loadAds()).willReturn(Observable.just(ads));

        // WHEN
        presenter.retryLoadMore();

        // THEN
        verify(view).addLoadingMoreRow();
        verify(view).removeLoadingMoreRow();
        verify(view, never()).showLoading();
        verify(view, never()).showContentList();
        verify(view).addContent(ads);
    }
    @Test
    public void presenterShouldShowRetryIfRetryLoadMoreFails() {
        // GIVEN
        given(interactor.loadAds()).willReturn(Observable.error(new RuntimeException()));

        // WHEN
        presenter.retryLoadMore();

        // THEN
        verify(view).removeTryAgainRow();
        verify(view).addLoadingMoreRow();
        verify(view).removeLoadingMoreRow();
        verify(view, never()).showLoading();
        verify(view, never()).showContentList();
        verify(view).addTryAgainRow();
    }
    @Test
    public void presenterShouldCallLoadNextOnceOnlyAtNewSearch() {
        // WHEN
        presenter.setNewSearch(anyString());

        // THEN
        verify(interactor, times(1)).loadAds();
    }
    @Test
    public void presenterShouldntCallLoadNextIfShouldLoadMoreOnShowReturnsFalse() {
        // GIVEN
        given(interactor.shouldLoadMoreOnShow(0)).willReturn(false);
        presenter.setNewSearch(anyString());

        // WHEN
        presenter.showingItemAtIndex(0);

        // THEN
        verify(interactor, times(1)).loadAds();
    }
    @Test
    public void presenterShouldCallLoadNextIfShouldLoadMoreOnShowReturnsTrue() {
        // GIVEN
        given(interactor.shouldLoadMoreOnShow(0)).willReturn(true);
        presenter.setNewSearch(anyString());

        // WHEN

        presenter.showingItemAtIndex(0);

        // THEN
        verify(interactor, times(2)).loadAds();
    }

    @Test
    public void presenterShoulWorkAsyncronusly() {
        ArrayList<String> ads = new ArrayList<String>() {{
            add("one");
        }};
        given(interactor.loadAds()).willReturn(Observable.just(ads).delay(100,TimeUnit.MILLISECONDS));

        AdsListPresenter<String, String> presenter = new AdsListPresenter<>(interactor, AndroidSchedulers.mainThread(), Schedulers.io());
        presenter.start(view);

        // WHEN
        presenter.setNewSearch(anyString());

        // THEN
        verify(view, times(0)).showContentList();
        verify(view, timeout(1000).times(1)).showContentList();
    }
}
