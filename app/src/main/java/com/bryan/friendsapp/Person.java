package com.bryan.friendsapp;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Random;

@AutoValue
public abstract class Person {
    public abstract String name();
    public abstract boolean friend();
    public abstract int number();

    private static int count = 0;

    public static Person create(@NonNull String name, boolean friend) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        return new AutoValue_Person(name, friend, count++);
    }

    private static final String[] firstName = new String[] {"Emma", "Noah", "Olivia", "Liam", "Sophia", "Mason"};
    private static final String[] lastName = new String[] {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis"};

    public static Person createRandom() {
        Random random = new Random();
        String name = String.format("%s %s",
                firstName[random.nextInt(firstName.length)],
                lastName[random.nextInt(lastName.length)]);

        return create(name, random.nextBoolean());
    }

    public Person toFriend() {
        return new AutoValue_Person(name(), true, number());
    }

    public Person toEnemy() {
        return new AutoValue_Person(name(), false, number());
    }
}
