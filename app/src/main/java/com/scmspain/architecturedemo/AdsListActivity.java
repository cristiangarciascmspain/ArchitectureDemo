package com.scmspain.architecturedemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.BaseKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.scmspain.adslistlibrary.AdsListPresenter;

import java.util.ArrayList;


public class AdsListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ads_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements AdsListPresenter.AdsListView<String> {

        private final AdsListPresenter<String,String> presenter;

        private class Interactor implements AdsListPresenter.AdsListInteractor<String,String> {
            @Override
            public void setNewSearch(String s) {

            }

            @Override
            public void restoreSearch(String s, int nAdsAlreadyLoaded) {

            }

            @Override
            public ArrayList<String> loadAds() {
                return null;
            }

            @Override
            public boolean shouldLoadMoreOnShow(int position) {
                return false;
            }
        }

        public PlaceholderFragment() {
            presenter = new AdsListPresenter<String, String>(new Interactor());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ads_list, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            presenter.start(this);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            presenter.stop();
        }

        @Override
        public void showLoading() {
            Button button = new Button(getActivity());
            button.setOnClickListener(v -> Toast.makeText(getActivity(),"PRUEBA",Toast.LENGTH_SHORT));
        }

        @Override
        public void showNetworkError() {

        }

        @Override
        public void showContentList() {

        }

        @Override
        public void addContent(ArrayList contentList) {

        }

        @Override
        public void showCeroResults() {

        }

        @Override
        public void addTryAgainRow() {

        }

        @Override
        public void removeTryAgainRow() {

        }

        @Override
        public void addLoadingMoreRow() {

        }

        @Override
        public void removeLoadingMoreRow() {

        }
    }
}
