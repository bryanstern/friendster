package com.bryan.friendsapp;

import com.jakewharton.rxrelay.BehaviorRelay;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class Presenter implements Contract.Presenter {
    private final CompositeSubscription allPeopleViewSub = new CompositeSubscription();
    private final CompositeSubscription myFriendsViewSub = new CompositeSubscription();
    private final CompositeSubscription chromeViewSub = new CompositeSubscription();

    private final BehaviorRelay<List<Person>> peopleRelay;

    public Presenter() {
        List<Person> persons = new ArrayList<>();
        persons.add(Person.createRandom());
        this.peopleRelay = BehaviorRelay.create(persons);
    }

    @Override
    public void attach(Contract.AllPeopleView view) {
        if (!allPeopleViewSub.hasSubscriptions()) {
            Subscription subscription = view.friendObs()
                    .withLatestFrom(peopleRelay, new Func2<Person, List<Person>, List<Person>>() {
                        @Override
                        public List<Person> call(Person person, List<Person> persons) {
                            List<Person> newList = new ArrayList<>(persons);
                            newList.remove(person);
                            newList.add(person.toFriend());
                            return newList;
                        }
                    })
                    .subscribe(peopleRelay);
            allPeopleViewSub.add(subscription);
        }
    }

    @Override
    public void detach(Contract.AllPeopleView view) {
        allPeopleViewSub.clear();
    }

    @Override
    public void attach(Contract.MyFriendsView view) {
        if (!myFriendsViewSub.hasSubscriptions()) {
            Subscription subscription = view.defriendObs()
                    .withLatestFrom(peopleRelay, new Func2<Person, List<Person>, List<Person>>() {
                        @Override
                        public List<Person> call(Person person, List<Person> persons) {
                            List<Person> newList = new ArrayList<>(persons);
                            newList.remove(person);
                            newList.add(person.toEnemy());
                            return newList;
                        }
                    })
                    .subscribe(peopleRelay);
            myFriendsViewSub.add(subscription);
        }
    }

    @Override
    public void detach(Contract.MyFriendsView view) {
        myFriendsViewSub.clear();
    }

    @Override
    public void attach(Contract.ChromeView view) {
        if (!chromeViewSub.hasSubscriptions()) {
            Subscription subscription = view.createRandomPerson()
                    .withLatestFrom(peopleRelay, new Func2<Void, List<Person>, List<Person>>() {
                        @Override
                        public List<Person> call(Void v, List<Person> persons) {
                            List<Person> newList = new ArrayList<>(persons);
                            newList.add(Person.createRandom());
                            return newList;
                        }
                    })
                    .subscribe(peopleRelay);
            chromeViewSub.add(subscription);
        }
    }

    @Override
    public void detach(Contract.ChromeView view) {
        chromeViewSub.clear();
    }

    @Override
    public Observable<List<Person>> people() {
        return peopleRelay;
    }

    @Override
    public Observable<List<Person>> friends() {
        return peopleRelay
                .flatMap(new Func1<List<Person>, Observable<List<Person>>>() {
                    @Override
                    public Observable<List<Person>> call(List<Person> persons) {
                        return Observable.from(persons)
                                .filter(new Func1<Person, Boolean>() {
                                    @Override
                                    public Boolean call(Person person) {
                                        return person.friend();
                                    }
                                })
                                .toList();
                    }
                });
    }

}
