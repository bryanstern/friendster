package com.bryan.friendsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxrelay.PublishRelay;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    private final PublishRelay<Person> clickPersonRelay = PublishRelay.create();
    private List<Person> personList = Collections.emptyList();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPerson(personList.get(position));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void personList(List<Person> personList) {
        this.personList = personList;
        notifyDataSetChanged();
    }

    public Observable<Person> peopleClicks() {
        return clickPersonRelay.asObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Person person;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickPersonRelay.call(person);
                }
            });
        }

        void setPerson(Person person) {
            this.person = person;
            ((TextView) super.itemView).setText(person.toString());
        }
    }
}
