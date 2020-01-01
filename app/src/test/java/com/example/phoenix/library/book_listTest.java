package com.example.phoenix.library;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import static org.junit.Assert.*;

public class book_listTest {
    @Test
    public void getStringText() {
        System.out.print(""+new book_list().getStringText());
    }
}