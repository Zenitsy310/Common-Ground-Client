package com.ark_das.springclient;

import org.junit.Test;

import static org.junit.Assert.*;

import android.widget.Toast;

import com.ark_das.springclient.adapter.UserAdapter;
import com.ark_das.springclient.model.Event;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.EventApi;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.ark_das.springclient.ui.UserListActivity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        //assertEquals(4, 2 + 2);


    }
}