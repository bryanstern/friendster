package com.bryan.friendsapp;

import java.util.List;

import rx.Observable;

/**
 * Created by bryan on 9/26/16.
 */

public class Contract {
    interface View {
        void setPresenter(Presenter presenter);
    }

    interface AllPeopleView extends View {
        Observable<Person> friendObs();
    }

    interface MyFriendsView extends View {
        Observable<Person> defriendObs();
    }

    interface ChromeView extends View {
        Observable<Void> createRandomPerson();
    }

    interface Presenter {
        void attach(AllPeopleView view);
        void detach(AllPeopleView view);

        void attach(MyFriendsView view);
        void detach(MyFriendsView view);

        void attach(ChromeView view);
        void detach(ChromeView view);

        Observable<List<Person>> people();
        Observable<List<Person>> friends();
    }

    interface PresenterProvider {
        Presenter presenter();
    }
}
