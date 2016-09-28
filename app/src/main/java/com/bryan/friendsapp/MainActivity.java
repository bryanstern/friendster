package com.bryan.friendsapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func0;

public class MainActivity extends AppCompatActivity implements Contract.ChromeView, Contract.PresenterProvider {

    @Bind(R.id.pager)
    ViewPager viewPager;
    @Bind(R.id.pager_title_strip)
    PagerTitleStrip pagerTitleStrip;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    private Contract.Presenter presenter;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setPresenter(presenter());

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach(this);
    }

    @Override
    public Observable<Void> createRandomPerson() {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                return RxView.clicks(floatingActionButton);
            }
        });
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
        presenter.attach(this);
    }

    @Override
    public Contract.Presenter presenter() {
        if (this.presenter == null) {
            this.presenter = new Presenter();
        }

        return this.presenter;
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AllPeopleFragment.newInstance();
            }

            return FriendsFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "People" : "Friends";
        }
    }
}
