package com.bryan.friendsapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AllPeopleFragment extends Fragment implements Contract.AllPeopleView{
    private Contract.Presenter presenter;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private PersonAdapter adapter;

    public AllPeopleFragment() {
        // Required empty public constructor
    }

    public static AllPeopleFragment newInstance() {
        AllPeopleFragment fragment = new AllPeopleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setPresenter(((Contract.PresenterProvider) activity).presenter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);

        adapter = new PersonAdapter();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Subscription sub = presenter.people()
                .flatMap(new Func1<List<Person>, Observable<List<Person>>>() {
                    @Override
                    public Observable<List<Person>> call(List<Person> persons) {
                        return Observable.from(persons)
                                .toSortedList(new Func2<Person, Person, Integer>() {
                                    @Override
                                    public Integer call(Person x, Person y) {
                                        return x.number() - y.number();
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(List<Person> persons) {
                        adapter.personList(persons);
                    }
                });
        subscriptions.add(sub);

        presenter.attach(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscriptions.clear();
        presenter.detach(this);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Observable<Person> friendObs() {
        return adapter.peopleClicks();
    }
}
