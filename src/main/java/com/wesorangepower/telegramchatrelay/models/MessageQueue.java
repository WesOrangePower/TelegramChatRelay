package com.wesorangepower.telegramchatrelay.models;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MessageQueue<E> extends AbstractQueue<E>
{
    private final E[] container;
    private int pointer;

    @SuppressWarnings("unchecked")
    public MessageQueue(int size)
    {
        container = (E[]) new Object[size];
    }

    @Override
    public Iterator<E> iterator()
    {
        return new OurIterator();
    }

    @Override
    public boolean offer(E o)
    {
        if (pointer >= container.length)
            pointer = 0;
        container[pointer++] = o;
        return true;
    }

    @Deprecated
    @Override
    public E poll()
    {
        throw new RuntimeException("Unimplemented.");
    }

    @Deprecated
    @Override
    public E peek()
    {
        return container[pointer];
    }

    @Override
    public int size()
    {
        int counter = 0;
        for (E e : container)
        {
            if (e != null)
                counter++;
        }
        return counter;
    }

    @Override
    public boolean contains(Object o)
    {
        return Arrays.asList(container).contains(o);
    }

    private class OurIterator implements Iterator<E>
    {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        public boolean hasNext()
        {
            return cursor != container.length;
        }

        public E next()
        {
            int i = cursor;
            if (i >= container.length)
                throw new NoSuchElementException();
            cursor = i + 1;
            return container[lastRet = i];
        }


    }
}
